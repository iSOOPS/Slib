package com.isoops.slib.common.token;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import com.isoops.slib.utils.encrypt.EncryptDelegate;
import com.isoops.slib.utils.encrypt.SEncrypt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Token生成器 {@link EncryptDelegate}
 * @author samuel
 *
 * @see #caesarCipher(String, Boolean) caesarCipher    恺撒偏移加密/解密
 * @see #aesEncoding(String)           aesEncoding     SM2 国密加密
 * @see #aesDecoding(String)           aesDecoding     SM2 国密解密
 * @see #sm2Encoding(String)           sm2Encoding     AES 加密
 * @see #sm2Decoding(String)           sm2Decoding     AES 解密
 */
@Component
public class TokenGenertor {

    @Resource
    private SEncrypt sEncrypt;

    public String caesarCipher(String str ,Boolean isEncode) {
        return sEncrypt.caesarCipher(str, isEncode);
    }

    public String aesEncoding(String token) {
        return sEncrypt.encryptAes(token, Mode.CBC, Padding.PKCS5Padding);
    }

    public String aesDecoding(String token) {
        return sEncrypt.decryptAes(token,Mode.CBC,Padding.PKCS5Padding);
    }

    public String sm2Encoding(String aesToken) {
        String base64Token = Base64.encode(aesToken);
        return sEncrypt.encryptSM2(base64Token);
    }

    public String sm2Decoding(String sm2Token) {
        String base64Token = sEncrypt.decryptSM2(sm2Token);
        return Base64.decodeStr(base64Token);
    }
}
