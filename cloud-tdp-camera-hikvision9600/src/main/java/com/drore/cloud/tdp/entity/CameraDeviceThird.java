package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述: 资源(监控点实体类)
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/19  10:15.
 */
@Data
public class CameraDeviceThird {
    @JSONField(name = "i_id")
    private Integer iid;//记录id
    @JSONField(name = "c_creator")
    private String creator;//创建者
    @JSONField(name = "c_name")
    private String name;//监控点名称
    @JSONField(name = "i_channel_no")
    private Integer channelNo;//通道号
    @JSONField(name = "c_device_ip")
    private String deviceIp;//监控点ip
    @JSONField(name = "i_device_port")
    private Integer devicePort;//监控点端口
    @JSONField(name = "i_org_id")
    private Integer orgId;//组织id
    @JSONField(name = "c_index_code")
    private String indexCode;//资源编号
}
