package com.drore.cloud.tdp.entity;

import lombok.Data;

/**
 * 描述:深大检票实体类
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/13  14:39.
 */
@Data
public class WebSaleTicketThird {
    private String scenicName;//景区名称
    private String billNo;//票号
    private String ticketModelName;//票型
    private String clientName;//售票渠道
    private String userName;//购票人姓名
    private String billDate;//售票时间
    private String travelDateTime;//游玩时间
    private Integer ticketCount;//售票数量
    private Double sellPrice;//票单价
    private Double ticketPrice;//票总价
    private String tel;//购票人电话
    private String cerNo;//购票人身份证
    private String billDetailId;//售票主键id(唯一值)
}
