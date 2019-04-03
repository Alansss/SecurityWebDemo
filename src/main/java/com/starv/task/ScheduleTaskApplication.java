package com.starv.task;

import com.starv.task.configuration.ApplicationStartup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ScheduleTaskApplication {

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(ScheduleTaskApplication.class);
		springApplication.addListeners(new ApplicationStartup());
		springApplication.run(args);
	}

}

