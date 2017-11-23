package com.drore.cloud.tdp.common.park;

import com.alibaba.fastjson.JSON;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.parking.CarParkInfo;
import com.drore.cloud.tdp.tables.park.CarParkTables;
import com.drore.cloud.tdp.utils.CloudQueryRunnerUtil;
import com.drore.cloud.tdp.utils.ConfigUtil;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  16:54.
 */
@Component
public class CarParkCommon {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarParkCommon.class);
    @Autowired
    private CloudQueryRunner runner;


    public Map<String, String> getBeginAndEndTime() {
        RequestExample example = new RequestExample(1, 1);
        example.addSort("cross_time", "desc");
        String beginTime;
        Pagination<CarParkInfo> carParkInfoPagination = runner.queryListByExample(CarParkInfo.class, CarParkTables.CAR_PARK_INFO, example);
        if (carParkInfoPagination.getCount() > 0) {
            beginTime = carParkInfoPagination.getData().get(0).getCrossTime();
            beginTime = DateTimeUtil.timeAddMinusSeconds(beginTime, 1);
        } else {
            LOGGER.info("数据库暂无数据,beginTime&endTime 使用默认配置;");
            beginTime = ConfigUtil.getConfig().getString("carParkBeginTime");
        }
        String endTime = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String nowTime = DateTimeUtil.getNowTime();
        if (DateTimeUtil.compareDate(endTime, nowTime)) {
            endTime = nowTime;
        }
        Map<String, String> map = new HashMap<>();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);
        return map;
    }

    public void saveOrUpdateCarParkInfo(List<CarParkInfo> add, List<CarParkInfo> update) {
        if (add.size() > 0) {
            RestMessage restMessage = runner.insertBatch(CarParkTables.CAR_PARK_INFO, JSON.toJSON(add));
            if (null != restMessage) {
                LOGGER.info("新增停车场进出记录成功,共新增：" + add.size() + "条记录;");
            } else {
                LOGGER.info("新增停车场进出记录失败;");
            }
        } else {
            LOGGER.info("没有新增停车场进出记录;");
        }
        if (update.size() > 0) {
            RestMessage restMessage = runner.updateBatch(CarParkTables.CAR_PARK_INFO, JSON.toJSON(update));
            if (null != restMessage) {
                LOGGER.info("更新停车场进出记录成功,共更新：" + update.size() + "条记录;");
            } else {
                LOGGER.info("更新停车场进出记录失败;");
            }
        } else {
            LOGGER.info("没有停车记录需要更新;");
        }
    }
}
