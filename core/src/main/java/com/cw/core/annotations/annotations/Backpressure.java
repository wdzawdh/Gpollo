package com.cw.core.annotations.annotations;

import com.cw.core.annotations.entity.BackpressureMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cw
 * @date 2018/3/23
 */
@Target(ElementType.METHOD)//限定注解使用的范围
@Retention(RetentionPolicy.RUNTIME)//限定注解的生命周期
public @interface Backpressure {
    BackpressureMode value();
}
