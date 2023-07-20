package com.isoops.slib.pojo;

/**
 * 克隆方向
 * @author Samuel
 */
public class CloneDirection {

	public static final Integer TO_RO = 1;
	public static final Integer TO_VO = 2;
	public static final Integer TO_BO = 3;
	public static final Integer TO_PO = 4;
	public static final Integer TO_DO = 5;
	public static final Integer TO_DTO = 6;

	public static String toDomain(Integer cloneDirection) {
		switch (cloneDirection) {
			case 1: return DomainType.RO;
			case 2: return DomainType.VO;
			case 3: return DomainType.BO;
			case 4: return DomainType.PO;
			case 5: return DomainType.DO;
			case 6: return DomainType.DTO;
			default: return null;
		}
	}

	public static <R> Class<?> getTargetClass(Class<R> clazz , Integer cloneDirection) {
		// ReflectionDTO
		String className = clazz.getName();
		//RO/VO/DO...
		String domainTag = CloneDirection.toDomain(cloneDirection);
		if (domainTag == null) {
			return null;
		}
		//是否为DTO 为DTO则截3为,反之2位
		int sumEndInteger = className.length() - (className.endsWith(DomainType.DTO) ? 3 : 2);
		//拼接新class名称
		String cloneTargetClassName = className.substring(0, sumEndInteger) + CloneDirection.toDomain(cloneDirection);
		try {
			return Class.forName(cloneTargetClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
