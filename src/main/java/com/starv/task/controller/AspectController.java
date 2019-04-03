package com.starv.task.controller;

import com.starv.task.configuration.annotation.JsonArg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: Wang Lijie
 * @Date: 2019/4/2 16:03
 * @Description: TODO
 */
@RestController
@Slf4j
public class AspectController {

    @RequestMapping("/log")
    public String printLog(@JsonArg String str) {
        log.info("run method>>>>>>>>>>>>>>>>>>>>>" + str);
        return "aaa";
    }

    @RequestMapping("/log2")
    public String printLog2(String str) {
        log.info("run method>>>>>>>>>>>>>>>>>>>>>" + str);
        return "aaa";
    }

    @RequestMapping("/whoim")
    public Object whoIm(){
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }


}
