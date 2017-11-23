package com.drore.cloud.tdp.entity.kpi;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/27  11:14.
 */
@Data
public class TouristContinuousDetentionTime {
    @JSONField(name = "spot_code")
    private String spotNo;
    @JSONField(name = "spot_name")
    private String spotName;
    @JSONField(name = "kpi_value")
    private Integer kpiValue;
    @JSONField(name = "kpi_day")
    private Integer kpiDay;
    @JSONField(name = "sync_time")
    private String kpiTime;
}
