package com.starv.task.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class shandongMobileJob {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron="0 48 15 ? * THU")
    public void runTask(){
        logger.info("1111111111111111111111111");

//        Integer query = jdbcTemplate.queryForObject("select count(1) from mobile_title where day = ? limit 3", Integer.class,
//                DateTime.now().plus(-1).toString("yyyyMMdd"));
//
//        if (query==0){
//            System.out.println("11111111111");
//        }
//        HttpClientUtil.doGet("");
    }

    @Async
    public void runTask2(){

    }

}
