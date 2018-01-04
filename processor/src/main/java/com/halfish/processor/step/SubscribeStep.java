package com.halfish.processor.step;

import com.halfish.core.annotations.annotations.SubscribeOn;
import com.halfish.processor.GpolloDescriptor;
import com.halfish.processor.GpolloProcessor;
import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

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
public class SubscribeStep implements BasicAnnotationProcessor.ProcessingStep {
    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(SubscribeOn.class);
    }

    @Override
    public Set<? extends Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Map.Entry<Class<? extends Annotation>, Collection<Element>> classCollectionEntry : elementsByAnnotation.asMap().entrySet()) {
            for (Element element : classCollectionEntry.getValue()) {
                if (MoreElements.isAnnotationPresent(element, SubscribeOn.class)) {
                    GpolloDescriptor gpolloDescriptor = GpolloProcessor.sDescriptorMap.get(element);
                    gpolloDescriptor.subscribeOn = MoreElements.asExecutable(element).getAnnotation(SubscribeOn.class).value();
                }
            }
        }
        return new HashSet<>();
    }
}
