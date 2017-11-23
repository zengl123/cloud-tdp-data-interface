package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/12  11:30.
 */
@Data
public class BaseEntity {
    @JSONField(name = "pkid")
    private String id;
    @JSONField(name = "modified_time")
    private String modifiedTime;
    @JSONField(name = "sort")
    private Integer sort;
}
