package com.drore.cloud.tdp.schedule;

import com.drore.cloud.tdp.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/16  14:07.
 */
@Component
@PropertySource("classpath:ticket_schedule.properties")
public class Schedule {
    @Autowired
    private TicketServiceImpl ticketService;

    @Scheduled(cron = "${syncScenicArea}")
    public void scenicArea() {
        ticketService.syncScenicArea();
    }

    @Scheduled(cron = "${syncSpotArea}")
    public void spotArea() {
        ticketService.syncSpotArea();
    }

    @Scheduled(cron = "${syncSpotSale}")
    public void spotSale() {
        ticketService.syncSpotSaleInfo();
    }

    @Scheduled(cron = "${syncSpotCheck}")
    public void spotCheck() {
        ticketService.syncSpotCheckInfo();
    }

    @Scheduled(cron = "${syncWebSale}")
    public void webSale() {
        ticketService.syncWebOrderInfo();
    }

    @Scheduled(cron = "${syncWebCheck}")
    public void webCheck() {
        ticketService.syncWebCheckInfo();
    }
}
