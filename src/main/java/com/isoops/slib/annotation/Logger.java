package com.isoops.slib.annotation;


import java.lang.annotation.*;

/**
 * 日志通用注解
 * @author samuel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Logger {

    /**
     * 日志范围
     */
    LoggerEnum logType() default LoggerEnum.ALL;

    /**
     * 自定义输出
     */
    String msg() default "controller";

    /**
     * 需要打印请求的对象
     */
    Class<Object> clazz() default Object.class;
}
