package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/17  17:36.
 */
@Data
public class CameraDtoThird {
    @JSONField(name = "i_id")
    private Integer id;
    @JSONField(name = "c_name")
    private String name;
    @JSONField(name = "c_creator")
    private String creator;
    @JSONField(name = "c_user_pwd")
    private String password;
    @JSONField(name = "c_device_ip")
    private String deviceIp;
    @JSONField(name = "i_device_port")
    private Integer devicePort;
    @JSONField(name = "c_index_code")
    private String indexCode;
    @JSONField(name = "i_channel_no")
    private Integer channelNo;
    @JSONField(name = "i_org_id")
    private Integer orgId;
}
