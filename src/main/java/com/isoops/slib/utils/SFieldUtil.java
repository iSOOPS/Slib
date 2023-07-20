package com.isoops.slib.utils;

import cn.hutool.core.util.StrUtil;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 封装处理Class/Feild/Function的常用方法
 * @author samuel
 * *Menu*
 * @see #getFiled(Class, String)                    获取 Class 特定名称的属性名
 * @see #getFileds(Class)                           获取 Class 所有属性(包含父类)
 * @see #getFiledsNames(Class)                      获取 Class 所有属性名(包含父类)
 * @see #getSuperClassFields(Field[], Class)        获取 Class 父类所有属性名
 *
 * @see #getFunctionClass(Function)                 获取 Class
 * @see #getFunctionName(Function)                  获取 field-string
 * @see #getFunctionFiled(Function)                 获取 field-string
 * @see #setProperty(Object, String, Object)        给泛型写入值
 * @see #getMethodByField(Field, Class, Boolean)    获取method set/get 方法
 */
public class SFieldUtil {

    private static <T> SerializedLambda getSerializedLambda(Function<T, ?> fn) {
        try {
            // 从function取出序列化方法
            Method writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
            // 从序列化方法取出序列化的lambda信息
            boolean isAccessible = writeReplaceMethod.isAccessible();
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
            writeReplaceMethod.setAccessible(isAccessible);
            return serializedLambda;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 Class 特定名称的属性名
     */
    public static Field getFiled(Class<?> clazz, String fieldName) {
        Field[] fields = getFileds(clazz);
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 获取 Class 所有属性(包含父类)
     */
    public static Field[] getFileds(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        return getSuperClassFields(fields, clazz);
    }

    /**
     * 获取 Class 所有属性名(包含父类)
     */
    public static String[] getFiledsNames(Class<?> clazz) {
        Field[] fields = getFileds(clazz);
        String[] fieldNames = new String[fields.length];
        for(int i=0;i<fields.length;i++){
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 获取父类 所有属性名
     */
    public static Field[] getSuperClassFields(Field[] fields, Class<?> clazz) {
        if (clazz.getSuperclass() == null) {
            return fields;
        }
        Class<?> superClazz = clazz.getSuperclass();
        Field[] superFields = superClazz.getDeclaredFields();
        Field[] fieldsNew = new Field[fields.length + superFields.length];
        System.arraycopy(fields, 0, fieldsNew, 0, fields.length);
        System.arraycopy(superFields, 0, fieldsNew, fields.length, superFields.length);
        getSuperClassFields(fieldsNew, superClazz);
        return fieldsNew;
    }

    /**
     * function 获取 Class
     * @param fn 必须继承 Serializable
     */
    public static <T> Class<?> getFunctionClass(Function<T, ?> fn) {
        SerializedLambda serializedLambda = getSerializedLambda(fn);
        String className = serializedLambda.getImplClass().replace("/", ".");
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * function 获取 field-string
     * @param fn 必须为 Funtion 必须继承 Serializable
     */
    public static <T> String getFunctionName(Function<T, ?> fn) {
        SerializedLambda serializedLambda = getSerializedLambda(fn);
        String fieldName = serializedLambda.getImplMethodName().substring("get".length());
        return StrUtil.lowerFirst(fieldName);
    }

    /**
     * function 获取 field
     * @param fn 必须为 Funtion 必须继承 Serializable
     */
    public static <T> Field getFunctionFiled(Function<T, ?> fn) {
        Class<?> clazz = getFunctionClass(fn);
        String fieldName = getFunctionName(fn);
        return getFiled(clazz,fieldName);
    }

    /**
     * 给泛型写入值
     */
    public static void setProperty(Object object, String key, Object value) {
        Class<?> thisClazz = object.getClass();
        Field[] fields = thisClazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(key)) {
                Method setFieldMethod = getMethodByField(field, thisClazz ,true);
                try {
                    setFieldMethod.invoke(object, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 获取method set/get 方法
     */
    public static Method getMethodByField(Field field, Class<?> clazz , Boolean needSet) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), clazz);
            return needSet ? descriptor.getWriteMethod() : descriptor.getReadMethod();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
}
