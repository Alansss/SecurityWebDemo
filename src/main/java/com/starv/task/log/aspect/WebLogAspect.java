package com.starv.task.log.aspect;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.starv.task.entity.OperationLog;
import com.starv.task.log.annotation.OperationLogDetail;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @Pointcut("execution(public * com.starv.task.api.test.controller..*.*(..))")
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
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //log.info("around执行方法之前");
        // 计时并调用目标函数
        //获取目标类名称
        String clazzName = joinPoint.getTarget().getClass().getName();
        //获取目标类方法名称
        String methodName = joinPoint.getSignature().getName();
        long time = System.currentTimeMillis();
        Object res = null;
        try {
            res = joinPoint.proceed();
            time = System.currentTimeMillis() - time;
            return res;
        }finally {
            log.info(clazzName + "." + methodName + "() ConsumeTime:" + time + " ms");
//            try {
                //方法执行完成后增加日志
//                addOperationLog(joinPoint, res, time);
//            }catch (Exception e){
//                log.warn("LogAspect 操作失败：" + e.getMessage());
//                e.printStackTrace();
//            }
            //log.info("around执行方法之后--返回值：" + object);
        }
    }

    private void addOperationLog(JoinPoint joinPoint, Object res, long time){
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        OperationLog operationLog = new OperationLog();
        operationLog.setRunTime(time);
        operationLog.setReturnValue(JSON.toJSONString(res));
        operationLog.setId(UUID.randomUUID().toString());
        operationLog.setArgs(JSON.toJSONString(joinPoint.getArgs()));
        operationLog.setCreateTime(new Date());
        operationLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        operationLog.setUserId("#{currentUserId}");
        operationLog.setUserName("#{currentUserName}");
        OperationLogDetail annotation = signature.getMethod().getAnnotation(OperationLogDetail.class);
        if(annotation != null){
            operationLog.setLevel(annotation.level());
            operationLog.setDescribe(getDetail(((MethodSignature)joinPoint.getSignature()).getParameterNames(),joinPoint.getArgs(),annotation));
            operationLog.setOperationType(annotation.operationType().getValue());
            operationLog.setOperationUnit(annotation.operationUnit().getValue());
        }

        log.info("记录日志:" + operationLog.toString());
//        operationLogService.insert(operationLog);
    }

    /**
     * 对当前登录用户和占位符处理
     * @param argNames 方法参数名称数组
     * @param args 方法参数数组
     * @param annotation 注解信息
     * @return 返回处理后的描述
     */
    private String getDetail(String[] argNames, Object[] args, OperationLogDetail annotation){

        Map<Object, Object> map = new HashMap<>(4);
        for(int i = 0;i < argNames.length;i++){
            map.put(argNames[i],args[i]);
        }

        String detail = annotation.detail();
        try {
            detail = "'" + "#{currentUserName}" + "'=》" + annotation.detail();
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object k = entry.getKey();
                Object v = entry.getValue();
                detail = detail.replace("{{" + k + "}}", JSON.toJSONString(v));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return detail;
    }

}