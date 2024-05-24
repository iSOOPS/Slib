package com.isoops.slib.annotation.blocker;


import com.isoops.slib.DelegateLocator;
import com.isoops.slib.annotation.BasicContract;
import com.isoops.slib.annotation.RequestEasyModel;
import com.isoops.slib.annotation.logger.LoggerFactory;
import com.isoops.slib.utils.SLog;
import com.isoops.slib.utils.SUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志注解切片实现
 *
 * @author samuel
 */
@Slf4j
@Aspect
@Component
public class BlockerContract extends BasicContract {

    @Autowired
    private DelegateLocator delegateLocator;

    @Autowired
    private LoggerFactory loggerFactory;

    @Pointcut("@annotation(com.isoops.slib.annotation.blocker.Blocker) || @within(com.isoops.slib.annotation.blocker.Blocker)")
    public void methodBlocker() {
    }

    @Around("methodBlocker()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestEasyModel requestEasyModel = new RequestEasyModel(getRequest());
        Blocker blocker = getAnnotation(joinPoint,Blocker.class);
        long startTime = System.currentTimeMillis();
        Object requestObject = getRequestObject(joinPoint);

        loggerFactory.beforLogger(requestEasyModel,requestObject,blocker.logLevel(),startTime);
        Object result = joinPoint.proceed();
        loggerFactory.afterLogger(result,blocker.logLevel(),startTime);

        return result;
    }

    @Before("methodBlocker()")
    public void doBefore(JoinPoint joinPoint) {
        BlockerDelegate delegate = delegateLocator.get(BlockerDelegate.class);
        HttpServletRequest request = getRequest();
        RequestEasyModel requestEasyModel = new RequestEasyModel(request);
        String token = request.getHeader(SUtil.isBlank(delegate.headerTokenName()) ? "token" : delegate.headerTokenName());
        SLog.info("==== TOKEN:" + token);

        Blocker blocker = getAnnotation(joinPoint,Blocker.class);
        if (blocker == null) {
            return;
        }
        delegate.before(requestEasyModel,token,request);

    }

    @After("methodBlocker()")
    public void doAfter(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        BlockerDelegate delegate = delegateLocator.get(BlockerDelegate.class);
        delegate.after(request);
    }

}
