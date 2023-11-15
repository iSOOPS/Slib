package com.isoops.slib;

import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.utils.SLog;
import com.isoops.slib.utils.SUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author samuel
 */
@SpringBootApplication
public class SLibApplication {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TestAAA extends AbstractObject {
        private Integer numbers;
        private String name;
        public TestAAA() {
            this.numbers = 123;
            this.name = "{\"signData\":\"B4/1Ej/j7/%2BJe2gmmw2/GiIqm5kVpyu5kvRXBlR75yZuJUF8AkUONDRdTCegJZLtwtUfpMUuNvFLRuVie0X3664xjX2TctG5PgJQ3FIdPLo1ugHRTpY%2BlbNf%2BwW16bhyEr1M0EZzFToNC/an6ZlXp%2BPzbznuHTfjFLAj3NinqoZWgjNnfQZOVNyKq1/CNidDbnr08c3gWeoAnEzN%2BJqv4zwUVBbJwVNPCIA0jMGoR0kffFsKp5C/H17n9a3Sny51k5p7XucwywOQ89du7H6B4LiurQmEQfz7hzj7S81S2tbOQW2SqMHX5iAtwtecfFx3SPxZleen2ZCf9jTbARfEqA==\",\"notifyData\":\"eyJ0eENvZGUiOiIwMiIsInJlc3BDb2RlIjoiMDAwMDAiLCJyZXNwTXNnIjoi5Lqk5piT5oiQ5YqfIiwidHJ4QW1vdW50IjowLjAxLCJwYXlNZXRob2QiOiIwOSIsInBheU1vZGUiOiIwMiIsIm9yZGVyU3RhdHVzIjoiMDIiLCJyZWZ1bmRTdGF0dXMiOm51bGwsIm9yZGVySWQiOiJKTTE3OTI3MzM3MjlUMDE1MjIiLCJyZWZ1bmRJZCI6bnVsbCwib3JkZXJDcmVhdGVEYXRlIjoiMjAyMzA4MjEiLCJvcmRlckNyZWF0ZVRpbWUiOiIxNTo0MDozMyIsImNvbXBsZXRlRGF0ZSI6IjIwMjMwODIxIiwiY29tcGxldGVUaW1lIjoiMTU6NDA6MzgiLCJhcHBJZCI6IjExMDAwMDAwMDAwMDAwMDA1MTg0IiwidmVuZG9ySWQiOiIyMDIzMDcwNjEwNTkwMDEiLCJ1c2VySWQiOiIxNTI3MjExOSIsIm1lck5hbWUiOiLlub/opb/ljZflroHln47luILniankuJrmnInpmZDlhazlj7giLCJzdWJNZXJOYW1lIjoi5bm/6KW/5Y2X5a6B5Z%2BO5biC54mp5Lia5pyJ6ZmQ5YWs5Y%2B4Iiwib3JkZXJObyI6bnVsbCwicmVhbFVuZnJlZXplQW10IjpudWxsLCJ2ZW5kb3JEaXNjb3VudCI6MC4wMCwiYmFua0Rpc2NvdW50IjowLjAwLCJpc0pmdERpc2NvdW50IjoiMCIsImpmdERpc2NvdW50QW10IjowLjAwLCJzaG9wQ29kZSI6bnVsbCwiaWNiY09yZGVySWQiOiIyMTAyNjUxMzQyMTEwMDA1NDIzMDgyMTAwMDMxNDUiLCJjb21tZW50IjpudWxsLCJleHRlbnNpb24iOm51bGwsImVycm9yQ29kZSI6bnVsbCwiak9yZGVySWQiOiIwMjAyMzA4MjEwMDIyMjU4OTg2NSIsImpQYXJlbnRSZWZ1bmRJZCI6bnVsbCwialJlZnVuZElkIjpudWxsLCJjb25maXJtU3RhdHVzIjpudWxsLCJtZXJDb25maXJtSWQiOm51bGwsInN1Yk9yZGVySW5mb0xpc3QiOm51bGwsInN1YlJlZnVuZHMiOm51bGwsInBheVNwbGl0RGV0YWxsQmVhbkxpc3QiOm51bGwsInNwbGl0QWNjb3VudFN0YXR1cyI6bnVsbCwibWVyU3BsaXRJZCI6bnVsbCwiY291cG9uSWRMaXN0IjpudWxsLCJzbWFydFdpdGhkcmF3U3RhdHVzIjpudWxsLCJ0aGlyZFRyYWRlTm8iOiI0MjAwMDAxODg4MjAyMzA4MjEyNzY1OTQzNDMwIiwid2l0aGRyYXdEYXRlR3JvdXAiOm51bGwsImN1c3RJZCI6bnVsbCwicGF5QW1vdW50IjowLjAxfQ==\"}";
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TestBBB  extends AbstractObject {
        private int numbers;
        private String name;
    }

    public static void main(String[] args) {
//        SpringApplication.run(SLibApplication.class, args);
        TestAAA aaa = new TestAAA();

        aaa = null;

        if (SUtil.isNotBlank(aaa,TestAAA::getName)) {
            SLog.info("1111");
        }
        SLog.info("222");
    }

}
