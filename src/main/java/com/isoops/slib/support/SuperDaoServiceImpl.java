package com.isoops.slib.support;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.isoops.slib.utils.SBeanUtil;
import com.isoops.slib.utils.SFieldUtil;
import com.isoops.slib.utils.SUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * DAO层,MybatisPlus通用实现方法
 * @author samuel
 * *Menu*
 * @see #querys(SFunction, List, SFunction[])       单字段查询,多数据匹配
 * @see #query(SFunction, Object, SFunction[])      单字段查询,单数据匹配
 * @see #queryPage(Object, List, SFunction[])       分页查询
 */
public class SuperDaoServiceImpl {

    /**
     * 单字段查询,多数据匹配
     * @param column 字段Func
     * @param vals 字段值(单个则为eq,多个则为in)
     * @param gets 需要获取的字段
     * @param <T> 对象类型
     * @return 查询对象
     */
    @SafeVarargs
    protected final <T> QueryWrapper<T> querys(SFunction<T, ?> column, List<?> vals, SFunction<T, ?>... gets) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (SUtil.isNotBlank(column,vals)) {
            if (vals.size() > 1){
                queryWrapper.lambda().in(column,vals);
            }
            else {
                queryWrapper.lambda().eq(column,vals.get(0));
            }
        }
        if (null != gets){
            queryWrapper.lambda().select(gets);
        }
        return queryWrapper;
    }

    /**
     * 单字段查询,单数据匹配
     * @param column 字段Func
     * @param val 字段值
     * @param gets 需要获取的字段
     * @param <T> 对象类型
     * @return 查询对象
     */
    @SafeVarargs
    protected final <T> QueryWrapper<T> query(SFunction<T, ?> column, Object val, SFunction<T, ?>... gets) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (SUtil.isNotBlank(column,val)) {
            queryWrapper.lambda().eq(column,val);
        }
        if (null != gets){
            queryWrapper.lambda().select(gets);
        }
        return queryWrapper;
    }

    /**
     * 分页查询
     * @param val 条件对象
     * @param likeColumns 使用like的字段,其余默认eq
     * @param gets 查询的字段
     * @param <T> 查询对象类型
     * @return 查询对象
     */
    @SafeVarargs
    protected final <T> QueryWrapper<T> queryPage(T val, List<SFunction<T, ?>> likeColumns, SFunction<T, ?>... gets) {

        String[] filedNames = SFieldUtil.getFiledsNames(val.getClass());
        List<String> likeColumnString = new ArrayList<>();
        for (SFunction<T, ?> fn : likeColumns) {
            String funcName = SFieldUtil.getFunctionName(fn);
            likeColumnString.add(StrUtil.toUnderlineCase(funcName));
        }
        List<String> eqFiled = SBeanUtil.disposeSetList(Arrays.asList(filedNames),likeColumnString, SBeanUtil.SETTYPE.DISJUNCTION);

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        Map<String,Object> queryMap = new HashMap<>();
        for (String filed : eqFiled) {
            Field field = SFieldUtil.getFiled(val.getClass(),filed);
            if (field == null) {
                continue;
            }
            Method method = SFieldUtil.getMethodByField(field,val.getClass(),false);
            if (method == null) {
                continue;
            }
            Object object;
            try {
                object = method.invoke(val);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (object != null && !"transMap".equals(filed)) {
                queryMap.put(StrUtil.toUnderlineCase(filed),object);
            }
        }
        queryWrapper.allEq(queryMap,true);

        if (SUtil.isNotBlank(likeColumns)){
            for (SFunction<T,?> function : likeColumns){
                Object getVal = function.apply(val);
                if (getVal!=null) {
                    queryWrapper.lambda().like(function,getVal);
                }
            }
        }

        if (null != gets){
            queryWrapper.lambda().select(gets);
        }
        return queryWrapper;
    }

    /**
     * args转数组
     * @param gets args funcions
     * @param <T> 对象类型
     * @return 数组
     */
    @SafeVarargs
    protected final <T> List<SFunction<T, ?>> listColumns(SFunction<T, ?>... gets) {
        return Arrays.asList(gets);
    }



}
