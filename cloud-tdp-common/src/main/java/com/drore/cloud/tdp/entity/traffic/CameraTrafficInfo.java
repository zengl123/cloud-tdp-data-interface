package com.drore.cloud.tdp.entity.traffic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:监控客流实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/5  16:39.
 */
@Data
public class CameraTrafficInfo {
    @JSONField(name = "event_type")
    private String eventType;//报警事件类型
    @JSONField(name = "object_name")
    private String objectName;//告警源名称
    @JSONField(name = "stop_time")
    private String stopTime;//告警结束时间
    @JSONField(name = "object_type")
    private String objectType;//告警源类型
    @JSONField(name = "event_log_id")
    private String eventLogId;//告警事件编号
    @JSONField(name = "start_time")
    private String startTime;//报警事件开始时间
    @JSONField(name = "event_config_id")
    private String eventConfigId;//报警事件配置id
    @JSONField(name = "event_name")
    private String eventName;//报警事件名称
    @JSONField(name = "event_level")
    private String eventLevel;//报警事件配置级别
    @JSONField(name = "object_index_code")
    private String objectIndexCode;//报警点对象索引号
    @JSONField(name = "org_name")
    private String orgName;//告警源所属组织名称
    @JSONField(name = "org_index")
    private String orgIndex;//告警源所属组织编号
    private String status;//告警事件状态
    @JSONField(name = "abstime")
    private String absTime;
    @JSONField(name = "relativetime")
    private String relativeTime;
    private String mode;
    @JSONField(name = "enternum")
    private Integer enterNum;//进入人数
    @JSONField(name = "leavenum")
    private Integer leaveNum;//离开人数
    @JSONField(name = "FomIn")
    private Integer fromIn;
}
