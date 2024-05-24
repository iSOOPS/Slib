package com.isoops.slib.annotation.blocker;

import com.isoops.slib.annotation.RequestEasyModel;

import javax.servlet.http.HttpServletRequest;

public interface BlockerDelegate {

    /**
     * 获取拦截 token 名称
     */
    String headerTokenName();

    /**
     * 拦截前操作
     */
    void before(RequestEasyModel requestEasyModel,
                String token,
                HttpServletRequest request);

    /**
     * 拦截后操作
     */
    void after(HttpServletRequest request);
}
