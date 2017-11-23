package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:景区实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/11  14:27.
 */
@Data
public class ScenicArea extends BaseEntity{
    @JSONField(name = "scenic_name")
    @SerializedName(value ="scenic_name")
    private String scenicName;
    @JSONField(name = "scenic_no")
    @SerializedName(value = "scenic_no")
    private String scenicNo;
}
