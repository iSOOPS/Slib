package com.isoops.slib.support;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.isoops.slib.pojo.IFunction;
import com.isoops.slib.utils.SObjectUtil;
import com.isoops.slib.utils.SUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DAO层,MybatisPlus通用实现方法
 * @author samuel
 */
public class SuperDaoServiceImpl {

    /**
     * 单字段查询
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
     * 单字段查询
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

        String[] filedNames = SObjectUtil.getFiledsNames(val);
        List<String> likeColumnString = new ArrayList<>();
        for (SFunction<T, ?> fn : likeColumns) {
            String cn = SObjectUtil.getFunctionFiledName(fn);
            likeColumnString.add(cn);
        }
        List<String> eqFiled = SObjectUtil.disposeSetList(Arrays.asList(filedNames),likeColumnString, SObjectUtil.SETTYPE.DISJUNCTION);

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        Map<String,Object> queryMap = new HashMap<>();
        for (String filed : eqFiled) {
            Object object = SObjectUtil.getValueByName(filed,val);
            if (object != null && !"transMap".equals(filed)) {
                queryMap.put(StrUtil.toCamelCase(filed),object);
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
