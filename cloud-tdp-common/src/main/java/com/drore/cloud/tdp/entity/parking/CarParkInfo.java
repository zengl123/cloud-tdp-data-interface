package com.drore.cloud.tdp.entity.parking;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  13:46.
 */
@Data
public class CarParkInfo extends BaseEntity{
    @JSONField(name = "record_id")
    private String recordId;//停车记录id
    @JSONField(name = "park_id")
    private String parkId;//停车场id
    @JSONField(name = "plate_no")
    private String plateNo;//车牌号
    @JSONField(name = "cross_time")
    @SerializedName(value = "cross_time")
    private String crossTime;//通行时间
    @JSONField(name = "plate_no_pic")
    private String plateNoPicUrl;//车牌号图片
    @JSONField(name = "vehicle_pic")
    private String vehiclePicUrl;//汽车图片
    @JSONField(name = "car_in_out")
    private Integer carInOut;//进出方向
    @JSONField(name = "vehicle_type")
    private Integer vehicleType;//汽车类型
    @JSONField(name = "entrance_name")
    private String entranceName;//名称
    @JSONField(name = "enter_time")
    private String enterTime;//进入时间
    @JSONField(name = "leave_time")
    private String leaveTime;//离开时间
}

