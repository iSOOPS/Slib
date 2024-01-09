package com.isoops.slib.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
public class SLog {

    private Boolean enable;

    private static SLog instance;

    private SLog(){}

    public static SLog getInstance(){
        if(instance == null){
            synchronized (SLog.class){
                if(instance == null){
                    instance = new SLog();
                    instance.setEnable(true);
                    instance.msg = "";
                }
            }
        }
        return instance;
    }

    private String msg;

    private static <T> String getJsonStr(T entity) {
        if (entity instanceof String) {
            return (String) entity;
        }
        if (entity instanceof Integer || entity instanceof Float ||
                entity instanceof Double || entity instanceof Long ||
                entity instanceof Boolean) {
            return String.valueOf(entity);
        }
        if (entity instanceof BigDecimal) {
            return entity.toString();
        }
        return JSON.toJSONString(entity);
    }

    public static SLog info() {
        return SLog.getInstance();
    }

    public static <T> SLog msg(String msg,T entity) {
        return SLog.getInstance();
    }

    public static void info(String msg) {
        if (SLog.getInstance().getEnable() && msg != null) {
            log.info(msg);
        }
    }

    public static <T> void info(String msg,T entity) {
        if (SLog.getInstance().getEnable() && entity != null && msg != null) {
            log.info("[" + msg + "]:\n" + getJsonStr(entity) + "\n");
        }
    }

    public static void warn(String msg) {
        if (SLog.getInstance().getEnable() && msg != null) {
            log.warn(msg);
        }
    }

    public static <T> void warn(String msg,T entity) {
        if (SLog.getInstance().getEnable() && entity != null && msg != null) {
            log.warn("[" + msg + "]:\n" + getJsonStr(entity) + "\n");
        }
    }

    public static void error(String msg) {
        if (SLog.getInstance().getEnable() && msg != null) {
            log.error(msg);
        }
    }

    public static <T> void error(String msg,T entity) {
        if (SLog.getInstance().getEnable() && entity != null && msg != null) {
            log.error("[" + msg + "]:\n" + getJsonStr(entity) + "\n");
        }
    }
}
