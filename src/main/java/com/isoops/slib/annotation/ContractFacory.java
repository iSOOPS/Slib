package com.isoops.slib.annotation;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

public class ContractFacory {

    /**
     * 获取真实的ip
     * @param request 请求对象
     * @return ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 请求类型转换
     * @param method str 请求类型
     * @return RequestMethod 请求类型
     */
    public static RequestMethod stringToMethod(String method) {
        switch (method) {
            case "get":
            case "GET":
                return RequestMethod.GET;
            case "post":
            case "POST":
                return RequestMethod.POST;
            case "put":
            case "PUT":
                return RequestMethod.PUT;
            case "delete":
            case "DELETE":
                return RequestMethod.DELETE;
            case "head":
            case "HEAD":
                return RequestMethod.HEAD;
            case "patch":
            case "PATCH":
                return RequestMethod.PATCH;
            case "options":
            case "OPTIONS":
                return RequestMethod.OPTIONS;
            case "trace":
            case "TRACE":
                return RequestMethod.TRACE;
            default:
                return null;
        }
    }
}
