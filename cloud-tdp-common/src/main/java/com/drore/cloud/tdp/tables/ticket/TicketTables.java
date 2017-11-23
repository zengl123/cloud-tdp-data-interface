package com.drore.cloud.tdp.tables.ticket;

import com.alibaba.fastjson.JSON;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.ScenicArea;
import com.drore.cloud.tdp.entity.SpotArea;
import com.drore.cloud.tdp.entity.ticket.CheckTicket;
import com.drore.cloud.tdp.entity.ticket.SaleTicket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/20  17:42.
 */
public class TicketTables {
    public static final String TICKET_SCENIC_TABLE = "ticket_scenic_info";
    public static final String TICKET_SPOT_TABLE = "ticket_spot_info";
    public static final String SALE_TICKET_TABLE = "ticket_sale_info";
    public static final String CHECK_TICKET_TABLE = "ticket_check_info";
}
