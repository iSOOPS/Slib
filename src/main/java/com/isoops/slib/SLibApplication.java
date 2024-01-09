package com.isoops.slib;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.isoops.slib.annotation.SFieldAlias;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.utils.SLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author samuel
 */
@SpringBootApplication
public class SLibApplication {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TestAAA extends AbstractObject {
        private String test1;
        private Integer test2;
        private Long test3;
        @SFieldAlias(name = "t4")
        private List<Integer> test4;
        private Long test5;


        public TestAAA() {
            this.test1 = "aaaaabbbbb";
            this.test2 = 11111;
            this.test3 = 22222L;
            this.test4 = Collections.singletonList(123);
            this.test5 = 5555L;

        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class TestBBB  extends AbstractObject {
        private String test1;
        private int test2;
        @SFieldAlias(name = "test3")
        private Long t3;

        @SFieldAlias(name = "test3")
        private String t4;
        @SFieldAlias(name = "test5")
        private Integer t5;
    }

    public static void main(String[] args) {
//        SpringApplication.run(SLibApplication.class, args);
        TestAAA aaa = new TestAAA();
        TestBBB bbb = aaa.aliasClone(TestBBB.class);

        SLog.info("aaa",aaa);
        SLog.info("bbb",bbb);


        List<TestAAA> list = new ArrayList<>();
        int rrr = 11;
        while (rrr > 0) {
            TestAAA aaas = new TestAAA();
            aaas.setTest1(String.valueOf(rrr));
            list.add(aaas);
            rrr --;
        }

        List<TestAAA> aasdfasdf = new ArrayList<>();

        int size = list.size() / 5 + 1;
        for (int i=0;i<size;i++) {
            int offset = i * 5;
            List<TestAAA> iList = dispossSize(list,offset);
            if (iList.isEmpty()) {
                break;
            }
            aasdfasdf.addAll(iList);
        }
        SLog.info("111",aasdfasdf.size());

//        Date akkk = getLastDate(DateUtil.beginOfYear(new Date()));
//        Date akkk = getLastDate(new Date());
        Date akkk = DateUtil.offset(DateUtil.beginOfYear(new Date()),DateField.DAY_OF_YEAR,-1);
        SLog.info("akkk",akkk);

    }

    private static List<TestAAA> dispossSize(List<TestAAA> list,Integer offset) {
        List<TestAAA> inList = new ArrayList<>();
        int size = Math.min(offset + 5, list.size());
        for (int i = offset ; i< size ; i ++) {
            inList.add(list.get(i));
        }
        return inList;
    }

    public static Date getLastDate(Date date) {
        //获取前一天的记录/处理跨年情况
        String monthAndDay = DateUtil.format(date, "MM-dd");
        String year = DateUtil.format(date, "yyyy");
        Date lastDate;
        if ("01-01".equals(monthAndDay)) {
            lastDate = DateUtil.parseDate(year + "-12-31");
        }
        else {
            lastDate = DateUtil.offset(date, DateField.DAY_OF_YEAR, -1);
        }
        return lastDate;
    }

}
