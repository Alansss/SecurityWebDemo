package com.starv.task.configuration;

import com.starv.task.api.chat.ChatServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        int port=8088; //服务端默认端口
        new Thread(()->{
            try {
                new ChatServer().bind(port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}