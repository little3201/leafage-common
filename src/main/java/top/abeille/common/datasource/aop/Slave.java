package top.abeille.common.datasource.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 从库注解——标识使用从库
 *
 * @author liwenqiang 2019/4/3 10:47
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Slave {
}
