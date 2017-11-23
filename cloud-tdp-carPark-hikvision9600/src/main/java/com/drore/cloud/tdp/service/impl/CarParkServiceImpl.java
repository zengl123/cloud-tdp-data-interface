package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.tdp.common.park.CarParkCommon;
import com.drore.cloud.tdp.entity.CarParkThird;
import com.drore.cloud.tdp.entity.parking.CarParkInfo;
import com.drore.cloud.tdp.service.IWsPmsSdkServiceStub;
import com.drore.cloud.tdp.tables.park.CarParkTables;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  13:59.
 */
@Component
public class CarParkServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarParkServiceImpl.class);
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CarParkCommon parkCommon;

    private String url;
    private String parkIndexCode;

    private void initMethod() {
        String factoryModelName = "carPark9600";
        JSONObject object = queryUtil.queryConfigByFactoryModelName(factoryModelName);
        url = object.getString("url");
        parkIndexCode = object.getString("parkIndexCode");
    }

    public void syncCarParkInfo() {
        try {
            initMethod();
        } catch (Exception e) {
            LOGGER.error("获取停车场配置参数:url,parkIndexCode异常,error=", e);
            return;
        }
        Map<String, String> beginAndEndTime = parkCommon.getBeginAndEndTime();
        JSONObject object = new JSONObject();
        object.put("pageNo", 1);
        object.put("pageSize", 1000);
        object.put("parkIndexCode", parkIndexCode);
        object.put("startTime", beginAndEndTime.get("beginTime"));
        object.put("endTime", beginAndEndTime.get("endTime"));
        String xml = XmlUtil.json2xml(object.toJSONString());
        String resultXml;
        try {
            IWsPmsSdkServiceStub stub = new IWsPmsSdkServiceStub(url);
            IWsPmsSdkServiceStub.GetVehicleRecordPage getParkPage = new IWsPmsSdkServiceStub.GetVehicleRecordPage();
            getParkPage.setRequestXml(xml);
            IWsPmsSdkServiceStub.GetVehicleRecordPageResponse recordPageResponse = stub.getVehicleRecordPage(getParkPage);
            resultXml = recordPageResponse.get_return().getResponseXml();
        } catch (RemoteException e) {
            LOGGER.error("停车场进出记录接口异常,error=", e);
            return;
        }
        System.out.println("resultXml = " + resultXml);
        JSONObject jsonObject = XmlUtil.xmlToJson(resultXml);
        JSONArray array = jsonObject.getJSONObject("PageBean").getJSONArray("VehicleRecordDto");
        List<CarParkInfo> add = new ArrayList<>();
        List<CarParkInfo> update = new ArrayList<>();
        array.stream().forEach(carParkThirds -> {
            CarParkInfo carParkInfo = new CarParkInfo();
            CarParkThird carParkThird = JSONObject.toJavaObject((JSON) (carParkThirds), CarParkThird.class);
            String uuid = carParkThird.getUuid();
            carParkInfo.setRecordId(carParkThird.getUuid());
            carParkInfo.setParkId(carParkThird.getParkId());
            carParkInfo.setCarInOut(carParkThird.getCarOut());
            carParkInfo.setCrossTime(carParkThird.getCrossTime());
            carParkInfo.setEntranceName(carParkThird.getEntranceName());
            carParkInfo.setPlateNo(carParkThird.getPlateNo());
            carParkInfo.setPlateNoPicUrl(carParkThird.getPlateNoPicUrl());
            carParkInfo.setVehiclePicUrl(carParkThird.getVehiclePicUrl());
            carParkInfo.setVehicleType(carParkThird.getVehicleType());
            String id;
            try {
                id = queryUtil.checkRepeat(CarParkTables.CAR_PARK_INFO, "record_id", uuid);
            } catch (Exception e) {
                LOGGER.error("syncCarParkInfo->checkRepeat()异常,error=", e);
                return;
            }
            if (null == id) {
                add.add(carParkInfo);
            } else {
                update.add(carParkInfo);
            }
        });
        try {
            parkCommon.saveOrUpdateCarParkInfo(add, update);
        } catch (Exception e) {
            LOGGER.error("停车场车辆进出记录新增|更新异常,error=", e);
        }
    }
}
