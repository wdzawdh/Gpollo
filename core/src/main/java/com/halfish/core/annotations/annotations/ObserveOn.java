package com.halfish.core.annotations.annotations;

import com.halfish.core.annotations.entity.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cw
 * @date 2017/12/21
 */
@Target(ElementType.METHOD)//限定注解使用的范围
@Retention(RetentionPolicy.CLASS)//限定注解的生命周期
public @interface ObserveOn {
    ThreadMode value();
}
