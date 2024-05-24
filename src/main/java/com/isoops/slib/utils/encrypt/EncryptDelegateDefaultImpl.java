package com.isoops.slib.utils.encrypt;

import org.springframework.stereotype.Service;

@Service
public class EncryptDelegateDefaultImpl implements EncryptDelegate{

    @Override
    public Integer getCaesarCipherIv() {
        return 3;
    }

    @Override
    public String getSM2PublicKey() {
        return "BICWkklC+O/UlmuSPu5+GvC3ejXwpPa9ytzLZyBJcbRCYJ9HtuaV7cqRobmxFaCTIlQlfu7xSRuLN7ynjJ6oT04=";
    }

    @Override
    public String getSM2PrivateKey() {
        return "AOELZt/zEFF1IsLvEzw97h6iPl+GuaX85ODJ/JiGCb1X";
    }

    @Override
    public String getAESKey() {
        return "jNf256zAdOZt6Wnb";
    }

    @Override
    public String getAESIv() {
        return "1aEr8Ij9gEHpfKsl";
    }
}
