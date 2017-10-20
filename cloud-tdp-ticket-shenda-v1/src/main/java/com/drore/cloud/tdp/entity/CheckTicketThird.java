package com.drore.cloud.tdp.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/15  11:01.
 */
@Data
public class CheckTicketThird {
    private String scenicAreaCode;//景区编号
    private String parkFullName;//景点名称
    private String clientType;//类型
    private String operatorName;//售票员姓名
    private String ticketModelName;//
    private String ticketKindName;//票种
    private String takeTicketPlace;//售票点
    private String tradeDate;//售票时间
    private String useTime;//检票时间
    private String checkPlace;//检票地点
    private String ticketModelPrice;//票价
    private String saleModel;//售票类型
    private Integer alreadyUseCount;//使用人数
    @JSONField(name = "gateno")
    private String gateNo;//闸机号
    private String checkDetailId;
}
