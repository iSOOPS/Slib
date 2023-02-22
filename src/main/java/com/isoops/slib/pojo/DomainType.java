package com.isoops.slib.pojo;

/**
 * 领域模型对象的类型
 * @author Samuel
 *
 */
public class DomainType {

	/**
	 * RO: Request Object
	 * 请求对象
	 */
	public static final String RO = "RO";

	/**
	 * VO：Value Object
	 * 响应对象
	 */
	public static final String VO = "VO";

	/**
	 * BO: Business Object
	 * 业务对象
	 */
	public static final String BO = "BO";

	/**
	 * DTO：Data Transfer Object
	 * 转换对象
	 */
	public static final String DTO = "DTO";

	/**
	 * DO：Data Object
	 * 领域对象
	 */
	public static final String DO = "DO";

	/**
	 * PO: Persistent Object
	 * 持久化对象
	 */
	public static final String PO = "PO";

	public static final String[] ALL_DOMAIN_TYPES = new String[] { RO, VO, BO, DO , PO , DTO};

	public static <T> Boolean isDomain(Class<T> clazz) {
		String className = clazz.getName();
		for (String type : ALL_DOMAIN_TYPES) {
			if (className.endsWith(type)) {
				return true;
			}
		}
		return false;
	}
}
