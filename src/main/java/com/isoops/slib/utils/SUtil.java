package com.isoops.slib.utils;

import com.isoops.slib.pojo.IFunction;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 *
 * 基于hutool/StringUtils的高频方法进行二次封装
 * @author samuel
 *
 * @see #isBlank(Object)                        isBlank     空判断,判断范围:null/size/""
 * @see #isBlank(Object...)                     isBlank     空判断,多对象判断,存在空条件
 * @see #isBlank(Object, IFunction[])           isBlank     空判断,对象判断,lambok取值
 * @see #isFullBlank(Object...)                 isFullBlank 空判断,多对象判断,全空条件
 * @see #isFullBlank(Object, IFunction[])       isFullBlank 空判断,对象判断,lambok取值,全空条件
 * @see #isNotBlank(Object)                     isNotBlank  非空判断,为{@link #isBlank(Object)}反义
 * @see #isNotBlank(Object...)                  isNotBlank  非空判断,为{@link #isBlank(Object...)}反义
 * @see #isNotBlank(Object, IFunction[])        isNotBlank  非空判断,为{@link #isBlank(Object, IFunction[])}反义
 * @see #isEqual(Object, IFunction, Object)     isEqual     相等判断
 */

public class SUtil {

    public static final String EMPTY = "";

    /**
     * object=null || (string="" 且 string=null) || (List=null 且 List.size＜1) = true
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
     * object!=null || (string!="" 且 string!=null) || (List!=null 且 List.size≥1)
     */
    public static boolean isNotBlank(Object temp){
        return !isBlank(temp);
    }

    /**
     * [对象值A!=null || 对象值B!=null || 对象值C!=null ...]=true
     * ps:不存在 null 则为 true
     */
    public static boolean isNotBlank(Object...args){
        return !isBlank(args);
    }

    /**
     * [对象!=null 且 对象值A!=null 且 对象值B=null 且 对象值C!=null ...]=true
     * ps:全部不能为空
     */
    @SafeVarargs
    public static <T,R> boolean isNotBlank(T object, IFunction<T,R>...args){
        if (!isNotBlank(object)) {
            return false;
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
