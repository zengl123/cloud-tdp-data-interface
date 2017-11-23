package com.drore.cloud.tdp.schedule;

import com.drore.cloud.tdp.service.KpiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/27  14:50.
 */
@Component
public class Schedule {
    @Autowired
    private KpiServiceImpl kpiService;

    @Scheduled(cron = "0 0/5 * * * ?")
    private void touristNumberMinute5Schedule() {
        kpiService.syncTouristNumberMinute5();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    private void touristNumberDaySchedule() {
        kpiService.syncTouristNumberMinute5();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    private void touristOriginDaySchedule() {
        kpiService.syncTouristOriginDay();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    private void touristsRetentionTimeDaySchedule() {
        kpiService.syncTouristsRetentionTimeDay();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    private void touristsContinuousResidenceTimeMonthSchedule() {
        kpiService.syncTouristsContinuousResidenceTimeMonth();
    }

}
