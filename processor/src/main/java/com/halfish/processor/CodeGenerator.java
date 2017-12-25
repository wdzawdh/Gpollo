package com.halfish.processor;

import com.halfish.core.annotations.Gpollo;
import com.halfish.core.annotations.contrace.GpolloBinder;
import com.halfish.core.annotations.contrace.GpolloBinderGenerator;
import com.halfish.core.annotations.entity.GpolloBinderImpl;
import com.halfish.core.annotations.entity.ThreadMode;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import rx.functions.Action1;

/**
 * @author cw
 * @date 2017/12/18
 */
class CodeGenerator {

    private static final String GENERATE_PACKAGE_NAME = "com.halfish.gpollo.generate";
    private static final String GENERATE_CLASS_NAME = "GpolloBinderGeneratorImpl";
    private static final String GPOLLO_BINDER_NAME = "gpolloBinder";
    private static final String ACTION1_CALL_PARAM = "callParam";
    private static final String GENERATE_PARAM = "bindObject";

    private List<GpolloDescriptor> mGpolloDescriptors;
    private Filer mFiler;

    private CodeGenerator(ArrayList<GpolloDescriptor> gpolloDescriptors, Filer filer) {
        this.mGpolloDescriptors = gpolloDescriptors;
        this.mFiler = filer;
    }

    static CodeGenerator create(ArrayList<GpolloDescriptor> gpolloDescriptors, Filer filer) {
        return new CodeGenerator(gpolloDescriptors, filer);
    }

    void createJavaFile() {
        try {
            getBinderGeneratorJavaFile().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JavaFile getBinderGeneratorJavaFile() {
        return JavaFile.builder(GENERATE_PACKAGE_NAME, getGeneratorTypeSpec())
                .addStaticImport(ThreadMode.MAIN)
                .addStaticImport(ThreadMode.IO)
                .addStaticImport(ThreadMode.IMMEDIATE)
                .addStaticImport(ThreadMode.NEWTHREAD)
                .build();
    }

    //----------------------------------------------------------------------------------------------

    /**
     * GpolloBinderGeneratorImpl.class
     */
    private TypeSpec getGeneratorTypeSpec() {
        return TypeSpec.classBuilder(GENERATE_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(GpolloBinderGenerator.class)
                .addField(getSingleInstanceFileSpec())
                .addMethod(getSingleInstanceMethodSpec())
                .addMethod(getGenerateFunctionMethodSpec())
                .build();
    }

    /**
     * private static GpolloBinderGenerator sInstance;
     */
    private FieldSpec getSingleInstanceFileSpec() {
        return FieldSpec.builder(GpolloBinderGenerator.class, "instance", Modifier.PRIVATE, Modifier.STATIC)
                .build();
    }

    /**
     * public static synchronized GpolloBinderGenerator instance() {
     * if (null == sInstance) {
     * sInstance = new GpolloBinderGenerator();
     * }
     * return sInstance;
     * }
     */
    private MethodSpec getSingleInstanceMethodSpec() {
        return MethodSpec.methodBuilder("instance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.SYNCHRONIZED)
                .returns(GpolloBinderGenerator.class)
                .beginControlFlow("if (instance == null)")
                .addStatement("instance = new " + GENERATE_CLASS_NAME + "()")
                .endControlFlow()
                .addStatement("return instance")
                .build();
    }

    /**
     * public GpolloBinder generate(final Object bindObject) {...}
     */
    private MethodSpec getGenerateFunctionMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("generate")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(GpolloBinder.class)
                .addParameter(Object.class, GENERATE_PARAM, Modifier.FINAL)
                .addStatement("$T " + GPOLLO_BINDER_NAME + " = new $T()", GpolloBinderImpl.class, GpolloBinderImpl.class);

        if (mGpolloDescriptors != null) {
            for (GpolloDescriptor gpolloDescriptor : mGpolloDescriptors) {
                getSingleBinderStatement(builder, gpolloDescriptor);
            }
        }
        return builder.addStatement("return " + GPOLLO_BINDER_NAME).build();
    }

    /**
     * GpolloBinderImpl gpolloBinder = new GpolloBinderImpl();
     * if (MainActivity.class.isAssignableFrom(bindObject.getClass())) {
     * gpolloBinder.add(Gpollo.getDefault().toObservable(new String[]{...}, ....class).subscribe(new Action1<...>() {
     * ......
     * }
     */
    private void getSingleBinderStatement(MethodSpec.Builder builder, GpolloDescriptor gpolloDescriptors) {
        List<? extends VariableElement> parameters = gpolloDescriptors.methodElement.getParameters();
        if (parameters.size() != 1) return;
        TypeMirror typeMirror = parameters.get(0).asType();
        Class<?> eventType = GpollpUtil.parseVariableClass(typeMirror);
        ThreadMode subscribeOn = gpolloDescriptors.subscribeOn;
        ThreadMode observeOn = gpolloDescriptors.observeOn;
        String methodName = gpolloDescriptors.methodElement.getSimpleName().toString();
        String clazzType = gpolloDescriptors.methodElement.getEnclosingElement().asType().toString().replaceAll("<.*>", "");
        builder.beginControlFlow("if (" + clazzType + ".class.isAssignableFrom(" + GENERATE_PARAM + ".getClass()))")
                .addStatement(GPOLLO_BINDER_NAME + ".add($T.getDefault().toObservable(new String[]{" + GpollpUtil.split(gpolloDescriptors.tags, ",") + "}, $T.class)" + getSubscribeOnMethodCode(subscribeOn) + getObserveOnMethodCode(observeOn) + ".subscribe(new $T<$T>() {" + getOnAction1MethodCode(eventType, clazzType, methodName) + "}));", Gpollo.class, eventType, Action1.class, eventType)
                .endControlFlow();
    }

    /**
     * .subscribeOn(Gpollo.getSchedulerProvider().get(ThreadMode.IO))
     */
    private String getSubscribeOnMethodCode(ThreadMode subscribeOn) {
        return ".subscribeOn(Gpollo.getSchedulerProvider().get(" + subscribeOn.name() + "))";
    }

    /**
     * .observeOn(Gpollo.getSchedulerProvider().get(ThreadMode.MAIN))
     */
    private String getObserveOnMethodCode(ThreadMode observeOn) {
        return ".observeOn(Gpollo.getSchedulerProvider().get(" + observeOn.name() + "))";
    }

    /**
     * public void call(java.util.ArrayList callParam) {...}
     */
    private MethodSpec getOnAction1MethodCode(Class<?> eventType, String clazzType, String methodName) {
        return MethodSpec.methodBuilder("call")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(eventType, ACTION1_CALL_PARAM)
                .addCode(getCallMethodCode(clazzType, methodName))
                .build();
    }

    /**
     * MainActivity subscribe = (com.cw.gpollo.MainActivity) bindObject;
     * subscribe.add(callParam);
     */
    private CodeBlock getCallMethodCode(String clazzType, String methodName) {
        return CodeBlock.builder()
                .addStatement(clazzType + " subscribe = (" + clazzType + ") " + GENERATE_PARAM)
                .addStatement("subscribe." + methodName + "(" + ACTION1_CALL_PARAM + ")").build();
    }
}
