package com.isoops.slib.common.auth;

import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.utils.SUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
public class AuthSingleton {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public class TokenCache extends AbstractObject {
        private String token;
        private Long timeEnd;
        public TokenCache(String token,Long timeEnd) {
            this.token = token;
            this.timeEnd = timeEnd;
        }
    }

    private static volatile AuthSingleton authSingleton;

    private AuthSingleton() {
        tokenCacheMap = new HashMap<>();
    }

    private Map<String,TokenCache> tokenCacheMap;

    private Long getRealTimeEnd(Long time, TimeUnit timeUnit) {
        Long realTime = 0L;
        switch (timeUnit) {
            case DAYS:
                realTime = time * 1000 * 60 * 60 * 24;
                break;
            case HOURS:
                realTime = time * 1000 * 60 * 60;
                break;
            case MINUTES:
                realTime = time * 1000 * 60;
                break;
            case SECONDS:
                realTime = time * 1000;
                break;
            case MILLISECONDS:
            default:
                realTime = time;
        }
        return System.currentTimeMillis() + realTime;
    }

    public void setToken(String key,
                         String token,
                         Long time,
                         TimeUnit timeUnit) {
        TokenCache tokenCache = new TokenCache(token,getRealTimeEnd(time,timeUnit));
        tokenCacheMap.put(key,tokenCache);
    }

    public String getToken(String key) {
        TokenCache tokenCache = tokenCacheMap.get(key);
        if (tokenCache == null) {
            return null;
        }
        if (tokenCache.getTimeEnd() < System.currentTimeMillis()){
            tokenCacheMap.remove(key);
            return null;
        }
        return tokenCache.getToken();
    }

    public Boolean checkToken(String key,String token) {
        String t = getToken(key);
        return SUtil.isNotBlank(t) && t.equals(token);
    }

    public static AuthSingleton singleton() {
        if (authSingleton == null) {
            synchronized (AuthSingleton.class) {
                if (authSingleton == null) {
                    authSingleton = new AuthSingleton();
                }
            }
        }
        return authSingleton;
    }
}
