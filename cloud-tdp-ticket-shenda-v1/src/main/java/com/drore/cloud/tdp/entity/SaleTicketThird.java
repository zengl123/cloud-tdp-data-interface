package com.drore.cloud.tdp.entity;

import lombok.Data;

/**
 * 描述:把第三方返回的售票数据——>售票实体类
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/13  13:08.
 */
@Data
public class SaleTicketThird {
    private String scenicAreaCode;//景区名称
    private String parkFullName;//景点名称
    private String clientType;//游客类型
    private String ticketModelName;//票类型
    private String DictDetailName;//票种
    private String takeTicketPlace;//售票点名称
    private String operatorName;//售票员
    private String tradeDate;//售票时间
    private Integer ticketNoCount;///购票张数
    private Double ticketModelPrice;//票价
    private Double paySum;//总售价
    private String billType;//售票渠道
    private String areaName;//来源地
    private String clientFullName;//旅行社线下
    private String tradeDetailId;//售票主键id(唯一值)
}
