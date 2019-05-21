/*
 * Copyright (c) 2019. Abeille All Right Reserved.
 */
package top.abeille.common.log.aop;


import java.lang.annotation.*;

/**
 * 日志服务注解
 *
 * @author liwenqiang 2019/3/18 21:40
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogServer {

    String value() default "";
}
