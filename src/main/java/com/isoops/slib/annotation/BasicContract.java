package com.isoops.slib.annotation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class BasicContract {

    /**
     * 获取 http 请求对象
     */
    protected HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("RequestContextHolder is not set");
        }
        return attributes.getRequest();
    }

    /**
     * 获取注解对象
     */
    protected <T extends Annotation>T getAnnotation(JoinPoint joinPoint, Class<T> clazz) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        T annotation = method.getAnnotation(clazz);
        if (annotation == null) {
            Class<?> tagClass = methodSignature.getDeclaringType();
            boolean isAnnotation = tagClass.isAnnotationPresent(clazz);
            if(isAnnotation){
                annotation = tagClass.getAnnotation(clazz);
            }
        }
        return annotation;
    }

    /**
     * 获取请求对象
     */
    protected Object getRequestObject(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        Object requestObject = null;
        switch (Objects.requireNonNull(ContractFacory.stringToMethod(request.getMethod()))) {
            case GET: {
                Map<String,Object> map = new HashMap<>();
                //获取request中uri中提交的参数key
                Enumeration<String> enu = request.getParameterNames();
                while (enu.hasMoreElements()) {
                    String paraName = enu.nextElement();
                    map.put(paraName, request.getParameter(paraName));
                }
                requestObject = map;
                break;
            }
            case POST: {
                List<Object> objects = Arrays.asList(joinPoint.getArgs());
                requestObject = objects.size() == 1 ? objects.get(0) : objects;
                break;
            }
            default: {
                break;
            }
        }
        return requestObject;
    }

    protected String getHeaderString() {
        HttpServletRequest request = getRequest();
        Enumeration<String> requestHeadInfos = request.getHeaderNames();
        StringBuilder heads = new StringBuilder();
        while (requestHeadInfos.hasMoreElements()) {
            String headName = requestHeadInfos.nextElement();
            //根据请求头的名字获取对应的请求头的值
            String headValue = request.getHeader(headName);
            heads = new StringBuilder(heads + headName + "={" + headValue + "}; ");
        }
        return heads.toString();
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
}
