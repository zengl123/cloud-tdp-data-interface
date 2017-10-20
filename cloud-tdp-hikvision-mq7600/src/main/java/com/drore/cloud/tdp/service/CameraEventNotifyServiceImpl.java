package com.drore.cloud.tdp.service;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.tdp.entity.EventNotify;
import com.drore.cloud.tdp.entity.mq.CameraTrafficInfo;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 描述:报警信息操作及存储
 * 项目名:cloud-tdp-moudles
 *
 * @Author:ZENLIN
 * @Created 2017/7/21  9:24.
 */
@Component
@PropertySource("classpath:config_mq.properties")
public class CameraEventNotifyServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CameraEventNotifyServiceImpl.class);
    @Autowired
    private CloudQueryRunner runner;
    @Autowired
    private QueryUtil queryUtil;
    @Value("${event_url}")
    private String url;//获取管控报警地址

    private static final String STATUS = "1";//<!—告警事件状态，0 瞬时，1 脉冲开始，2 脉冲持续，3 脉冲结束 -->
    private static final String MODE = "0";


    /**
     * 获取边防报警信息
     *
     * @param eventNotify
     * @return
     */
    public void cameraBorder(EventNotify eventNotify) {
        JSONObject args = new JSONObject();//配置报警参数
        args.put("alarm_type", "ALARM_TYPE_SOS");
        args.put("trigger_device", eventNotify.getObjectIndexCode());
        String status = eventNotify.getStatus();
        if (STATUS.equals(status)) {
            String response = HttpClientUtil.postJson(url, args);//调用管控报警接口
            if (StringUtils.isNotEmpty(response)) {
                LOGGER.info("报警成功;response=" + response);
            } else {
                LOGGER.info("请求管控报警接口异常;response=" + response);
            }
        } else {
            LOGGER.info("告警事件状态status is " + status + " not 1,不需启动报警");
        }
    }

    /**
     * 监控客流报警信息
     *
     * @param eventNotify
     * @return
     */
    public void cameraTraffic(EventNotify eventNotify) {
        String mode;
        try {
            mode = eventNotify.getExtInfo().getExtEventInfo().getMode();
        } catch (Exception e) {
            LOGGER.error("ExtInfo or ExtEventInfo is null");
            return;
        }
        if (MODE.equals(mode)) {
            CameraTrafficInfo cameraTrafficInfo = new CameraTrafficInfo();
            cameraTrafficInfo.setEventType(eventNotify.getEventType());
            cameraTrafficInfo.setObjectType(eventNotify.getObjectType());
            cameraTrafficInfo.setObjectName(eventNotify.getObjectName());
            cameraTrafficInfo.setOrgName(eventNotify.getOrgName());
            cameraTrafficInfo.setEventName(eventNotify.getEventName());
            cameraTrafficInfo.setEventLogId(eventNotify.getEventLogId());
            //cameraTrafficInfo.setEventConfigId(eventNotify.getEventConfigId());
            cameraTrafficInfo.setOrgIndex(eventNotify.getOrgIndex());
            cameraTrafficInfo.setObjectIndexCode(eventNotify.getObjectIndexCode());
            cameraTrafficInfo.setStartTime(eventNotify.getStartTime());
            cameraTrafficInfo.setStopTime(eventNotify.getStopTime());
            cameraTrafficInfo.setStatus(eventNotify.getStatus());
            //cameraTrafficInfo.setEventLevel(eventNotify.getEventLevel());
            //cameraTrafficInfo.setAbsTime(eventNotify.getExtInfo().getExtEventInfo().getAbsTime());
            //cameraTrafficInfo.setRelativeTime(eventNotify.getExtInfo().getExtEventInfo().getRelativeTime());
            cameraTrafficInfo.setEnterNum(eventNotify.getExtInfo().getExtEventInfo().getEnterNum());
            cameraTrafficInfo.setLeaveNum(eventNotify.getExtInfo().getExtEventInfo().getLeaveNum());
            cameraTrafficInfo.setMode(eventNotify.getExtInfo().getExtEventInfo().getMode());
            String eventLogId = eventNotify.getEventLogId();
            String pkId = queryUtil.checkRepeat("camera_traffic_info", "event_log_id", eventLogId);
            if (null == pkId) {
                RestMessage insert = runner.insert("camera_traffic_info", JSONObject.toJSON(cameraTrafficInfo));
                if (insert != null) {
                    LOGGER.info("监控客流数据保存成功;cameraTrafficInfo=" + cameraTrafficInfo.toString());
                } else {
                    LOGGER.info("监控客流数据保存失败;");
                }
            } else {
                RestMessage update = runner.update("camera_traffic_info", pkId, JSONObject.toJSON(cameraTrafficInfo));
                if (update != null) {
                    LOGGER.info("监控客流数据更新成功;cameraTrafficInfo=" + cameraTrafficInfo.toString());
                } else {
                    LOGGER.info("监控客流数据更新失败;");
                }
            }
        } else {
            LOGGER.info("mode=" + mode + " is not 0,不需要保存;");
        }
    }
}
