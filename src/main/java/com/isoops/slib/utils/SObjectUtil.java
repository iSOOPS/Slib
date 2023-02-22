package com.isoops.slib.utils;


import cn.hutool.core.collection.CollUtil;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.pojo.BeanCopierUtils;
import com.isoops.slib.pojo.IFunction;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * 对象工具类
 * @author Samuel
 *
 */
public class SObjectUtil {

	public static <T,V> V domainClone(T domain,Class<V> clazz) {
		V target = null;
		try {
			target = clazz.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		assert target != null;
		BeanCopierUtils.copyProperties(domain, target);
		return target;
	}

	public static <T,V> V domainClone(T domain,V target) {
		assert target != null;
		BeanCopierUtils.copyProperties(domain, target);
		return target;
	}

	/**
	 * 转换集合
	 * @param sourceList 源集合
	 * @param targetClazz 目标集合元素类型
	 * @return 转换后的集合
	 */
	@SneakyThrows
	public static <T,K> List<K> convertList(List<T> sourceList, Class<K> targetClazz) {
		if(sourceList == null) {
			return null;
		}
		List<K> targetList = new ArrayList<>();
		for(T sourceObject : sourceList) {
			if (sourceObject instanceof AbstractObject) {
				targetList.add( ((AbstractObject) sourceObject).clone(targetClazz));
				continue;
			}
			K objectNew = targetClazz.newInstance();
			BeanCopierUtils.copyProperties(sourceObject, objectNew);
			targetList.add(objectNew);
		}
		return targetList;
	}

	/**
	 * 转换集合-深度克隆
	 * @param sourceList 源集合
	 * @param targetClazz 目标集合元素类型
	 * @return 转换后的集合
	 */
	public static <T> List<T> convertList(List<? extends AbstractObject> sourceList,
										  Class<T> targetClazz, Integer cloneDirection) throws Exception {
		if(sourceList == null) {
			return null;
		}
		List<T> targetList = new ArrayList<T>();
		for(AbstractObject sourceObject : sourceList) {
			targetList.add(sourceObject.clone(targetClazz, cloneDirection));
		}
		return targetList;
	}

	/**
	 * 获取属性名数组
	 */
	public static String[] getFiledName(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Class<?> superClazz = clazz.getSuperclass();
		if (!superClazz.equals(clazz)){
			Field[] tableSuperFields = superClazz.getDeclaredFields();
			Field[] superFields = new Field[fields.length + tableSuperFields.length];
			System.arraycopy(fields, 0, superFields, 0, fields.length);
			System.arraycopy(tableSuperFields, 0, superFields, fields.length, tableSuperFields.length);
			fields = getSuperClassFields(superFields, superClazz);
		}
		String[] fieldNames = new String[fields.length];
		for(int i=0;i<fields.length;i++){
			fieldNames[i] = fields[i].getName();
		}
		return fieldNames;
	}

	public static String[] getFiledName(Object o){
		return getFiledName(o.getClass());
	}

	/**
	 * 获取父类的所有字段
	 * @param tableFields f
	 * @param clazz c
	 * @return f
	 */
	private static Field[] getSuperClassFields(Field[] tableFields, Class<?> clazz) {
		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz.equals(Object.class)) {
			return tableFields;
		}
		Field[] tableSuperFields = superClazz.getDeclaredFields();
		Field[] c = new Field[tableFields.length + tableSuperFields.length];
		System.arraycopy(tableFields, 0, c, 0, tableFields.length);
		System.arraycopy(tableSuperFields, 0, c, tableFields.length, tableSuperFields.length);
		getSuperClassFields(c, superClazz);
		return c;
	}

	/**
	 * 根据属性名获取属性值
	 */
	public static Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter);
			return method.invoke(o);
		} catch (Exception e) {
			return null;
		}
	}

	public static <T> String getFunctionFiledName(Function<T, ?> fn) {
		// 从function取出序列化方法
		Method writeReplaceMethod;
		try {
			writeReplaceMethod = fn.getClass().getDeclaredMethod("writeReplace");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}

		// 从序列化方法取出序列化的lambda信息
		boolean isAccessible = writeReplaceMethod.isAccessible();
		writeReplaceMethod.setAccessible(true);
		SerializedLambda serializedLambda;
		try {
			serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(fn);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		writeReplaceMethod.setAccessible(isAccessible);
		// 从lambda信息取出method、field、class等
		String fieldName = serializedLambda.getImplMethodName().substring("get".length());
		fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
		Field field;
		try {
			field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
		} catch (ClassNotFoundException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
	}

	/**
	 * 遍历数组->
	 * 获取数组中对象某个值->
	 * 重组成一个新数组
	 *
	 * @param list source list
	 * @param function 获取的值
	 * @param <T> source list 里的对象类型
	 * @param <R> 新数组/获取的值 的对象类型
	 * @return f
	 */
	public static <T,R>  List<R> foreach(List<T> list, IFunction<T,R> function){
		List<R> res = new ArrayList<>();
		if (SUtil.isBlank(list,function)){
			return res;
		}
		for (T object : list){
			R r = function.apply(object);
			if (SUtil.isNotBlank(r)){
				res.add(r);
			}
		}
		return res;
	}

	/**
	 * 遍历数组->
	 * 获取数组中对象某个值==value->
	 * 将满足条件的对象重组成一个新数组
	 *
	 * @param list source list
	 * @param function 需要判断的字段
	 * @param values 匹配的值
	 * @param <T> source list 里的对象类型
	 * @param <R> 匹配的值 的对象类型
	 * @return f
	 */
	public static <T,R>  List<T> foreachByListKey(List<T> list, IFunction<T,R> function, List<R> values){
		List<T> res = new ArrayList<>();
		if (SUtil.isBlank(list,function,values)){
			return res;
		}
		for (T object : list){
			R r = function.apply(object);
			for (R r1 : values){
				if (r.equals(r1)){
					res.add(object);
				}
			}
		}
		return res;
	}
	public static <T,R>  List<T> foreachByKey(List<T> list, IFunction<T,R> function, R value){
		return foreachByListKey(list,function, Collections.singletonList(value));
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

	public static <T> List<T> disposeSetList(List<T> sou1,List<T> sou2,SETTYPE type){
		switch (type){
			case UNION: return (List<T>) CollUtil.union(sou1, sou2);
			case INTERSECTION: return (List<T>) CollUtil.intersection(sou1, sou2);
			case DISJUNCTION: return (List<T>) CollUtil.disjunction(sou1, sou2);
			default: return new ArrayList<>();
		}
	}

	/**
	 * 给泛型写入值
	 * @param object 泛型对象
	 * @param key key
	 * @param value 值
	 * @param <V> 写入值类型
	 * @param <T> 泛型类型
	 */
	@SneakyThrows
	public static <V,T> void setProperty(T object, String key, V value) {
		Class<?> thisClazz = object.getClass();
		Field[] fields = thisClazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.getName().equals(key)) {
				Method setFieldMethod = getMethodByField(field, thisClazz ,true);
				setFieldMethod.invoke(object, value);
				break;
			}
		}
	}

	/**
	 * 获取method set 方法
	 * @param field f
	 * @param clazz c
	 * @return f
	 */
	public static Method getMethodByField(Field field, Class<?> clazz , Boolean needSet) {
		//组织method方法名
		String keyName = field.getName();
		String setMethodName = needSet ? "set" : "get" + keyName.substring(0, 1).toUpperCase() + keyName.substring(1);
		Method setFieldMethod = null;
		for(Method method : clazz.getDeclaredMethods()) {
			//遍历出method
			if(method.getName().equals(setMethodName)) {
				setFieldMethod = method;
				break;
			}
		}
		return setFieldMethod;
	}
}
