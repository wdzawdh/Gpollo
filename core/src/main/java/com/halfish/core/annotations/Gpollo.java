package com.halfish.core.annotations;

import com.halfish.core.annotations.contrace.GpolloBinder;
import com.halfish.core.annotations.contrace.GpolloBinderGenerator;
import com.halfish.core.annotations.entity.Event;
import com.halfish.core.annotations.entity.GpolloBinderImpl;
import com.halfish.core.annotations.entity.SchedulerProvider;

import java.lang.reflect.Method;

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

    private final Subject<Object, Object> mBus;
    private GpolloBinderGenerator mGenerator;
    private SchedulerProvider mSchedulerProvider;

    private Gpollo() {
        mBus = new SerializedSubject<>(PublishSubject.create());
        generator();
    }

    @SuppressWarnings("unchecked")
    private void generator() {
        try {
            Class<GpolloBinderGenerator> generatorClass = (Class<GpolloBinderGenerator>) Class.forName("com.halfish.gpollo.generate.GpolloBinderGeneratorImpl");
            Method method = generatorClass.getMethod("instance");
            mGenerator = (GpolloBinderGenerator) method.invoke(null);
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

    public static void init(Scheduler scheduler) {
        Gpollo.getDefault().mSchedulerProvider = SchedulerProvider.create(scheduler);
    }

    public static SchedulerProvider getSchedulerProvider() {
        return Gpollo.getDefault().mSchedulerProvider;
    }

    public GpolloBinder bind(Object o) {
        if (null == o) {
            throw new NullPointerException("object to subscribe must not be null");
        }
        if (mGenerator == null) {
            return new GpolloBinderImpl();
        }
        return mGenerator.generate(o);
    }

    public void unBind(GpolloBinder bind) {
        if (bind != null) {
            bind.unbind();
        }
    }

    public void post(String tag) {
        mBus.onNext(new Event(tag, null));
    }

    public void post(String tag, Object actual) {
        mBus.onNext(new Event(tag, actual));
    }

    public <T> rx.Observable<T> toObservable(final String[] tags, final Class<T> eventType) {
        return mBus.filter(new Func1<Object, Boolean>() {
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
