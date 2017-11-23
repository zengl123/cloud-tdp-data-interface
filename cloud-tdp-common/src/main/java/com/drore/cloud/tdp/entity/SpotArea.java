package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/12  10:53.
 */
@Data
public class SpotArea extends BaseEntity{
    @JSONField(name = "spot_name")
    @SerializedName(value = "spot_name")
    private String spotName;//景点名称
    @JSONField(name = "spot_no")
    @SerializedName(value = "spot_no")
    private String spotNo;//景点编号
    @JSONField(name = "scenic_no")
    @SerializedName(value = "scenic_no")
    private String scenicNo;//所属景区编号
}
