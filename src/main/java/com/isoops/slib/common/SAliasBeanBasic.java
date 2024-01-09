package com.isoops.slib.common;

import com.isoops.slib.utils.SFieldUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class SAliasBeanBasic extends SBeanBasic {

    protected static void generateOriginFieldWithValue(Object originBean,
                                                    Map<String, Object> originMap,
                                                    Map<String, Object> originAliasMap) {
        Class<?> beanClass = originBean.getClass();
        Field[] targetFieldList = SFieldUtil.getFileds(beanClass);
        for (Field field : targetFieldList) {
            String name = field.getName();

            Method methodOfGet = SFieldUtil.getMethodByField(field,beanClass,false);
            Object value = SFieldUtil.doObjectByMethod(methodOfGet,originBean);

            //存储值
            originMap.put(name, value);
            //获取注解别名
            String annotationName = getAnnotationTargetName(field,name);
            if (annotationName != null) {
                originAliasMap.put(annotationName, value);
            }
        }
    }

    protected static void settingTargetFieldWithValue(Object targetBean,
                                                    Map<String, Object> originMap,
                                                    Map<String, Object> originAliasMap) {
        Class<?> beanClass = targetBean.getClass();
        Field[] targetFieldList = SFieldUtil.getFileds(beanClass);
        for (Field field : targetFieldList) {
            String name = field.getName();

            Method methodOfGet = SFieldUtil.getMethodByField(field,beanClass,false);
            assert methodOfGet != null;

            Method methodOfSet = SFieldUtil.getMethodByField(field,beanClass,true);
            assert methodOfSet != null;

            Object value = SFieldUtil.doObjectByMethod(methodOfGet,targetBean);
            if (value!=null) {
                if (!value.equals(originMap.get(field.getName()))) {
                    SFieldUtil.doObjectByMethod(methodOfSet,targetBean,originMap.get(field.getName()));
                }
                continue;
            }

            Object originValue;
            String originName = getAnnotationOriginName(field);
            if (originName != null) {
                originValue = originMap.get(originName);
                if (!setObject(originValue,field,methodOfSet,targetBean)) {
                    continue;
                }
            }
            originValue = originAliasMap.get(name);
            setObject(originValue,field,methodOfSet,targetBean);
        }
    }


}
