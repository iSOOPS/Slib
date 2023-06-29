package com.isoops.slib.utils;

import com.isoops.slib.pojo.IFunction;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

/**
 *
 * 基于hutool的高频方法进行二次封装
 * @author samuel
 */
public class SUtil {

    public static final String EMPTY = "";

    /**
     * object=null / (string="" 且 string=null) / (List=null 且 List.size＜1)
     */
    public static boolean isBlank(Object temp){
        if (temp == null){
            return true;
        }
        if (temp instanceof String){
            if (StringUtils.isBlank((String) temp)){
                return true;
            }
        }
        if (temp instanceof List){
            if (((List<?>) temp).size() < 1){
                return true;
            }
        }
        return false;
    }

    /**
     * object!=null / (string!="" 且 string!=null) / (List!=null 且 List.size≥1)
     */
    public static boolean isNotBlank(Object temp){
        return !isBlank(temp);
    }

    /**
     * [对象值A!=null || 对象值B=null || 对象值C!=null ...]=true
     * ps:存在一个 null 则为 true
     */
    public static boolean isBlank(Object...args){
        for (Object temp:args){
            if (isBlank(temp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * [对象值A=null || 对象值B=null || 对象值C=null ...]=true
     * ps:全部 null 则为 true
     */
    public static boolean isFullBlank(Object...args){
        for (Object temp:args){
            if (isNotBlank(temp)) {
                return false;
            }
        }
        return true;
    }

    /**
     * [对象值A!=null || 对象值B!=null || 对象值C!=null ...]=true
     * ps:不存在 null 则为 true
     */
    public static boolean isNotBlank(Object...args){
        return !isBlank(args);
    }

    /**
     * [对象=null || 对象值A!=null || 对象值B=null || 对象值C!=null ...]=true
     * ps:对象=null || 存在对象值 null 则为 true
     */
    @SafeVarargs
    public static <T,R> boolean isBlank(T object, IFunction<T,R>...args) {
        if (isBlank(object)) {
            return true;
        }
        if (args == null || args.length == 0) {
            return false;
        }
        for (IFunction<T,R> func:args) {
            if (isBlank(func.apply(object))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 若[对象=null || (对象值A=null 且 对象值B=null 且 对象值C=null ...)]=true
     * ps:对象=null || 全部对象值 null 则为 true
     */
    @SafeVarargs
    public static <T,R> boolean isFullBlank(T object, IFunction<T,R>...args) {
        if (isBlank(object)) {
            return true;
        }
        if (args == null || args.length == 0) {
            return false;
        }
        for (IFunction<T,R> func:args) {
            if (isNotBlank(func.apply(object))) {
                return false;
            }
        }
        return true;
    }

    /**
     * [对象!=null 且 对象值A!=null 且 对象值B=null 且 对象值C!=null ...]=true
     * ps:全部不能为空
     */
    @SafeVarargs
    public static <T,R> boolean isNotBlank(T object, IFunction<T,R>...args){
        if (isNotBlank(object)) {
            return true;
        }
        if (args == null || args.length == 0) {
            return false;
        }
        for (IFunction<T,R> func:args) {
            if (isBlank(func.apply(object))) {
                return false;
            }
        }
        return true;
    }

    /**
     * [对象!=null 且 对象值A=value]=true
     */
    public static <T,R> boolean isEqual(T object, IFunction<T,R> func, R value) {
        if (isBlank(object)) {
            return false;
        }
        if (func == null || value == null) {
            return false;
        }
        return func.apply(object).equals(value);
    }


}
