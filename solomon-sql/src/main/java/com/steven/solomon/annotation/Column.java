package com.steven.solomon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    /**
     * 数据库字段名
     */
    String name() default "";

    /**
     * 数据库字段分割符 默认"_"
     * @return
     */
    String regex() default "_";
}