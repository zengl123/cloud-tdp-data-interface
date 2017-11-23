package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  15:15.
 */
@Data
public class CarParkThird {
    private String uuid;//停车记录id
    private String parkId;//停车场id
    private String plateNo;//车牌号
    private String crossTime;//通行时间
    private String plateNoPicUrl;//车牌号图片
    private String vehiclePicUrl;//汽车图片
    private Integer carOut;//进出方向
    private Integer vehicleType;//汽车类型
    private String entranceName;//名称
}
