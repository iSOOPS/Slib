package com.isoops.slib.common.auth;

import com.isoops.slib.common.token.TokenChannel;

public interface SAuthService {

    /**
     * 获取token 单例存储
     * @param data 自定义数据
     * @param serviceKey 自定义服务类型
     * @param channel 自定义来源渠道
     * @return token
     */
    String getTokenBySingleton(String data,
                               String serviceKey,
                               TokenChannel channel);

    /**
     * 验证 token 有效性
     * @param token token
     * @return f
     */
    Boolean checkTokenBySingleton(String token);

    /**
     * 获取token
     * @param data 自定义数据
     * @param serviceKey 自定义服务类型
     * @param channel 自定义来源渠道
     * @return token
     */
    String getToken(String data,
                    String serviceKey,
                    TokenChannel channel);

    /**
     * token 加密测试
     * @param str str
     * @return f
     */
    String encrypt(String str);

    /**
     * token 解密测试
     * @param str str
     * @return f
     */
    String decrypt(String str);
}
