package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/14  16:07.
 */
@Data
public class WebCheckTicketThird {
    private String scenicName;//景区名称
    @JSONField(name = "billno")
    private String billNo;//票号
    private String ticketModelName;//票型
    private String takeTicketPlace;//检票点
    private String checkType;//检票方式
    private String checkName;//检票点
    private String useTime;//检票时间
    private Integer checkNum;//人数
    private String billCheckDetailId;//电子订单检票id
}
