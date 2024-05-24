package com.isoops.slib.annotation.logger;

import com.alibaba.fastjson.JSON;
import com.isoops.slib.annotation.RequestEasyModel;
import com.isoops.slib.utils.SLog;
import org.springframework.stereotype.Component;

@Component
public class LoggerFactory {

    public void beforLogger(RequestEasyModel requestEasyModel,
                            Object requestObject,
                            LogEnum logEnum ,
                            long startTime) {
        switch (logEnum) {
            case Easy: {
                SLog.info("==== START LOG BEFORE ====");
                SLog.info("==== START-TIME:" + startTime + "/ms");
                SLog.info("==== URI:" + requestEasyModel.getUrl());
                break;
            }
            case Full: {
                SLog.info("==== START LOG BEFORE ====");
                SLog.info("==== START-TIME:" + startTime + "/ms");
                SLog.info("==== IP:" + requestEasyModel.getIp());
                SLog.info("==== METHOD:" + requestEasyModel.getMethod());
                SLog.info("==== URL:" + requestEasyModel.getUrl());
                SLog.info("==== URI:" + requestEasyModel.getUri());
                SLog.info("==== REQUEST-HEAD:" + requestEasyModel.getHeadersString());
                SLog.info("==== REQUEST-OBJECT:" + JSON.toJSONString(requestObject));
                break;
            }
            case None:
            default:
                break;
        }
    }

    public void afterLogger(Object responseObject,
                            LogEnum logEnum ,
                            long startTime) {
        long endTime = System.currentTimeMillis();
        switch (logEnum) {
            case Easy: {
                SLog.info("==== END-TIME:" + endTime + "/ms");
                SLog.info("==== USED-TIME" + (endTime - startTime) + "/ms");
                SLog.info("==== END LOG AFTER ====");
                break;
            }
            case Full: {
                SLog.info("==== REQUEST-OBJECT:" + JSON.toJSONString(responseObject));
                SLog.info("==== END-TIME:" + endTime + "/ms");
                SLog.info("==== USED-TIME:" + (endTime - startTime) + "/ms");
                SLog.info("==== END LOG AFTER ====");
                break;
            }
            case None:
            default:
                break;
        }
    }
}
