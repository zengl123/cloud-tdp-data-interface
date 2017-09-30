package com.drore.cloud.tdp.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/29  16:56.
 */
@Component
@Configuration
@PropertySource("classpath:schedule.properties")
public class ExampleSchedule {
    @Scheduled(cron = "${exampleQuartz}")
    public void exampleQuartz() {
        System.out.println("nowDay= " + LocalDate.now() +" "+ LocalTime.now().withNano(0));
    }
}
