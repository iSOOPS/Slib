package com.isoops.slib.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.isoops.slib.utils.SObjectUtil;
import com.isoops.slib.utils.SUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author samuel
 */
public class SuperDaoServiceImpl {

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

    @SafeVarargs
    protected final <T> QueryWrapper<T> queryPage(
            T val,
            List<SFunction<T, ?>> likeColumns,
            SFunction<T, ?>... gets) {

        String[] filedNames = SObjectUtil.getFiledName(val);
        List<String> likeColumnString = new ArrayList<>();
        for (SFunction<T, ?> fn : likeColumns) {
            String cn = SObjectUtil.getFunctionFiledName(fn);
            likeColumnString.add(cn);
        }
        List<String> eqFiled = SObjectUtil.disposeSetList(Arrays.asList(filedNames),likeColumnString, SObjectUtil.SETTYPE.DISJUNCTION);

        QueryWrapper<T> queryWrapper = new QueryWrapper<>();

        Map<String,Object> queryMap = new HashMap<>();
        for (String filed : eqFiled) {
            Object object = SObjectUtil.getFieldValueByName(filed,val);
            if (object != null && !"transMap".equals(filed)) {
                queryMap.put(humpToUnderline(filed),object);
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

    @SafeVarargs
    protected final <T> List<SFunction<T, ?>> listColumns(SFunction<T, ?>... gets) {
        return Arrays.asList(gets);
    }

    public static String humpToUnderline(String str) {
        String regex = "([A-Z])";
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) {
            String target = matcher.group();
            str = str.replaceAll(target, "_"+target.toLowerCase());
        }
        return str;
    }

}
