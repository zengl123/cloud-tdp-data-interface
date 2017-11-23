package com.drore.cloud.tdp.entity.kpi;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:游客客源地数据
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/27  11:19.
 */
@Data
public class TouristOrigin extends BaseEntity {
    @JSONField(name = "spot_no")
    @SerializedName(value = "spot_no")
    private String spotNo;//景点编码
    @JSONField(name = "spot_name")
    @SerializedName(value = "spot_name")
    private String spotName;//景点名称
    @JSONField(name = "kpi_city")
    @SerializedName(value = "kpi_city")
    private String kpiCity;//城市编码
    @JSONField(name = "kpi_type")
    @SerializedName(value = "kpi_type")
    private String kpiType;//kpi类型
    @JSONField(name = "kpi_id")
    @SerializedName(value = "kpi_id")
    private String kpiId;
    @JSONField(name = "kpi_value")
    @SerializedName(value = "kpi_value")
    private Integer kpiValue;//kpi数值
    @JSONField(name = "kpi_time")
    @SerializedName(value = "kpi_time")
    private String kpiTime;//kpi时间
}
