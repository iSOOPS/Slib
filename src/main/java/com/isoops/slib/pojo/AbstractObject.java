package com.isoops.slib.pojo;

import com.isoops.slib.utils.SBeanUtil;
import com.isoops.slib.utils.SFieldUtil;
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
	 */
	public void apply(String key,Object value) {
		SFieldUtil.setProperty(this,key,value);
	}

	/**
	 * 浅度克隆
	 */
	public <T> T clone(Class<T> clazz) {
		return SBeanUtil.clone(this,clazz);
	}

	/**
	 * 浅度克隆
	 */
	public <T> T clone(T target) {
		return SBeanUtil.clone(this,target);
	}

	/**
	 * 别名克隆
	 */
	public <T> T aliasClone(Class<T> clazz) {
		return SBeanUtil.aliasClone(this,clazz);
	}

	/**
	 * 别名克隆
	 */
	public <T> T aliasClone(T target) {
		return SBeanUtil.aliasClone(this,target);
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
				Class<?> cloneTargetClazz = CloneDirection.getTargetClass(listGenericClazz, cloneDirection);
				Method setFieldMethod = SFieldUtil.getMethodByField(field, clazz , true);
				assert cloneTargetClazz != null;
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
				Class<?> cloneTargetClazz = CloneDirection.getTargetClass(listGenericClazz, cloneDirection);
				// 将list集合克隆到目标list集合中去
				List<? extends AbstractObject> clonedList = SBeanUtil.clones(
						list,
						(Class<? extends AbstractObject>)cloneTargetClazz,
						cloneDirection);
				// 获取设置克隆好的list的方法名称
				// setRelations
				Method setFieldMethod = SFieldUtil.getMethodByField(field, clazz ,true);
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

	public static Boolean isAbstractObject(Class<?> clazz) {
		return clazz.getName().contains("AbstractObject");
	}


}
