
package com.starv.task.log.constant;

public enum OperateType {
    /**
     * 当操作类型为created时，要求方法返回格式为：return ok("具体操作信息", new MapBean("此处为实体主键属性名称", primaryKeyValue));
     */
    create, modify, delete
}