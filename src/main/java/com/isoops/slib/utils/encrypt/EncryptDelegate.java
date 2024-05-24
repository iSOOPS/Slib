package com.isoops.slib.utils.encrypt;

public interface EncryptDelegate {

    /**
     * 设置 恺撒偏移量
     */
    Integer getCaesarCipherIv();

    /**
     * 设置 SM2 加密公钥
     */
    String getSM2PublicKey();

    /**
     * 设置 SM2 加密公钥
     */
    String getSM2PrivateKey();

    /**
     * AES 加密密钥
     */
    String getAESKey();

    /**
     * AES 偏移
     */
    String getAESIv();
}
