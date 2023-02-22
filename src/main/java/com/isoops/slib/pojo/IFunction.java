package com.isoops.slib.pojo;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 自定义function
 * @author samuel
 */
@FunctionalInterface
public interface IFunction<T, R> extends Function<T, R>, Serializable {
}
