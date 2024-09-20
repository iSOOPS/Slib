package com.isoops.slib.utils;

import cn.hutool.core.util.StrUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 封装处理Class/Feild/Function的常用方法
 * @author samuel
 *
 * @see #getFiled(Class, String)                    getFiled            获取 Class 特定名称的属性名
 * @see #getFileds(Class)                           getFileds           获取 Class 所有属性(包含父类)
 * @see #getFiledsNames(Class)                      getFiledsNames      获取 Class 所有属性名(包含父类)
 * @see #getSuperClassFields(Field[], Class)        getSuperClassFields 获取 Class 父类所有属性名
 *
 * @see #getFunctionClass(Function)                 getFunctionClass    获取 Class
 * @see #getFunctionName(Function)                  getFunctionName     获取 field-string
 * @see #getFunctionFiled(Function)                 getFunctionFiled    获取 field-string
 * @see #setProperty(Object, String, Object)        setProperty         给泛型写入值
 * @see #getMethodByField(Field, Class, Boolean)    getMethodByField    获取method set/get 方法
 * @see #getObjectByMethod(Method, Object)          doObjectByMethod    通过获取method后取值
 * @see #setObjectByMethod(Method, Object, Object)  doObjectByMethod    通过获取method后存值
 *
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
                if (setFieldMethod == null) {
                    continue;
                }
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
        PropertyDescriptor descriptor;
        try {
            descriptor = new PropertyDescriptor(field.getName(), clazz);
        } catch (IntrospectionException e) {
            //兼容 lambok 的 @Accessors(chain = true) 导致无法获得反射方法的异常
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(clazz);
            } catch (IntrospectionException a) {
                return null;
            }
            List<PropertyDescriptor> descriptors = Arrays.stream(beanInfo.getPropertyDescriptors()).filter(p -> {
                String name = p.getName();
                //过滤掉不需要修改的属性
                return !"class".equals(name);
            }).collect(Collectors.toList());

            Map<String, PropertyDescriptor> map = descriptors.stream()
                    .collect(Collectors.toMap(PropertyDescriptor::getName, p -> p));
            if (map.containsKey(field.getName())) {
                return needSet ? map.get(field.getName()).getWriteMethod() : map.get(field.getName()).getReadMethod();
            }
            return null;
        }
        return needSet ? descriptor.getWriteMethod() : descriptor.getReadMethod();
    }

    /**
     * 通过获取method后取值
     */
    public static Object getObjectByMethod(Method method,Object object) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            SLog.warn("SLib Error:[SFieldUtil.doObjectByMethod]",e.getMessage());
            return null;
        }
    }

    /**
     * 通过获取method后存值
     */
    public static void setObjectByMethod(Method method,Object object,Object toObejct) {
        if (method == null) {
            return;
        }
        try {
            //获取值
            method.invoke(object,toObejct);
        } catch (IllegalAccessException | InvocationTargetException e) {
            SLog.warn("SLib Error:[SFieldUtil.doObjectByMethod]",e.getMessage());
        }
    }


}
