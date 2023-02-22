package com.isoops.slib.annotation.comtract;

import com.isoops.slib.utils.SObjectUtil;
import org.aspectj.lang.JoinPoint;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class BasicContract {

    /**
     * 获取切片拦截的对象（body请求用）
     * @param joinPoint 切片对象
     * @param clazz 对象class类型
     * @param <T> 类型
     * @return f
     */
    protected <T> List<T> getArgsModel(JoinPoint joinPoint, Class<T> clazz){
        Object[] args = joinPoint.getArgs();
        List<T> result = new ArrayList<>();
        for (Object arg : args) {
            if (arg.getClass().equals(clazz)) {
                result.add((T) arg);
            }
        }
        return result.size() < 1 ? null : result;
    }

    /**
     * 获取切片拦截URL并反射对象（get请求用）
     * @param request r
     * @param clazz 对象class类型
     * @param <T> 对象泛型
     * @return f
     * @throws Exception e
     */
    protected <T> T getArgsByUri(HttpServletRequest request, Class<T> clazz) {
        //初始化泛型对象
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            //获取request中uri中提交的参数key
            Enumeration<String> enu = request.getParameterNames();
            while (enu.hasMoreElements()) {
                String paraName = enu.nextElement();
                //写入值，若值不存在于该对象，则不会写入
                SObjectUtil.setProperty(entity, paraName, request.getParameter(paraName));
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getUrlParameter(HttpServletRequest request){
        StringBuilder url = new StringBuilder(request.getRequestURL().toString() + "?");
        Enumeration<String> enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            url.append(paraName).append("=").append(request.getParameter(paraName)).append("&");
        }
        return url.substring(0,url.length()-1);
    }

    /**
     * 获取真实的ip
     * @param request r
     * @return f
     */
    protected String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取调用接口名称
     * @param request r
     * @return f
     */
    protected String getInterfaceName(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String[] sArray = uri.split("/");
        if (sArray.length > 2) {
            return sArray[2].substring(0, sArray[2].length() - 4);
        }
        return null;
    }
}
