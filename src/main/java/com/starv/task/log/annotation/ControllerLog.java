package com.starv.task.log.annotation;

import com.starv.task.log.constant.OperateModule;
import com.starv.task.log.constant.OperateType;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ControllerLog {
    /**
     * 操作描述 业务名称business
     */
    String description() default "";
 
    /**
     * 操作模块
     */
    OperateModule module();
 
    /**
     * 操作类型 create modify delete
     */
    OperateType opType();
 
    /**
     * 主键入参参数名称，入参中的哪个参数为主键
     */
    String primaryKeyName() default "";
 
    /**
     * 主键在参数中的顺序，从0开始，默认0
     */
    int primaryKeySort() default 0;
 
    /**
     * 主键所属类
     */
    Class<?> primaryKeyBelongClass();
 
}