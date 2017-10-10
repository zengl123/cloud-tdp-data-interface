package com.drore.cloud.tdp.entity.camera;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述: 监控列表实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/25  18:27.
 */
@Data
public class CameraGroup {
    @JSONField(name = "pkid")
    private String id;
    @JSONField(name = "name")
    private String cameraListName;//监控列表名称
    @JSONField(name = "region_id")
    private String cameraListNo;//监控列表编号--对应监控点父编号
}
