package com.isoops.slib.pojo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SFieldAlias {

    /**
     * 设置目标/源属性名
     */
    String name() default "";

    /**
     * 设置目标/源属性名组
     */
    String[] names() default "";
}
