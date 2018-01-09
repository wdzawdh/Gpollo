package com.halfish.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.halfish.processor.step.ObserveStep;
import com.halfish.processor.step.ReceiveStep;
import com.halfish.processor.step.SubscribeStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

/**
 * @author cw
 * @date 2017/12/18
 */
@AutoService(Processor.class)
public class GpolloProcessor extends BasicAnnotationProcessor {

    public static Map<Element, GpolloDescriptor> sDescriptorMap = new HashMap<>();
    private boolean mGenerated = false;
    private String mModuleName;

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        Map<String, String> options = processingEnv.getOptions();
        if (options != null && !options.isEmpty()) {
            mModuleName = options.get("gModuleName");
        }
        if (mModuleName == null) {
            throw new RuntimeException("gradle must config arguments = [gModuleName: \"XXX\"]");
        }
        return ImmutableSet.of(new ReceiveStep(), new ObserveStep(), new SubscribeStep());
    }

    @Override
    protected void postRound(RoundEnvironment roundEnv) {
        if (mGenerated && sDescriptorMap.isEmpty()) {
            return;
        }
        CodeGenerator.create(new ArrayList<>(sDescriptorMap.values()), processingEnv.getFiler(), mModuleName).createJavaFile();
        mGenerated = true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        //指定你使用的Java版本
        return SourceVersion.latestSupported();
    }
}
