package com.halfish.processor.step;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.halfish.core.annotations.annotations.Receive;
import com.halfish.processor.GpolloDescriptor;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * @author cw
 * @date 2017/12/18
 */
public class ReceiveStep implements BasicAnnotationProcessor.ProcessingStep {

    private Map<Element, GpolloDescriptor> mDescriptorMap;

    public ReceiveStep(Map<Element, GpolloDescriptor> descriptorMap) {
        this.mDescriptorMap = descriptorMap;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(Receive.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Map.Entry<Class<? extends Annotation>, Collection<Element>> classCollectionEntry : elementsByAnnotation.asMap().entrySet()) {
            for (Element element : classCollectionEntry.getValue()) {
                GpolloDescriptor descriptor = new GpolloDescriptor((ExecutableElement) element);
                ExecutableElement executableElement = MoreElements.asExecutable(element);
                Receive annotation = executableElement.getAnnotation(Receive.class);
                descriptor.tags = Arrays.asList(annotation.value());
                mDescriptorMap.put(element, descriptor);
            }
        }
        return new HashSet<>();
    }
}
