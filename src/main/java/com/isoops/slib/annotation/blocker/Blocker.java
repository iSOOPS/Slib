package com.isoops.slib.annotation.blocker;

import com.isoops.slib.annotation.logger.LogEnum;

import java.lang.annotation.*;

/**
 * 自定义鉴权注解
 * @author samuel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Blocker {

    /**
     * 日志级别
     */
    LogEnum logLevel() default LogEnum.None;
}
