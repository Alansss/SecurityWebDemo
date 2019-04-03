package com.starv.task.log.constant;

public enum OperateModule {
    SightMerchant("商家管理"), AdminUser("用户管理"), Tour("行程管理"), UserTour("用户行程");
    private String text;
 
    OperateModule(String text) {
        this.text = text;
    }
 
    public String getText() {
        return text;
    }
}