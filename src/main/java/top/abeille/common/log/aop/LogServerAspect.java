package top.abeille.common.log.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import top.abeille.common.log.model.LogInfoModel;
import top.abeille.common.log.service.LogInfoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 日志服务切面
 *
 * @author liwenqiang 2019/3/18 21:00
 **/
@Aspect
@Component
public class LogServerAspect {

    protected static final Logger log = LoggerFactory.getLogger(LogServerAspect.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private final LogInfoService logInfoService;

    @Autowired
    public LogServerAspect(LogInfoService logInfoService) {
        this.logInfoService = logInfoService;
    }

    @Pointcut("@annotation(top.abeille.common.log.aop.LogServer)")
    public void aspectAim() {
    }

    @Before("aspectAim()")
    public void aspectBefore(JoinPoint joinPoint) {
        for (Object object : joinPoint.getArgs()) {
            if (object instanceof MultipartFile
                    || object instanceof HttpServletRequest
                    || object instanceof HttpServletResponse) {
                continue;
            }
            try {
                if (log.isDebugEnabled()) {
                    log.debug(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName()
                            + " : request parameter : " + objectMapper.writeValueAsString(object));
                }
            } catch (Exception e) {
                log.error("invokeBefore occurred error: {}", e);
            }
        }
    }

    @Around(value = "aspectAim()")
    public ResponseEntity aspectExecute(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed = pjp.proceed();
        String s = objectMapper.writeValueAsString(proceed);
        log.info("执行结果：" + s);
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        method.getAnnotation(LogServer.class);
        logInfoService.save(new LogInfoModel());
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @AfterReturning(returning = "obj", pointcut = "aspectAim()")
    public void aspectAfterReturning(Object obj) {
        /* 处理完请求，返回内容 */
        log.info("RESPONSE : " + obj);
    }

    @AfterThrowing(throwing = "throwable", pointcut = "aspectAim()")
    public void aspectException(JoinPoint joinPoint, Throwable throwable) {
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
            String result = param.toString().substring(0, param.lastIndexOf(","));
            log.info("请求入参为：" + result);
        }
        log.error("[Exception]:[" + className + "]" + methodName + ":" + throwable);
    }
}
