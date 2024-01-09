package com.isoops.slib.common;

import com.isoops.slib.annotation.SFieldAlias;
import com.isoops.slib.utils.SFieldUtil;
import com.isoops.slib.utils.SLog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SBeanBasic {

    protected static <T> T createTarget(Class<T> clazz) {
        T target = null;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return target;
    }

    protected static String getAnnotationTargetName(Field field, String name) {
        SFieldAlias a = field.getAnnotation(SFieldAlias.class);
        if (a == null) {
            return null;
        }
        String tName = a.targetName().isEmpty() ? a.name() : a.targetName();
        if (tName == null) {
            return null;
        }
        if (tName.equals(name)) {
            return null;
        }
        return tName;
    }

    protected static String getAnnotationOriginName(Field field) {
        SFieldAlias a = field.getAnnotation(SFieldAlias.class);
        if (a == null) {
            return null;
        }
        return a.originName().isEmpty() ? a.name() : a.originName();
    }

    protected static String errorMsg(Object targetBean,Field field,Object originValue) {
        String targetFiledName = targetBean.getClass().getName() + "." + field.getName() + "的类型" + field.getType().getName();
        String originFiledName = "来源数据" + originValue.getClass().getName();
        return targetFiledName + "与" + originFiledName + "类型不一致";
    }

    protected static boolean setObject(Object originValue, Field field, Method methodOfSet, Object targetBean) {
        if (originValue==null) {
            return false;
        }
        if (field.getType().equals(String.class)) {
            SFieldUtil.doObjectByMethod(methodOfSet,targetBean,String.valueOf(originValue));
            return false;
        }
        if (!field.getType().equals(originValue.getClass())) {
            SLog.warn(errorMsg(targetBean,field,originValue));
            return false;
        }
        SFieldUtil.doObjectByMethod(methodOfSet,targetBean,originValue);
        return true;
    }
}
