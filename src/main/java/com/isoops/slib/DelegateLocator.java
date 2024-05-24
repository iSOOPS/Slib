package com.isoops.slib;

import cn.hutool.core.util.StrUtil;
import com.isoops.slib.annotation.blocker.BlockerDelegate;
import com.isoops.slib.annotation.blocker.BlockerDelegateDefaultImpl;
import com.isoops.slib.redis.RedisDelegate;
import com.isoops.slib.redis.RedisDelegateDefaultImpl;
import com.isoops.slib.utils.encrypt.EncryptDelegate;
import com.isoops.slib.utils.encrypt.EncryptDelegateDefaultImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 代理选择器
 */
@Component
public class DelegateLocator implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 构建默认代理对象
     */
    private static final Map<Class<?>,Class<?>> delegateDefaultClass = new HashMap<>();
    static{
        delegateDefaultClass.put(BlockerDelegate.class , BlockerDelegateDefaultImpl.class);
        delegateDefaultClass.put(EncryptDelegate.class , EncryptDelegateDefaultImpl.class);
        delegateDefaultClass.put(RedisDelegate.class , RedisDelegateDefaultImpl.class);
    }

    /**
     * 代理实现对象
     */
    private Map<Class<?>, Map<String, ?>> delegateMap;


    /**
     * 获取某代理的所有实现的 map
     */
    public <T> Map<String, ?> getMap(Class<T> clazz) {
        return delegateMap.get(clazz);
    }

    /**
     * 获取某代理的某个实现
     */
    @SuppressWarnings("unchecked")
    public <T,M> M getByImplName(Class<T> clazz,String implName) {
        return (M) getMap(clazz).get(implName);
    }

    /**
     * 获取非默认代理
     */
    public <T,M> M get(Class<T> clazz) {
        Set<String> implNames = getMap(clazz).keySet();
        String defaultName = StrUtil.lowerFirst(delegateDefaultClass.get(clazz).getSimpleName());
        if (implNames.size() > 1) {
            for (String implName : implNames) {
                if (!defaultName.equals(implName)) {
                    return getByImplName(clazz,implName);
                }
            }
        }
        return getByImplName(clazz,defaultName);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        delegateMap = new HashMap<>();
        for (Class<?> clazz : delegateDefaultClass.keySet()) {
            delegateMap.put(clazz,applicationContext.getBeansOfType(clazz));
        }
    }
}
