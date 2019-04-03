package com.starv.task.log.aspect;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class WebLogAspect {

    private static final Gson GSON = new Gson();

    //com.kzj.kzj_rabbitmq.controller 包中所有的类的所有方法切面
    //@pointcut("execution(public * com.kzj.kzj_rabbitmq.controller.*.*(..))")

    //只针对 MessageController 类切面
    //@pointcut("execution(public * com.kzj.kzj_rabbitmq.controller.MessageController.*(..))")

    /**
     * 统一切点,对com.kzj.kzj_rabbitmq.controller及其子包中所有的类的所有方法切面
     */
    @Pointcut("execution(public * com.starv.task.api.controller..*.*(..))")
    public void pointcut() {
    }

    /**
     * 前置通知
     */
    @Before("pointcut()")
    public void beforeMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();
        log.info(className + "." + methodName + "() Request Args:" + GSON.toJson(args));
    }

    //@After: 后置通知
    @After("pointcut()")
    public void afterMethod(JoinPoint joinPoint) {
        //log.info("调用了后置通知");
    }

    //@AfterRunning: 返回通知 rsult为返回内容
    @AfterReturning(value = "pointcut()", returning = "result")
    public void afterReturningMethod(JoinPoint joinPoint, Object result) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        log.info(className + "." + methodName + "() Response Args:" + GSON.toJson(result));
    }

    //@AfterThrowing: 异常通知
    @AfterThrowing(value = "pointcut()", throwing = "e")
    public void afterReturningMethod(JoinPoint joinPoint, Exception e) {
        //log.info("调用了异常通知");
    }

    //@Around：环绕通知
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        //log.info("around执行方法之前");
        // 计时并调用目标函数
        //获取目标类名称
        String clazzName = pjp.getTarget().getClass().getName();
        //获取目标类方法名称
        String methodName = pjp.getSignature().getName();
        long start = System.currentTimeMillis();
        Object object = pjp.proceed();
        long time = System.currentTimeMillis() - start;
        log.info(clazzName + "." + methodName + "() ConsumeTime:" + time + " ms");
        //log.info("around执行方法之后--返回值：" + object);
        return object;
    }


}