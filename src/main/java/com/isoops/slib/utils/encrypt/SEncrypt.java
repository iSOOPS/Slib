package com.isoops.slib.utils.encrypt;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.AES;
import com.isoops.slib.DelegateLocator;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 加密工具 代理密钥见{@link EncryptDelegate}
 * @author samuel
 *
 * @see #caesarCipher(String, Boolean)      caesarCipher    恺撒偏移加密/解密
 * @see #encryptSM2(String)                 encryptSM2      SM2 国密加密
 * @see #decryptSM2(String)                 decryptSM2      SM2 国密解密
 * @see #encryptAes(String, Mode, Padding)  encryptAes      AES 加密
 * @see #decryptAes(String, Mode, Padding)  decryptAes      AES 解密
 */
@Component
public class SEncrypt {

    @Resource
    private DelegateLocator delegateLocator;

    /**
     * 恺撒偏移加密/解密
     * @param str 内容
     * @param isEncrypt 是否加密
     * @return 加密后内容
     */
    public String caesarCipher(String str,Boolean isEncrypt) {
        EncryptDelegate delegate = delegateLocator.get(EncryptDelegate.class);
        int iv = delegate.getCaesarCipherIv() * (isEncrypt ? 1 : -1);
        String uStr = str.toUpperCase(Locale.ROOT);
        StringBuilder string = new StringBuilder();
        for(int i=0;i<uStr.length();i++) {
            char c=uStr.charAt(i);
            if(c>='A' && c<='Z') {//如果字符串中的某个字符是大写字母
                c+= (char) (iv % 26);//移动key%26位
                if(c<'A') {
                    c+=26;//向左超界
                }
                if(c>'Z') {
                    c-=26;//向右超界
                }
            }
            string.append(c);//将解密后的字符连成字符串
        }
        return string.toString();
    }

    /**
     * SM2 国密加密
     * @param text 内容
     * @return 加密后内容
     */
    public String encryptSM2(String text) {
        EncryptDelegate delegate = delegateLocator.get(EncryptDelegate.class);
        try {
            String output = StringEscapeUtils.unescapeHtml4(text);
            SM2 sm2 = SmUtil.sm2(null, delegate.getSM2PublicKey());
            return sm2.encryptHex(output, StandardCharsets.UTF_8, KeyType.PublicKey);
        } catch (Exception e) {
            throw new RuntimeException("SM2 Encrypt出错");
        }
    }

    /**
     * SM2 国密解密
     * @param text 内容
     * @return 解密后内容
     */
    public String decryptSM2(String text) {
        EncryptDelegate delegate = delegateLocator.get(EncryptDelegate.class);
        try {
            SM2 sm2 = SmUtil.sm2(delegate.getSM2PrivateKey(), delegate.getSM2PublicKey());
            return sm2.decryptStr(text, KeyType.PrivateKey);
        } catch (Exception e) {
            throw new RuntimeException("SM2 Decrypt 出错");
        }
    }

    /**
     * AES 加密
     * @param data 内容
     * @param mode 加密方式
     * @param padding p
     * @return 加密后内容
     */
    public String encryptAes(String data, Mode mode, Padding padding) {
        return initAes(mode, padding).encryptBase64(data, StandardCharsets.UTF_8);
    }

    /**
     * AES 解密
     * @param data 内容
     * @param mode 解密方式
     * @param padding p
     * @return 解密后内容
     */
    public String decryptAes(String data, Mode mode, Padding padding) {
        byte[] decryptDataBase64 = initAes(mode, padding).decrypt(data);
        return new String(decryptDataBase64, StandardCharsets.UTF_8);
    }

    private AES initAes(Mode mode, Padding padding) {
        EncryptDelegate delegate = delegateLocator.get(EncryptDelegate.class);
        if (Mode.CBC == mode) {
            return new AES(mode,
                    padding == null ? Padding.NoPadding : padding,
                    new SecretKeySpec(delegate.getAESKey().getBytes(), "AES"),
                    new IvParameterSpec(delegate.getAESIv().getBytes()));
        }
        return new AES(mode, padding,
                new SecretKeySpec(delegate.getAESKey().getBytes(), "AES"));
    }
}
