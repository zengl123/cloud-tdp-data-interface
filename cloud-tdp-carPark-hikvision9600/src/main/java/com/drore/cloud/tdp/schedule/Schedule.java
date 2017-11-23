package com.drore.cloud.tdp.schedule;

import com.drore.cloud.tdp.service.impl.CarParkServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  16:20.
 */
@Component
public class Schedule {
    @Autowired
    private CarParkServiceImpl carParkService;

    @Scheduled(cron = "${syncCarParkInfo}")
    public void carParkInfoSchedule() {
        carParkService.syncCarParkInfo();
    }
}
