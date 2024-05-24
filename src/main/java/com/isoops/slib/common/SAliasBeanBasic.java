package com.isoops.slib.common;

import com.isoops.slib.utils.SFieldUtil;
import com.isoops.slib.utils.SUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SAliasBeanBasic extends SBeanBasic {

    protected static List<String> aliasNames(Field field) {
        String name = getAnnotationOriginName(field);
        String[] names = getAnnotationOriginNames(field);
        List<String> nameList = new ArrayList<>();
        if (names != null && names.length > 0) {
            nameList.addAll(Arrays.asList(names));
        }
        if (SUtil.isNotBlank(name)) {
            nameList.add(name);
        }
        return nameList;
    }

    protected static void generateOriginFieldWithValue(Object originBean,
                                                    Map<String, Object> originMap,
                                                    Map<String, Object> originAliasMap) {
        Class<?> beanClass = originBean.getClass();
        Field[] targetFieldList = SFieldUtil.getFileds(beanClass);
        for (Field field : targetFieldList) {
            String name = field.getName();

            Method methodOfGet = SFieldUtil.getMethodByField(field,beanClass,false);
            Object value = SFieldUtil.getObjectByMethod(methodOfGet,originBean);

            //存储值
            originMap.put(name, value);
            //获取注解别名
            List<String> aliasNames = aliasNames(field);
            for (String aliasName : aliasNames) {
                originAliasMap.put(aliasName, value);
            }
        }
    }

    protected static void settingTargetFieldWithValue(Object targetBean,
                                                      Map<String, Object> originMap,
                                                      Map<String, Object> originAliasMap,
                                                      Boolean isFill,
                                                      Boolean isReplace) {
        Class<?> beanClass = targetBean.getClass();
        Field[] targetFieldList = SFieldUtil.getFileds(beanClass);
        for (Field field : targetFieldList) {

            Method methodOfGet = SFieldUtil.getMethodByField(field,beanClass,false);
            assert methodOfGet != null;

            Method methodOfSet = SFieldUtil.getMethodByField(field,beanClass,true);
            assert methodOfSet != null;

            //获取需要写入的值
            //优先级 target本名->别名->origin别名
            String name = field.getName();
            List<String> aliasNames = aliasNames(field);

            Object setValue = originMap.get(name);
            if (setValue == null) {
                for (String aliasName : aliasNames) {
                    setValue = originMap.get(aliasName);
                    if (setValue != null) {
                        break;
                    }
                }
            }
            if (setValue == null) {
                setValue = originAliasMap.get(name);
            }
            //获取当前 target 的值
            Object value = SFieldUtil.getObjectByMethod(methodOfGet,targetBean);


            if (setValue == null && value == null) {
                continue;
            }
            //不填充
            if (!isFill) {
                setObject(setValue,field,methodOfSet,targetBean);
                continue;
            }
            //填充
            if (setValue == null) {
                continue;
            }
            if (value == null) {
                setObject(setValue,field,methodOfSet,targetBean);
                continue;
            }
            if (isReplace) {
                setObject(setValue, field, methodOfSet, targetBean);
            }
        }
    }


}
