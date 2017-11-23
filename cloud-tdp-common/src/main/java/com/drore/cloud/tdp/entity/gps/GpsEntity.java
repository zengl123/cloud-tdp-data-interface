package com.drore.cloud.tdp.entity.gps;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import lombok.Data;
import lombok.Value;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/13  14:40.
 */
@Data
public class GpsEntity extends BaseEntity{
    @JSONField(name = "user")
    private String deviceNo;
    @JSONField(name = "gps_time")
    private String gpsTime;
    private String x;//经度
    private String y;//纬度
    private String gx;//谷歌坐标(经度)
    private String gy;//谷歌坐标(纬度)
    @JSONField(name = "duty")
    private String speed;//速度
    @JSONField(name = "hand")
    private String direction;//方向
    @JSONField(name = "device_num")
    private String deviceNum;
}
