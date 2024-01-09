package com.isoops.slib.utils;

import cn.hutool.core.collection.CollUtil;
import com.isoops.slib.annotation.SFieldAlias;
import com.isoops.slib.common.SAliasBeanBasic;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.pojo.BeanCopierUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 对象操作工具类 Clone/List操作等 适用与DDD转化
 * @author samuel
 *
 * @see #clone(Object, Object)                      clone               克隆,对象与对象
 * @see #clone(Object, Class)                       clone               克隆,复制克隆
 * @see #clones(List, Class)                        clones              克隆,复制克隆
 * @see #clones(List, Class, Integer)               clones              克隆,Domain定向深克隆,类型见{@link com.isoops.slib.pojo.CloneDirection}
 * @see #aliasClone(Object, Object)                 aliasClone          别名克隆,对象与对象,支持克隆对象增加别名注解{@link SFieldAlias}
 * @see #aliasClone(Object, Class)                  aliasClone          别名克隆,复制克隆,支持克隆对象增加别名注解{@link SFieldAlias}
 * @see #aliasClones(List, Class)                   aliasClones         别名克隆,对象与对象,支持克隆对象增加别名注解{@link SFieldAlias}
 * @see #methodOfAlias(Object, Object)              methodOfAlias       别名操作,仅操作注解的别名复制
 *
 * @see #foreach(List, Function)                    foreach             数组重组,指定数组里对象的某一个feild,取出重组一个数组
 * @see #foreachByKey(List, Function, Object)       foreachByKey        数组重组,获取数组中对象的某个值,将满足条件的值,重组成一个新数组
 * @see #foreachByListKey(List, Function, List)     foreachByListKey    数组重组,获取数组中对象的某个值,将满足条件的值,重组成一个新数组
 * @see #foreachByNotKey(List, Function, Object)    foreachByNotKey     数组重组,获取数组中对象的某个值,将满足条件的值,重组成一个新数组
 * @see #foreachByListNotKey(List, Function, List)  foreachByListNotKey 数组重组,获取数组中对象的某个值,将满足条件的值,重组成一个新数组
 *
 * @see #disposeSetList(List, List, SETTYPE)        disposeSetList      数组重组,获取2个数组的差/交/并集
 * @see #outDuplicate(List)                         outDuplicate        数组重组,数组去重
 */
public class SBeanUtil extends SAliasBeanBasic {

    public static <T,R>  List<T> foreachCustom(List<T> list,
                                               Function<T,R> function,
                                               List<R> values,
                                               Boolean isEquals){
        if (SUtil.isBlank(list,function,values)){
            return new ArrayList<>();
        }
        List<T> res = new ArrayList<>();
        for (T object : list){
            R r = function.apply(object);
            if (SUtil.isBlank(r)){
                continue;
            }
            for (R r1 : values){
                if ((isEquals && r.equals(r1)) || (!isEquals && !r.equals(r1))) {
                    res.add(object);
                }
            }
        }
        return res;
    }


    /**
     * 克隆
     * example: T=SBeanUtil.clone(R,T)
     */
    public static <R,T> T clone(R origin,T target) {
        if (SUtil.isBlank(origin,target)) {
            return target;
        }
        BeanCopierUtils.copyProperties(origin, target);
        return target;
    }

    /**
     * 克隆
     * example: T=clone(R,T.class)
     */
    public static <R,T> T clone(R origin,Class<T> clazz) {
        return clone(origin,createTarget(clazz));
    }

    /**
     * 克隆
     * example: List[T]=clones(List[R],T.class)
     */
    public static <R,T> List<T> clones(List<R> origins, Class<T> clazz) {
        if(origins == null || origins.size() < 1) {
            return new ArrayList<>();
        }
        List<T> targets = new ArrayList<>();
        for(R orig : origins) {
            targets.add(clone(orig,clazz));
        }
        return targets;
    }

    /**
     * 深度克隆 domain 定向(内部对象也会克隆)
     * example: List[T]=clones(List[T],T.class,CloneDirection)
     * desc: 类型见{@link com.isoops.slib.pojo.CloneDirection}
     */
    public static <R extends AbstractObject,T> List<T> clones(List<R> origins, Class<T> clazz,
                                                              Integer direction){
        if(origins == null || origins.size() < 1) {
            return new ArrayList<>();
        }
        List<T> targets = new ArrayList<>();
        for(R orig : origins) {
            try {
                targets.add(orig.clone(clazz,direction));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return targets;
    }

    /**
     * 别名克隆
     * example: T=aliasClone(R,T)
     * desc: 该克隆支持SFieldAlias注解,可以给field增加别名alias,克隆后可匹配别名写入
     */
    public static <T,V> V aliasClone(T origin,V target) {
        return methodOfAlias(origin,clone(origin,target));
    }

    /**
     * 别名克隆
     * example: T=aliasClone(R,T.class)
     * desc: 该克隆支持SFieldAlias注解,可以给field增加别名alias,克隆后可匹配别名写入
     */
    public static <T,V> V aliasClone(T origin,Class<V> clazz) {
        return methodOfAlias(origin,clone(origin,clazz));
    }

    /**
     * 别名克隆
     * example: List[T]=aliasClones(List[R],T.class)
     * desc: 该克隆支持SFieldAlias注解,可以给field增加别名alias,克隆后可匹配别名写入
     */
    public static <R,T> List<T> aliasClones(List<R> origins, Class<T> clazz) {
        if(origins == null || origins.size() < 1) {
            return new ArrayList<>();
        }
        List<T> targets = new ArrayList<>();
        for(R orig : origins) {
            targets.add(aliasClone(orig,clone(orig,clazz)));
        }
        return targets;
    }

    /**
     * 别名操作
     * example: T=methodOfAlias(R,T)
     * desc: 仅操作注解的别名复制
     */
    public static <T,R> T methodOfAlias(R origin,T target) {
        Map<String, Object> originMap = new HashMap<>(16);
        Map<String, Object> originAliasMap = new HashMap<>(16);
        //生成源bean的属性及其值的字典
        SAliasBeanBasic.generateOriginFieldWithValue(origin, originMap, originAliasMap);

        T newTarget = clone(origin,target);

        //设置目标bean的属性值
        SAliasBeanBasic.settingTargetFieldWithValue(newTarget, originMap, originAliasMap);
        return target;
    }

    /**
     * 数组重组
     * example: List[R]=foreach(List[T],T::R)
     * desc: 指定数组里对象的某一个feild,取出重组一个数组
     */
    public static <T,R>  List<R> foreach(List<T> list, Function<T,R> function){
        if (SUtil.isBlank(list,function)){
            return new ArrayList<>();
        }
        List<R> res = new ArrayList<>();
        for (T object : list){
            R r = function.apply(object);
            if (SUtil.isNotBlank(r)){
                res.add(r);
            }
        }
        return res;
    }

    /**
     * 重组数组
     * example: List[T]=foreachByKey(List[T],T::R,R)
     * desc: 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
     */
    public static <T,R>  List<T> foreachByKey(List<T> list, Function<T,R> function, R value){
        return foreachCustom(list,function, Collections.singletonList(value),true);
    }

    /**
     * 重组数组
     * example: List[T]=foreachByKey(List[T],T::R,List[R])
     * desc: 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
     */
    public static <T,R>  List<T> foreachByListKey(List<T> list, Function<T,R> function, List<R> values){
        return foreachCustom(list,function,values,true);
    }

    /**
     * 重组数组
     * example: List[T]=foreachByKey(List[T],T::R,R)
     * desc: 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
     */
    public static <T,R>  List<T> foreachByNotKey(List<T> list, Function<T,R> function, R value){
        return foreachCustom(list,function, Collections.singletonList(value),false);
    }

    /**
     * 重组数组
     * example: List[T]=foreachByKey(List[T],T::R,List[R])
     * desc: 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
     */
    public static <T,R>  List<T> foreachByListNotKey(List<T> list, Function<T,R> function, List<R> values){
        return foreachCustom(list,function,values,false);
    }

    public enum SETTYPE {
        /**
         * 差集
         */
        DISJUNCTION,
        /**
         * 交集
         */
        INTERSECTION,
        /**
         * 并集
         */
        UNION
    }

    /**
     * 获取2个数组的差/交/并集
     * example: List[T]=disposeSetList(List[T],List[T],SBeanUtil.SETTYPE)
     */
    public static <T> List<T> disposeSetList(List<T> sou1, List<T> sou2, SBeanUtil.SETTYPE type){
        switch (type){
            case UNION: return (List<T>) CollUtil.union(sou1, sou2);
            case INTERSECTION: return (List<T>) CollUtil.intersection(sou1, sou2);
            case DISJUNCTION: return (List<T>) CollUtil.disjunction(sou1, sou2);
            default: return new ArrayList<>();
        }
    }

    /**
     * 数组去重
     */
    public static <T> List<T> outDuplicate(List<T> list){
        LinkedHashSet<T> temp = new LinkedHashSet<>(list);
        return new ArrayList<>(temp);
    }
}
