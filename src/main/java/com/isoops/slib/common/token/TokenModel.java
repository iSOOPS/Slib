package com.isoops.slib.common.token;

import com.isoops.slib.utils.SUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Token 对象操作类
 * @author samuel
 *
 * @see #to(String, String, TokenChannel)   to     对象创建
 * @see #to(String, Boolean)                to     对象反向创建
 * @see #key()                              key    生成 key 用于存储标识
 * @see #token()                            token  生成 token
 */
@Data
@Component
public class TokenModel {

    @Resource
    private TokenGenertor tokenGenertor;

    private static final String defaultServiceKey = "ISOOPS";

    private String data;

    private String serviceKey;

    private TokenChannel channel;

    public TokenModel() {
        this.data = "";
        this.serviceKey = defaultServiceKey;
        this.channel = TokenChannel.UNK;
    }

    /**
     * 对象创建
     * @param data 自定义对象
     * @param serviceKey 业务key,默认 3 位
     * @param channel 渠道
     * @return  对象
     */
    public TokenModel to(String data, String serviceKey, TokenChannel channel) {
        this.data = data == null ? "" : data;
        this.serviceKey = defaultServiceKey;
        if (SUtil.isNotBlank(serviceKey)) {
            this.serviceKey = serviceKey;
            //serviceKey 限制为 6 位,不足的补全
            if (serviceKey.length() < 6) {
                this.serviceKey = StringUtils.rightPad(serviceKey,6,"I");
            }
            if (serviceKey.length() > 6) {
                this.serviceKey = serviceKey.substring(0,5);
            }
        }
        this.channel = channel == null ? TokenChannel.UNK : channel;
        return this;
    }

    /**
     * 对象反向创建
     * @param str 字段
     * @param isToken 是为 token,否为 key
     * @return 对象
     */
    public TokenModel to(String str,Boolean isToken) {
        String caesarKey = str.substring(0,6);
        this.serviceKey = tokenGenertor.caesarCipher(caesarKey,false);

        String channel = isToken ? str.substring(19,22) : str.substring(6,9);
        this.channel = TokenChannel.valueOf(channel);

        this.data = isToken ? str.substring(22) : str.substring(9);
        return this;
    }

    /**
     * 生成 key 用于存储标识
     */
    public String key() {
        String caesarKey = tokenGenertor.caesarCipher(this.getServiceKey(),true);
        String channel = getCodeByChannel(this.getChannel());
        //长度 6 + 3 + ？
        return caesarKey + channel + this.getData();
    }

    /**
     * 生成 token
     */
    public String token() {
        String caesarKey = tokenGenertor.caesarCipher(this.getServiceKey(),true);
        String channel = getCodeByChannel(this.getChannel());
        //长度 6 + 13 + 3 + ？
        return caesarKey + System.currentTimeMillis() + channel + this.getData();
    }

    private String getCodeByChannel(TokenChannel channel) {
        if (channel == null) {
            return "UNK";
        }
        //0-后端 1-h5 2-app 3-小程序
        switch (channel) {
            case SERVICE :
                return "SER";
            case WEB :
                return "WEB";
            case APP :
                return "APP";
            case MIN :
                return "MIN";
            default :
                return "UNK";
        }
    }
}
