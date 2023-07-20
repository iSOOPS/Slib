package com.isoops.slib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SFieldAlias {

    /**
     * 在即将被拷贝的属性上面,设置目标属性名
     */
    String targetName() default "";

    /**
     * 在即将拷贝至改属性上面,设置源属性名
     */
    String originName() default "";

}
