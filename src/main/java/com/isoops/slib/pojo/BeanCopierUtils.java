package com.isoops.slib.pojo;


import org.springframework.cglib.beans.BeanCopier;

import java.util.HashMap;
import java.util.Map;

/**
 * BeanCopier工具类
 * @author Samuel
 */
public class BeanCopierUtils {

	/**
	 * BeanCopier缓存
	 */
	public static Map<String, BeanCopier> beanCopierCacheMap = new HashMap<String, BeanCopier>();

	/**
	 * 将source对象的属性拷贝到target对象中去
	 * @param source source对象
	 * @param target target对象
	 */
    public static void copyProperties(Object source, Object target){
        String cacheKey = source.getClass().toString() +
        		target.getClass().toString();
        BeanCopier beanCopier = null;

        // 线程1和线程2，同时过来了

        if (!beanCopierCacheMap.containsKey(cacheKey)) {
        	// 两个线程都卡这儿了
        	// 但是此时线程1先获取到了锁，线程2就等着
        	synchronized(BeanCopierUtils.class) {
        		// 线程1进来之后，发现这里还是没有那个BeanCopier实例
        		// 此时线程2，会发现缓存map中已经有了那个BeanCopier实例了，此时就不会进入if判断内的代码
        		 if(!beanCopierCacheMap.containsKey(cacheKey)) {
        			 // 进入到这里会创建一个BeanCopier实例并且放在缓存map中
        			 beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
        			 beanCopierCacheMap.put(cacheKey, beanCopier);
        		 } else {
        			 beanCopier = beanCopierCacheMap.get(cacheKey);
        		 }
        	}
        } else {
            beanCopier = beanCopierCacheMap.get(cacheKey);
        }

        beanCopier.copy(source, target, null);
    }

}
