package com.drore.cloud.tdp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.tdp.entity.gps.GpsEntity;
import com.drore.cloud.tdp.tables.gps.GpsTables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/13  14:33.
 */
@Component
public class GpsHkServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(GpsHkServiceImpl.class);

    @Autowired
    private CloudQueryRunner runner;

    public void syncGps(JSONObject object) {
        JSONObject params = object.getJSONObject("Params");
        String deviceId = params.getString("DeviceID");
        String deviceName = params.getString("DeviceName");
        String deviceType = params.getString("DeviceType");
        String time = params.getString("Time");
        String divisionEW = params.getString("DivisionEW");
        String longitude = String.valueOf(Double.valueOf(params.getString("Longitude")) / 1000000);
        String divisionNS = params.getString("DivisionNS");
        String latitude = String.valueOf(Double.valueOf(params.getString("Latitude")) / 1000000);
        String direction = params.getString("Direction");
        String speed = params.getString("Speed");
        String satellites = params.getString("Satellites");
        String precision = params.getString("Precision");
        String height = params.getString("Height");

        GpsEntity gpsEntity = new GpsEntity();
        gpsEntity.setDeviceNo(deviceId);
        gpsEntity.setDeviceNum(deviceId);
        gpsEntity.setX(longitude);
        gpsEntity.setY(latitude);
        gpsEntity.setGx(longitude);
        gpsEntity.setGy(latitude);
        gpsEntity.setDirection(direction);
        gpsEntity.setSpeed(speed);
        gpsEntity.setGpsTime(time);
        RestMessage insert = runner.insert(GpsTables.GPS_DATA, JSON.toJSON(gpsEntity));
        if (null != insert) {
            LOGGER.info("数据新增成功,gpsEntity=" + gpsEntity);
        } else {
            LOGGER.info("数据新增失败");
        }
    }
}
