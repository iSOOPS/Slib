package com.isoops.slib.annotation.blocker;

import com.isoops.slib.annotation.RequestEasyModel;
import com.isoops.slib.common.auth.SAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class BlockerDelegateDefaultImpl implements BlockerDelegate {

    @Autowired
    private SAuthService service;

    @Override
    public String headerTokenName() {
        return "a-token";
    }

    @Override
    public void before(RequestEasyModel requestEasyModel, String token, HttpServletRequest request) {
        if (!service.checkTokenBySingleton(token)) {
            throw new RuntimeException("a-token 已过期");
        }
    }

    @Override
    public void after(HttpServletRequest request) {

    }
}
