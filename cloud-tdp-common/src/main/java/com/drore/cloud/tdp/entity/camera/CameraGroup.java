package com.drore.cloud.tdp.entity.camera;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述: 监控列表实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/25  18:27.
 */
@Data
public class CameraGroup extends BaseEntity{
    @JSONField(name = "name")
    @SerializedName(value = "name")
    private String cameraListName;//监控列表名称
    @JSONField(name = "region_id")
    @SerializedName(value = "region_id")
    private Integer cameraListNo;//监控列表编号--对应监控点父编号
    @JSONField(name = "index_code")
    @SerializedName(value = "index_code")
    private String indexCode;//组织内部编码
}
