package com.halfish.core.annotations;

import com.halfish.core.annotations.contrace.GpolloBinder;
import com.halfish.core.annotations.contrace.GpolloBinderGenerator;
import com.halfish.core.annotations.entity.Event;
import com.halfish.core.annotations.entity.GpolloBinderImpl;
import com.halfish.core.annotations.entity.SchedulerProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import rx.Scheduler;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * @author cw
 * @date 2017/12/20
 */
public class Gpollo {

    private static volatile Gpollo mInstance;
    private static String[] sModules = new String[]{};
    private static String sMainModule;

    private final Subject<Object, Object> mBus;
    private Map<String, GpolloBinderGenerator> mGeneratorMap = new HashMap<>();
    private SchedulerProvider mSchedulerProvider;

    private Gpollo() {
        mBus = new SerializedSubject<>(PublishSubject.create());
        for (String model : sModules) {
            generator(model);
        }
        generator(sMainModule);
    }

    @SuppressWarnings("unchecked")
    private void generator(String modelName) {
        try {
            Class<GpolloBinderGenerator> generatorClass = (Class<GpolloBinderGenerator>) Class.forName("com.halfish.gpollo.generate.GpolloBinderGeneratorImpl_" + modelName);
            Method method = generatorClass.getMethod("instance");
            GpolloBinderGenerator mGenerator = (GpolloBinderGenerator) method.invoke(null);
            mGeneratorMap.put(modelName, mGenerator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Gpollo getDefault() {
        if (mInstance == null) {
            synchronized (Gpollo.class) {
                if (mInstance == null) {
                    mInstance = new Gpollo();
                }
            }
        }
        return mInstance;
    }

    public static void init(Scheduler scheduler, String mainModule, String... modules) {
        if (mainModule == null || mainModule.length() == 0) {
            throw new RuntimeException("mainModule must has");
        }
        sMainModule = mainModule;
        sModules = modules;
        Gpollo.getDefault().mSchedulerProvider = SchedulerProvider.create(scheduler);
    }

    public static SchedulerProvider getSchedulerProvider() {
        if (Gpollo.getDefault().mSchedulerProvider == null) {
            throw new RuntimeException("Gpollo must be init");
        }
        return Gpollo.getDefault().mSchedulerProvider;
    }

    public static GpolloBinder bind(Object o) {
        if (null == o) {
            throw new RuntimeException("object to subscribe must not be null");
        }
        GpolloBinderGenerator generator = null;
        String packageName = o.getClass().getPackage().getName();
        for (String module : sModules) {
            if (packageName.contains("." + module)) {
                generator = Gpollo.getDefault().mGeneratorMap.get(module);
                break;
            }
        }
        if (generator == null) {
            generator = Gpollo.getDefault().mGeneratorMap.get(sMainModule);
        }
        if (generator == null) {
            return new GpolloBinderImpl();
        }
        return generator.generate(o);
    }

    public static void unBind(GpolloBinder bind) {
        if (bind != null) {
            bind.unbind();
        }
    }

    public static void post(String tag) {
        Gpollo.getDefault().mBus.onNext(new Event(tag, null));
    }

    public static void post(String tag, Object actual) {
        Gpollo.getDefault().mBus.onNext(new Event(tag, actual));
    }

    public static <T> rx.Observable<T> toObservable(final String[] tags, final Class<T> eventType) {
        return Gpollo.getDefault().mBus.filter(new Func1<Object, Boolean>() {
            @Override
            public Boolean call(Object o) {
                return Event.class.isInstance(o);
            }
        }).cast(Event.class).filter(new Func1<Event, Boolean>() {
            @Override
            public Boolean call(Event event) {
                for (String tag : tags) {
                    if (tag.equals(event.getTag())) {
                        return eventType.isInstance(event.getData()) || event.getData() == null;
                    }
                }
                return false;
            }
        }).map(new Func1<Event, T>() {
            @Override
            public T call(Event event) {
                Object data = event.getData();
                if (data != null) {
                    return (T) data;
                } else {
                    return null;
                }
            }
        });
    }
}