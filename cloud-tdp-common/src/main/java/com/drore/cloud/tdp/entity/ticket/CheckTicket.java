package com.drore.cloud.tdp.entity.ticket;

import com.alibaba.fastjson.annotation.JSONField;
import com.drore.cloud.tdp.entity.BaseEntity;
import lombok.Data;

/**
 * 描述:检票实体类
 * 项目名:cloud-tdp-modules-ticket
 *
 * @Author:ZENLIN
 * @Created 2017/9/13  11:51.
 */
@Data
public class CheckTicket extends BaseEntity{
    @JSONField(name = "checkDetailId")
    private String checkDetailId;//检票id(唯一值)
    @JSONField(name = "scenic_area_code")
    private String scenicAreaCode;//景区名称
    @JSONField(name = "park_full_name")
    private String parkFullName;//景点名称
    @JSONField(name = "client_type")
    private String clientType;//游客类型
    @JSONField(name = "operator_name")
    private String operatorName;//售票员
    @JSONField(name = "ticket_model_name")
    private String ticketModelName;//票型
    @JSONField(name = "ticket_kind_name")
    private String ticketKindName;//票种
    @JSONField(name = "take_ticket_place")
    private String takeTicketPlace;//售票点
    @JSONField(name = "trade_date")
    private String tradeDate;//售票时间
    @JSONField(name = "use_time")
    private String useTime;//检票时间
    @JSONField(name = "check_place")
    private String checkPlace;// 检票景点
    @JSONField(name = "ticket_model_price")
    private String ticketModelPrice;//售票价格
    @JSONField(name = "sale_model")
    private String saleModel;//售票类型
    @JSONField(name = "already_use_count")
    private Integer alreadyUseCount;//使用人数
    @JSONField(name = "gate_no")
    private String gateNo;//检票闸机编号
    @JSONField(name = "ticket_code")
    private String ticketNo;//票编号
    @JSONField(name = "check_type")
    private String checkWay;//检票方式
//    @JSONField(name = "sync_time")
//    private String syncTime;
}
