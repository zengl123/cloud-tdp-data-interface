package com.drore.cloud.tdp.schedule;

import com.drore.cloud.tdp.service.impl.CameraServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/25  15:09.
 */
@Component
@Configuration
@PropertySource("classpath:schedule.properties")
public class Schedule {
    @Autowired
    private CameraServiceImpl cameraService;

    @Scheduled(cron = "${cameraQuartz}")
    public void syncCamera() {
        cameraService.syncCamera();
    }
}
