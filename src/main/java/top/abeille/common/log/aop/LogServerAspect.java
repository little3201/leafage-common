package top.abeille.common.log.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.abeille.common.log.model.LogModel;
import top.abeille.common.log.service.LogService;

/**
 * 日志切面
 *
 * @author liwenqiang 2019/3/18 21:00
 **/
@Aspect
@Component
public class LogServerAspect {

    protected static final Log log = LogFactory.getLog(LogServerAspect.class);

    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final LogService logService;

    @Autowired
    public LogServerAspect(LogService logService) {
        this.logService = logService;
    }

    @Pointcut("@annotation(top.abeille.common.log.aop.LogServer)")
    public void invokeAim() {
    }

    @Before("invokeAim()")
    public void invokeBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        /*  */
    }

    @Around(value = "invokeAim()")
    public Object invokeExecute(ProceedingJoinPoint pjp) {
        return logService.save(new LogModel());
    }

    @AfterReturning(returning = "ret", pointcut = "invokeAim()")
    public void invokeAfterReturning(Object ret) {
        /* 处理完请求，返回内容 */
        log.info("RESPONSE : " + ret);
        log.info("SPEND TIME : " + (System.currentTimeMillis() - startTime.get()));
    }

    @AfterThrowing(throwing = "ex", pointcut = "invokeAim()")
    public void invokeException(JoinPoint joinPoint, Throwable ex) {
        /* 切入方法所属类名 */
        String className = joinPoint.getTarget().getClass().getName();
        /* 切入方法名 */
        String methodName = joinPoint.getSignature().getName();
        /*  目标方法传入的参数 */
        Object[] params = joinPoint.getArgs();
        StringBuilder param = new StringBuilder();
        if (params != null && params.length > 0) {
            param.append("入参为：");
            for (Object p : params) {
                param.append(p).append(",");
            }
            /* 去掉最后一个逗号 */
            String substring = param.toString().substring(0, param.lastIndexOf(","));
        }
        log.error("[Exception]:[" + className + "]" + methodName + ":" + ex);
    }
}
