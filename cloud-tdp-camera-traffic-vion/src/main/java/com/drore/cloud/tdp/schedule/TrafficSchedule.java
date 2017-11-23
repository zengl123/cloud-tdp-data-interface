package com.drore.cloud.tdp.schedule;

import com.drore.cloud.tdp.service.CameraTrafficServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/14  10:39.
 */
@Component
public class TrafficSchedule {
    @Autowired
    private CameraTrafficServiceImpl cameraTrafficService;
    @Scheduled(cron = "0 0 12 * * ?")
    public void syncScenicArea() {
        cameraTrafficService.syncScenicArea();
    }
    @Scheduled(cron = "0 5 12 * * ?")
    public void syncSpotArea(){
        cameraTrafficService.syncSpotArea();
    }
    @Scheduled(cron = "0 0/5 * * * ?")
    public void syncPassengerFlow(){
        cameraTrafficService.syncZonePassenger();
    }
}
