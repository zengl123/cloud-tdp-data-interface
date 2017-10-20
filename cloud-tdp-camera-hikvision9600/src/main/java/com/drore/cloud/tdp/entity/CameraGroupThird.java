package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/17  15:19.
 */
@Data
public class CameraGroupThird {
    @JSONField(name = "c_org_name")
    private String orgName;//组织名称(监控列表名称)
    @JSONField(name = "i_id")
    private Integer id;//xml中行id(监控列表编号)
    @JSONField(name = "c_index_code")
    private String indexCode;//组织内部编码
    @JSONField(name = "i_level")
    private Integer level;//
}
