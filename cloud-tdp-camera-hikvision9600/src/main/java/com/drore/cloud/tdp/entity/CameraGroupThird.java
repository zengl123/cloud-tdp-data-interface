package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:资源组织实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/19  10:14.
 */
@Data
public class CameraGroupThird {
    @JSONField(name = "i_id")
    private Integer iid;//记录id
    @JSONField(name = "c_org_name")
    private String orgName;//组织名称
    @JSONField(name = "c_index_code")
    private String indexCode;//组织编码
    @JSONField(name = "i_level")
    private Integer leave;
}
