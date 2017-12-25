package com.halfish.processor;

import com.halfish.core.annotations.entity.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

/**
 * @author cw
 * @date 2017/12/18
 */
public class GpolloDescriptor {

    public List<String> tags = new ArrayList<>();
    public ThreadMode observeOn = ThreadMode.MAIN;
    public ThreadMode subscribeOn = ThreadMode.IO;
    public ExecutableElement methodElement;

    public GpolloDescriptor(ExecutableElement element) {
        methodElement = element;
    }
}
