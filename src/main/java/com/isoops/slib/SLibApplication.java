package com.isoops.slib;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.isoops.slib.pojo.AbstractObject;
import com.isoops.slib.pojo.IFunction;
import com.isoops.slib.utils.SObjectUtil;
import com.isoops.slib.utils.SUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author samuel
 */
@SpringBootApplication
public class SLibApplication {



    public static void main(String[] args) {
//        SpringApplication.run(SLibApplication.class, args);
//        List<String> list = new ArrayList<>();
    }

}
