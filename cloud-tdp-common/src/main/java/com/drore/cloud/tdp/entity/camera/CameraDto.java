package com.drore.cloud.tdp.entity.camera;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import lombok.Data;

/**
 * 描述:监控点实体类(对应单个监控)
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/25  18:34.
 */
@Data
public class CameraDto extends BaseEntity{
    @JSONField(name = "name")
    private String cameraName;//监控点名称
    private String userName;//设备登陆的用户名
    @JSONField(name = "userPwd")
    private String password;//设备登陆的密码
    @JSONField(name = "networkAddr")
    private String ipAddress;//监控ip地址
    private Integer networkPort;//端口
    @JSONField(name = "device_id")
    private Integer deviceId;//设备id
    @JSONField(name = "index_code")
    private String indexCode;//设备编号
    private Integer channelNo;//监控通道号
    @JSONField(name = "region_id")
    private Integer cameraListId;//所属监控列表id
}
