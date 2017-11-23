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
    @JSONField(name = "check_detail_id")
    private String checkDetailId;//检票id(唯一值,可根据该字段去重)
    @JSONField(name = "scenic_name")
    private String scenicName;//景区名称
    @JSONField(name = "spot_name")
    private String spotName;//景点名称
    @JSONField(name = "tourist_type")
    private String touristType;//游客类型
    @JSONField(name = "operator_name")
    private String operatorName;//售票员
    @JSONField(name = "ticket_model_name")
    private String ticketModelName;//票型
    @JSONField(name = "ticket_kind_name")
    private String ticketKindName;//票种
    @JSONField(name = "sale_ticket_place")
    private String saleTicketPlace;//售票点
    @JSONField(name = "sale_time")
    private String saleTime;//售票时间
    @JSONField(name = "use_time")
    private String useTime;//检票时间
    @JSONField(name = "check_ticket_place")
    private String checkTicketPlace;// 检票景点
    @JSONField(name = "sale_ticket_price")
    private String saleTicketPrice;//售票价格
    @JSONField(name = "sale_model")
    private String saleModel;//售票类型
    @JSONField(name = "use_num")
    private Integer useNum;//使用人数
    @JSONField(name = "gate_no")
    private String gateNo;//检票闸机编号
    @JSONField(name = "ticket_no")
    private String ticketNo;//票编号
    @JSONField(name = "check_type")
    private String checkType;//检票方式
}
