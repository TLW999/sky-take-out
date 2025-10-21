package com.sky.anno;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义公共字段自动填充注解
 */
@Target(ElementType.METHOD) //定义注解的的范围
@Retention(RetentionPolicy.RUNTIME)//定义注解的生命周期
public @interface AutoFill {
    OperationType value();
}
