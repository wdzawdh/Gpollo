package com.halfish.processor.step;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.halfish.core.annotations.annotations.ObserveOn;
import com.halfish.processor.GpolloDescriptor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;

/**
 * @author cw
 * @date 2017/12/21
 */
public class ObserveStep implements BasicAnnotationProcessor.ProcessingStep {

    private Map<Element, GpolloDescriptor> mDescriptorMap;

    public ObserveStep(Map<Element, GpolloDescriptor> descriptorMap) {
        this.mDescriptorMap = descriptorMap;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(ObserveOn.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Map.Entry<Class<? extends Annotation>, Collection<Element>> classCollectionEntry : elementsByAnnotation.asMap().entrySet()) {
            for (Element element : classCollectionEntry.getValue()) {
                if (MoreElements.isAnnotationPresent(element, ObserveOn.class)) {
                    GpolloDescriptor gpolloDescriptor = mDescriptorMap.get(element);
                    gpolloDescriptor.observeOn = MoreElements.asExecutable(element).getAnnotation(ObserveOn.class).value();
                }
            }
        }
        return new HashSet<>();
    }
}
