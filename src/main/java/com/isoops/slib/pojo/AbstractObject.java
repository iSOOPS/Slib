package com.isoops.slib.pojo;

import com.isoops.slib.utils.SObjectUtil;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 基础POJO类
 * @author Samuel
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Data
public class AbstractObject {

	/**
	 * 给泛型写入一个值
	 * @param key 字段名称
	 * @param value 值
	 * @param <V> 字段泛型
	 */
	public <V> void apply(String key,V value) {
		SObjectUtil.setProperty(this,key,value);
	}

	/**
	 * 浅度克隆
	 */
	public <T> T clone(Class<T> clazz) {
		return SObjectUtil.domainClone(this,clazz);
	}

	/**
	 * 浅度克隆
	 */
	public <T> T clone(T target) {
		return SObjectUtil.domainClone(this,target);
	}

	/**
	 * 深度克隆/递归所有内部对象
	 */
	public <T,V extends AbstractObject> T clone(Class<T> clazz, Integer cloneDirection) throws Exception {
		// 先完成基本字段的浅克隆
		T target = clazz.getDeclaredConstructor().newInstance();
		BeanCopierUtils.copyProperties(this, target);
		// 完成所有List类型的深度克隆
		// CategoryDTO
		Class<?> thisClazz = this.getClass();
		Field[] fields = thisClazz.getDeclaredFields();
		for(Field field : fields) {
			field.setAccessible(true);
			// 如果判断某个字段是AbstractObject的领域对象
			if (isAbstractObject(field.getType()) && DomainType.isDomain(field.getType())) {
				V nowObject = (V) field.get(this);
				Class<?> listGenericClazz = getListGenericType(field);
				assert listGenericClazz != null;
				Class<?> cloneTargetClazz = getCloneTargetClazz(listGenericClazz, cloneDirection);
				Method setFieldMethod = SObjectUtil.getMethodByField(field, clazz , true);
				setFieldMethod.invoke(target, nowObject.clone(cloneTargetClazz,cloneDirection));
			}
			// 如果判断某个字段是List类型的
			// field = private List<Relation> relations;
			if(field.getType() == List.class) {
				// field.getType() List 不是 List<Relation>
				// List<RelationDTO>集合
				List<? extends AbstractObject> list = (List<? extends AbstractObject>) field.get(this);
				if(list == null || list.size() == 0) {
					continue;
				}
				// 获取List集合中的泛型类型
				// RelationDTO
				Class<?> listGenericClazz = getListGenericType(field);
				// 获取要克隆的目标类型
				// 假设CloneDirection是反向，此时获取到的就是RelationVO
				assert listGenericClazz != null;
				Class<?> cloneTargetClazz = getCloneTargetClazz(listGenericClazz, cloneDirection);
				// 将list集合克隆到目标list集合中去
				List<? extends AbstractObject> clonedList = SObjectUtil.convertList(
						list,
						(Class<? extends AbstractObject>)cloneTargetClazz,
						cloneDirection);
				// 获取设置克隆好的list的方法名称
				// setRelations
				Method setFieldMethod = SObjectUtil.getMethodByField(field, clazz ,true);
				setFieldMethod.invoke(target, clonedList);
				// target是CategoryVO对象，此时就是调用CategoryVO的setRelations方法，
				// 将克隆好的List<CategoryVO>给设置进去
			}
		}

		return target;
	}

	/**
	 * 获取list集合的泛型类型
	 */
	private Class<?> getListGenericType(Field field) {
		// genericType = List<RelationDTO>，不是List
		Type genericType = field.getGenericType();
        if(genericType instanceof ParameterizedType){
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            return (Class<?>)parameterizedType.getActualTypeArguments()[0];
        }
        return null;
	}

	/**
	 * 获取目标类名
	 */
	private Class<?> getCloneTargetClazz(Class<?> clazz, Integer cloneDirection) throws Exception {
		// ReflectionDTO
		String className = clazz.getName();
		//RO/VO/DO...
		String domainName = CloneDirection.toDomain(cloneDirection);
		if (domainName == null) {
			return clazz;
		}
		//是否为DTO
		int sumEndInteger = className.length() - (className.endsWith(DomainType.DTO) ? 3 : 2);
		//拼接新class名称
		String cloneTargetClassName = className.substring(0, sumEndInteger) + CloneDirection.toDomain(cloneDirection);
		return Class.forName(cloneTargetClassName);
	}

	public static Boolean isAbstractObject(Class<?> clazz) {
		return clazz.getName().contains("AbstractObject");
	}


}
