package top.abeille.common.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author liwenqiang 2019/3/18 21:00
 **/
@Aspect
@Component
public class ServerLogAspect {

    protected static final Log log = LogFactory.getLog(ServerLogAspect.class);

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(top.abeille.common.log.AbeilleLog)")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        // 省略日志记录内容
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        log.info("RESPONSE : " + ret);
        log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }

    @AfterThrowing(throwing = "ex", pointcut = "webLog()")
    public void exceptionDispose(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getTarget().getClass().getName(); //切入方法所属类名
        String methodName = joinPoint.getSignature().getName(); //切入的方法名
        Object[] params = joinPoint.getArgs(); //目标方法传入的参数
        String param = "入参为：";

        if (params != null && params.length > 0) {
            for (Object p : params) {
                param += p + ",";
            }
            param = param.substring(0, param.lastIndexOf(","));
        }
        log.error("[Exception]:[" + className + "]" + methodName + ":" + ex);
        System.out.println("【" + className + "】:" + methodName + "执行时出现异常：" + ex + "。");
        System.out.println(param);
    }
}
