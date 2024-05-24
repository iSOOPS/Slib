package com.isoops.slib.common.auth;

import com.isoops.slib.common.token.TokenChannel;
import com.isoops.slib.common.token.TokenGenertor;
import com.isoops.slib.common.token.TokenModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class SAuthServiceImpl implements SAuthService {

    @Autowired
    private TokenGenertor tokenGenertor;

    @Resource
    private TokenModel model;

    @Override
    public String getTokenBySingleton(String data, String serviceKey, TokenChannel channel) {
        model.to(data,serviceKey,channel);
        String token = AuthSingleton.singleton().getToken(model.key());
        if (token == null) {
            String unEnToken = getToken(data,serviceKey,channel);
            String aesToken = tokenGenertor.aesEncoding(unEnToken);
            AuthSingleton.singleton().setToken(model.key(),aesToken,6L, TimeUnit.HOURS);
            token = tokenGenertor.sm2Encoding(aesToken);
        }
        return token;
    }

    @Override
    public Boolean checkTokenBySingleton(String token) {
        String aesToken = tokenGenertor.sm2Decoding(token);
        String unToken = tokenGenertor.aesDecoding(aesToken);
        String saveAesToken = AuthSingleton.singleton().getToken(model.to(unToken,true).key());
        return aesToken.equals(saveAesToken);
    }

    @Override
    public String getToken(String data, String serviceKey, TokenChannel channel) {
        return model.to(data,serviceKey,channel).token();
    }

    @Override
    public String encrypt(String str) {
        return tokenGenertor.sm2Encoding(str);
    }

    @Override
    public String decrypt(String str) {
        return tokenGenertor.sm2Decoding(str);
    }
}
