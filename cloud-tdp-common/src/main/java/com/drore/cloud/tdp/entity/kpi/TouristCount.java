package com.drore.cloud.tdp.entity.kpi;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/27  11:09.
 */
@Data
public class TouristCount extends BaseEntity {
    @JSONField(name = "kpi_value")
    @SerializedName(value = "kpi_value")
    private Integer kpiValue;
    @JSONField(name = "kpi_id")
    @SerializedName(value = "kpi_id")
    private String kpiId;
    @JSONField(name = "spot_no")
    @SerializedName(value = "spot_no")
    private String spotNo;
    @JSONField(name = "spot_name")
    @SerializedName(value = "spot_name")
    private String spotName;
    @JSONField(name = "kpi_time")
    @SerializedName(value = "kpi_time")
    private String kpiTime;
}
