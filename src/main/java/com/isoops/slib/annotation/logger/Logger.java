package com.isoops.slib.annotation.logger;


import java.lang.annotation.*;

/**
 * 自定义日志注解
 * @author samuel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Logger {

    /**
     * 日志范围
     */
    LogEnum level() default LogEnum.Full;

}
