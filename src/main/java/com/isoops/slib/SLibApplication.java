package com.isoops.slib;

import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.pojo.SFieldAlias;
import com.isoops.slib.utils.SBeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.util.Version;

/**
 * @author samuel
 */
@SpringBootApplication
public class SLibApplication {

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Test extends AbstractObject {
        @SFieldAlias(name = "name1")
        private String name;
        private Integer age;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Test1 extends AbstractObject {
        private String name;
        private String name1;
        private Integer age;
    }

    public static void main(String[] args) {
//        SpringApplication.run(SLibApplication.class, args);

        Test test = new Test();
        test.setAge(11);
        test.setName("222");
        Test1 test1 = new Test1();
        test1.setName("333");
        test1.setName1("444");

//        SBeanUtil.aliasFillUnReplaceClone(test, test1);
        SBeanUtil.clone(test, test1);
        String versionString = System.getProperty("java.version");
        Version version = Version.parse(versionString);
        boolean aaa = version.compareTo(Version.parse("8")) > 0;
        System.out.println(test1);
    }




}
