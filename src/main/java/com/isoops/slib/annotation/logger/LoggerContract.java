package com.isoops.slib.annotation.comtract;


import com.alibaba.fastjson.JSON;
import com.isoops.slib.annotation.Logger;
import com.isoops.slib.annotation.LoggerEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;

/**
 * 日志注解切片实现
 *
 * @author samuel
 */
@Slf4j
@Aspect
@Component
public class LoggerContract extends BasicContract {


    @Pointcut("@annotation(com.isoops.slib.annotation.Logger)")
    public void log() {
    }

    @Around("log() && @annotation(req)")
    public Object doAround(ProceedingJoinPoint joinPoint, final Logger req) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        log.info("==================START RESPONSE[Desc:+" + req.msg() + "]=================");
        if (req.logType() != LoggerEnum.ONLY_REQUEST) {
            log.info("RESPONSE:" + JSON.toJSONString(result));
        }
        long endTime = System.currentTimeMillis();
        long usedTime = endTime - startTime;
        log.info("USED-TIME:" + usedTime + "/ms");
        return result;
    }

    @Before("log() && @annotation(req)")
    public void doBefore(JoinPoint joinPoint, final Logger req) {
        if (getLogBean(joinPoint).logType() == LoggerEnum.ONLY_RESPONSE) {
            return;
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = (HttpServletRequest) attributes.getRequest();
        Object requestObject = null;
        switch (request.getMethod()) {
            case "get": {
                requestObject = getArgsByUri(request,getLogBean(joinPoint).clazz());
                break;
            }
            case "post": {
                List<Object> objects = getArgsModel(joinPoint,getLogBean(joinPoint).clazz());
                requestObject = objects.size() == 1 ? objects.get(0) : objects;
                break;
            }
            default: {
                break;
            }
        }
        //获取所有的请求头
        Enumeration<String> reqHeadInfos = request.getHeaderNames();
        String heads = "";
        while (reqHeadInfos.hasMoreElements()) {
            String headName = reqHeadInfos.nextElement();
            //根据请求头的名字获取对应的请求头的值
            String headValue = request.getHeader(headName);
            heads = heads + headName + "={" + headValue + "}; ";
        }

        log.info("==================START REQUEST[Desc:+" + req.msg() + "]=================");
        log.info("URL:" + request.getRequestURL().toString());
        log.info("IP:" + getIpAddr(request));
        log.info("METHOD:" + request.getMethod());
        log.info("CLASS-METHOD:" + joinPoint.getSignature().getDeclaringTypeName());
        log.info("REQUEST-HEAD:" + heads);
        log.info("REQUEST:" + (requestObject != null ? JSON.toJSONString(requestObject) : "无法解析对象"));
    }

    private Logger getLogBean(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(Logger.class);
    }

    @After("log() && @annotation(req)")
    public void doAfter(final Logger req) {
        log.info("==================END REQUEST/REQUEST[Desc:+" + req.msg() + "]=================");
    }

}
