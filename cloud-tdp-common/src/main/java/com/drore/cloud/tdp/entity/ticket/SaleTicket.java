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
public class SaleTicket extends BaseEntity {
    @JSONField(name = "sale_detail_id")
    private String tradeDetailId;//售票主键id(唯一值,可根据该字段去重)
    @JSONField(name = "sale_ticket_place")
    private String saleTicketPlace;//售票点名称
    @JSONField(name = "sale_ticket_num")
    private Integer saleTicketNum;///购票张数
    @JSONField(name = "total_price")
    private Double totalPrice;//总售价
    @JSONField(name = "unit_price")
    private Double unitPrice;//票单价
    @JSONField(name = "channel")
    private String channel;//售票渠道
    @JSONField(name = "scenic_name")
    private String scenicName;//景区名称
    @JSONField(name = "spot_name")
    private String spotName;//景点名称
    @JSONField(name = "tourist_type")
    private String touristType;//游客类型
    @JSONField(name = "ticket_model_name")
    private String ticketModelName;//票类型
    @JSONField(name = "ticket_kind_name")
    private String ticketKindName;//票种
    @JSONField(name = "sale_time")
    private String saleTime;//售票时间
    @JSONField(name = "use_time")
    private String useTime;//游玩时间
    @JSONField(name = "user_name")
    private String userName;//购票人姓名
    @JSONField(name = "phone_number")
    private String phoneNumber;//电话
    @JSONField(name = "id_card")
    private String idCard;//身份证
    @JSONField(name = "ticket_no")
    private String ticketNo;//票号
    @JSONField(name = "operator_name")
    private String operatorName;//售票员
    @JSONField(name = "province_name")
    private String provinceName;//来源地
}
