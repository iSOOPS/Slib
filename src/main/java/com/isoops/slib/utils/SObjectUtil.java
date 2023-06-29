package com.isoops.slib.utils;


import cn.hutool.core.collection.CollUtil;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.pojo.BeanCopierUtils;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

/**
 * 对象工具类
 * @author Samuel
 *
 */
public class SObjectUtil {

	/**
	 * 克隆生成
	 * ps:V=domainClone(T,V.class)
	 */
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

	/**
	 * 克隆写入
	 * ps:V=domainClone(T,V)
	 */
	public static <T,V> V domainClone(T domain,V target) {
		assert target != null;
		BeanCopierUtils.copyProperties(domain, target);
		return target;
	}

	/**
	 * 批量克隆写入
	 * ps:List[K]=convertList(List[T],K.class)
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
			K objectNew = targetClazz.getDeclaredConstructor().newInstance();
			BeanCopierUtils.copyProperties(sourceObject, objectNew);
			targetList.add(objectNew);
		}
		return targetList;
	}

	/**
	 * 深度克隆 domain 定向(内部对象也会克隆)
	 * ps:List[T]=convertList(List[T],T.class,CloneDirection.TO_DO)
	 */
	public static <T> List<T> convertList(List<? extends AbstractObject> sourceList, Class<T> targetClazz, Integer cloneDirection){
		 if (sourceList == null) {
			 return null;
		}
		List<T > targetList = new ArrayList<T>();
		try {
			for (AbstractObject sourceObject : sourceList) {
				targetList.add(sourceObject.clone(targetClazz, cloneDirection));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return targetList;
	}

	/**
	 * 获取 Class
	 */
	public static Class<?> getClass(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	/**
	 * 获取 Class 特定名称的属性名
	 */
	public static Field getFiled(Class<?> clazz,String fieldName) throws NoSuchFieldException {
		return clazz.getDeclaredField(fieldName);
	}

	/**
	 * 获取 Class 所有属性名(包含父类)
	 */
	public static Field[] getFileds(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		Class<?> superClazz = clazz.getSuperclass();
		if (!superClazz.equals(clazz)){
			Field[] tableSuperFields = superClazz.getDeclaredFields();
			Field[] superFields = new Field[fields.length + tableSuperFields.length];
			System.arraycopy(fields, 0, superFields, 0, fields.length);
			System.arraycopy(tableSuperFields, 0, superFields, fields.length, tableSuperFields.length);
			fields = getSuperClassFields(superFields, superClazz);
		}
		return fields;
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
	 * 获取对象 所有属性名(包含父类)
	 */
	public static String[] getFiledsNames(Object o){
		return getFiledsNames(o.getClass());
	}

	/**
	 * 获取父类 所有属性名
	 */
	public static Field[] getSuperClassFields(Field[] tableFields, Class<?> clazz) {
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
	 * 获取对象 值
	 * ps:Obj=getValueByName("name",bean)=bean.getName()
	 */
	public static Object getValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter);
			return method.invoke(o);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取 lambda 的 Serialized
	 * @param fn 必须为 Funtion 必须继承 Serializable
	 */
	private static <T> SerializedLambda getSerializedLambda(Function<T, ?> fn) {
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
		return serializedLambda;
	}

	/**
	 * function 获取 class
	 * @param fn 必须为 Funtion 必须继承 Serializable
	 */
	public static <T> Class<?> getFunctionClass(Function<T, ?> fn) {
		SerializedLambda serializedLambda = getSerializedLambda(fn);
		String className = serializedLambda.getImplClass().replace("/", ".");
		try {
			return getClass(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * function 获取 string
	 * @param fn 必须为 Funtion 必须继承 Serializable
	 */
	public static <T> String getFunctionName(Function<T, ?> fn) {
		SerializedLambda serializedLambda = getSerializedLambda(fn);
		String fieldName = serializedLambda.getImplMethodName().substring("get".length());
		fieldName = fieldName.replaceFirst(fieldName.charAt(0) + "", (fieldName.charAt(0) + "").toLowerCase());
		return fieldName;
	}

	/**
	 * function 获取 field
	 * @param fn 必须为 Funtion 必须继承 Serializable
	 */
	public static <T> Field getFunctionFiled(Function<T, ?> fn) {
		Class<?> clazz = getFunctionClass(fn);
		String fieldName = getFunctionName(fn);
		Field field;
		try {
			assert clazz != null;
			field = getFiled(clazz,fieldName);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
		return field;
	}

	/**
	 * function 字段名
	 * @param fn 必须为 Funtion 必须继承 Serializable
	 */
	public static <T> String getFunctionFiledName(Function<T, ?> fn) {
		String fieldName = getFunctionName(fn);
		return fieldName.replaceAll("[A-Z]", "_$0").toLowerCase();
	}

	/**
	 * 获取数组中对象的某个值,重组成一个新数组
	 * ps: Name-List[String]=foreach(List[T],T::getName)
	 */
	public static <T,R>  List<R> foreach(List<T> list, Function<T,R> function){
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
	 * 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
	 * ps: List[T]=foreachByKey(List[T],T::getName,"someoneName")
	 */
	public static <T,R>  List<T> foreachByKey(List<T> list, Function<T,R> function, R value){
		return foreachByListKey(list,function, Collections.singletonList(value));
	}

	/**
	 * 获取数组中对象的某个值,将满足条件的值,重组成一个新数组
	 * ps: List[T]=foreachByKey(List[T],T::getName,List[R])
	 */
	public static <T,R>  List<T> foreachByListKey(List<T> list, Function<T,R> function, List<R> values){
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
	 */
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
	 * 获取method set/get 方法
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

	/**
	 * 数组去重
	 */
	public static <T> List<T> outDuplicate(List<T> list){
		LinkedHashSet<T> temp = new LinkedHashSet<>(list);
		return new ArrayList<>(temp);
	}
}
