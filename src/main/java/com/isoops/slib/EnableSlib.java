package com.isoops.slib;


import com.isoops.slib.common.SlibConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Slib 主入库使用注解
 * @author samuel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({SlibConfig.class})
public @interface EnableSlib {

}
