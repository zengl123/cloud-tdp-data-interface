package com.drore.cloud.tdp.entity.ticket;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import lombok.Data;

/**
 * 描述:售票实体类->对应数据库third_ticket
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/13  11:48.
 */
@Data
public class SaleTicket extends BaseEntity{
    @JSONField(name = "detailId")
    private String tradeDetailId;//售票主键id(唯一值)
    @JSONField(name = "name")
    private String takeTicketPlace;//售票点名称
    @JSONField(name = "num")
    private Integer ticketNoCount;///购票张数
    @JSONField(name = "totalPrice")
    private Double paySum;//总售价
    @JSONField(name = "channel")
    private String billType;//售票渠道
    @JSONField(name = "unitPrice")
    private Double ticketModelPrice;//票单价
    @JSONField(name = "scenic_name")
    private String scenicAreaCode;//景区名称
    @JSONField(name = "spot_name")
    private String parkFullName;//景点名称
    @JSONField(name = "tourist_type")
    private String clientType;//游客类型
    @JSONField(name = "ticket_model_name")
    private String ticketModelName;//票类型
    @JSONField(name = "ticket_kind_name")
    private String dictDetailName;//票种
    @JSONField(name = "orderTime")
    private String tradeDate;//售票时间
    @JSONField(name = "tourTime")
    private String travelDateTime;//游玩时间
    @JSONField(name = "user_name")
    private String userName;//购票人姓名
    @JSONField(name = "phone_number")
    private String tel;//电话
    @JSONField(name = "id_card")
    private String cerNo;//身份证
    @JSONField(name = "ticket_code")
    private String billNo;//票号
//    @JSONField(name = "sync_time")
//    private String syncTime;
    @JSONField(name = "operator_name")
    private String operatorName;//售票员
    @JSONField(name = "pov_name")
    private String areaName;//来源地
}
