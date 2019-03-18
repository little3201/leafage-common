package top.abeille.common.log;

/**
 * 操作日志注解
 *
 * @author liwenqiang 2019/3/18 21:40
 **/

import java.lang.annotation.*;

/**
 * 日志注解类
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
@Documented //生成文档
public @interface LogServer {

    String value() default "";
}
