package com.cw.processor;

import com.cw.core.annotations.entity.BackpressureMode;
import com.cw.core.annotations.entity.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;

/**
 * @author cw
 * @date 2017/12/18
 */
class GpolloDescriptor {

    List<String> tags = new ArrayList<>();
    boolean canReceiveNull = false;
    ThreadMode observeOn = ThreadMode.MAIN;
    ThreadMode subscribeOn = ThreadMode.MAIN;
    BackpressureMode backpressure = BackpressureMode.NORMAL;
    ExecutableElement methodElement;

    GpolloDescriptor(ExecutableElement element) {
        methodElement = element;
    }
}
