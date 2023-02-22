package com.isoops.slib.pojo;

/**
 * 克隆方向
 * @author Samuel
 *
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

}
