package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/5  15:11.
 */
@Data
public class ExtEventInfo {
//    @JSONField(name = "AbsTime")
//    private String absTime;
//    @JSONField(name = "RelativeTime")
//    private String relativeTime;
    @JSONField(name = "Mode")
    private String mode;
    @JSONField(name = "EnterNum")
    private Integer enterNum;//进入人数
    @JSONField(name = "LeaveNum")
    private Integer leaveNum;//离开人数
}
