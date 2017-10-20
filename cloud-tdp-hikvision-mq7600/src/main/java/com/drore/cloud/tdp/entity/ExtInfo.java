package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:报警事件扩展信息实体类
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/5  11:48.
 */
@Data
public class ExtInfo {
    @JSONField(name = "ExtEventInfo")
    private ExtEventInfo extEventInfo;
}
