package com.isoops.slib.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class SLog {

    private Boolean enable;

    private Map<String,Boolean> channelEnables;

    private static SLog instance;

    private SLog(){}

    private static Boolean enalble(String channel) {
        Boolean status = SLog.getInstance().getChannelEnables().get(channel);
        return status != null && status && SLog.getInstance().enable;
    }

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

    @SafeVarargs
    private static <T> String buildString(String msg, T ... args) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        // 获取调用printCurrentMethodInfo()方法的栈帧，通常是主线程调用这个方法的地方
        StackTraceElement caller = stackTraceElements[stackTraceElements.length -1]; // 注意：数组索引从0开始，所以这里是2，而不是1
        msg = SUtil.isBlank(msg) ? "SLOG::" : msg;
        StringBuilder builder = new StringBuilder(caller.getClassName()).append(".").append(caller.getMethodName());
        builder.append("[").append(caller.getLineNumber()).append("]");
        builder.append("[").append(msg).append("]");
        if (args == null) {
            return builder.toString();
        }
        for (T t:args) {
            builder.append("\n").append(getJsonStr(t));
        }
        return builder.toString();
    }

    public static SLog getInstance(){
        if(instance == null){
            synchronized (SLog.class){
                if(instance == null){
                    instance = new SLog();
                    instance.enable = true;
                    instance.channelEnables = new HashMap<>();
                    instance.channelEnables.put("#",true);
                }
            }
        }
        return instance;
    }

    @Data
    public static class Selector {

        private String channel;

        public void info(String msg) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.info(buildString(msg));
        }

        public <T> void info(String msg,T entity) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.info(buildString(msg,entity));
        }

        public <T> void info(String msg,T ...args) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.info(buildString(msg,args));
        }

        public void warn(String msg) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.warn(buildString(msg));
        }

        public <T> void warn(String msg,T entity) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.warn(buildString(msg,entity));
        }

        public <T> void warn(String msg,T ...args) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.warn(buildString(msg,args));
        }

        public void error(String msg) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.error(buildString(msg));
        }

        public <T> void error(String msg,T entity) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.error(buildString(msg,entity));
        }

        public <T> void error(String msg,T ...args) {
            if (!SLog.enalble(channel)){
                return;
            }
            log.error(buildString(msg,args));
        }

    }

    public static void enable() {
        SLog.getInstance().enable = true;
    }

    public static void disable() {
        SLog.getInstance().enable = false;
    }

    public static void channelEnable(String channel) {
        SLog.getInstance().getChannelEnables().put(channel,true);
    }

    public static void channelDisable(String channel) {
        SLog.getInstance().getChannelEnables().put(channel,false);
    }

    public static Map<String,Boolean> enableMap() {
        return SLog.getInstance().getChannelEnables();
    }

    public static Selector channel(String channel) {
        Selector selector = new Selector();
        selector.setChannel(channel);
        return selector;
    }

    public static void info(String msg) {
        SLog.channel("#").info(msg);
    }

    public static <T> void info(String msg,T entity) {
        SLog.channel("#").info(msg,entity);
    }

    public static <T> void info(String msg,T ...args) {
        SLog.channel("#").info(msg,args);
    }


    public static void warn(String msg) {
        SLog.channel("#").warn(msg);
    }

    public static <T> void warn(String msg,T entity) {
        SLog.channel("#").warn(msg,entity);
    }

    public static <T> void warn(String msg,T ...args) {
        SLog.channel("#").warn(msg,args);
    }

    public static void error(String msg) {
        SLog.channel("#").error(msg);
    }

    public static <T> void error(String msg,T entity) {
        SLog.channel("#").error(msg,entity);
    }

    public static <T> void error(String msg,T ...args) {
        SLog.channel("#").error(msg,args);
    }
}
