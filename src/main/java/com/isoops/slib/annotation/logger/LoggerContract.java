package com.isoops.slib.annotation.logger;


import com.isoops.slib.annotation.BasicContract;
import com.isoops.slib.annotation.RequestEasyModel;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 日志注解切片实现
 *
 * @author samuel
 */
@Slf4j
@Aspect
@Component
public class LoggerContract extends BasicContract {

    @Autowired
    private LoggerFactory loggerFactory;

    @Pointcut("@annotation(com.isoops.slib.annotation.logger.Logger) || @within(com.isoops.slib.annotation.logger.Logger)")
    public void log() {
    }

    @Around("log()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestEasyModel requestEasyModel = new RequestEasyModel(getRequest());
        Logger logger = getAnnotation(joinPoint,Logger.class);
        long startTime = System.currentTimeMillis();
        Object requestObject = getRequestObject(joinPoint);

        loggerFactory.beforLogger(requestEasyModel,requestObject,logger.level(),startTime);
        Object result = joinPoint.proceed();
        loggerFactory.afterLogger(result,logger.level(),startTime);

        return result;
    }
}
