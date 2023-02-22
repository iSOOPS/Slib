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
     * 批量兼容性非空判断(String/List/VO/DTO/DO)
     * @param args 对象
     * @return bool
     */
    public static boolean isNotBlank(Object...args){
        return !isBlank(args);
    }

    @SafeVarargs
    public static <T,R> boolean isNotBlank(T object, IFunction<T,R>...args){
        return !isBlank(object,args);
    }

    /**
     * 批量兼容性非空判断(String/List/VO/DTO/DO)
     * @param args 对象
     * @return bool
     */
    public static boolean isBlank(Object...args){
        for (Object temp:args){
            if (temp == null){
                return true;
            }
            if (temp instanceof String){
                if (StringUtils.isBlank((String) temp)){
                    return true;
                }
            }
            else if (temp instanceof List){
                if (((List<?>) temp).size() < 1){
                    return true;
                }
            }
        }
        return false;
    }

    @SafeVarargs
    public static <T,R> boolean isBlank(T object, IFunction<T,R>...args) {
        if (object == null) {
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
     * 数组去重
     * @param list 数组
     * @param <T> 对象类型
     * @return f
     */
    public static <T> List<T> outDuplicate(List<T> list){
        LinkedHashSet<T> temp = new LinkedHashSet<>(list);
        return new ArrayList<>(temp);
    }
}
