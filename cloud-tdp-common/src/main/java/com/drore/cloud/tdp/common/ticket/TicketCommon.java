package com.drore.cloud.tdp.common.ticket;

import com.alibaba.fastjson.JSON;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.ScenicArea;
import com.drore.cloud.tdp.entity.SpotArea;
import com.drore.cloud.tdp.entity.ticket.CheckTicket;
import com.drore.cloud.tdp.entity.ticket.SaleTicket;
import com.drore.cloud.tdp.tables.ticket.TicketTables;
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
 * @Created 2017/10/25  16:54.
 */
@Component
public class TicketCommon {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketCommon.class);
    @Autowired
    private CloudQueryRunner runner;

    /**
     * 获取所有的景区
     *
     * @return
     */
    public List<ScenicArea> getAllScenicArea() {
        RequestExample example = new RequestExample(Integer.MAX_VALUE, 1);
        RequestExample.Criteria criteria = example.create();
        RequestExample.Param param = example.createParam();
        param.addTerm("is_deleted", "N");
        criteria.getMust().add(param);
        List<ScenicArea> data = null;
        try {
            Pagination<ScenicArea> pagination = runner.queryListByExample(ScenicArea.class, TicketTables.TICKET_SCENIC_TABLE, example);
            if (pagination.getCount() > 0) {
                data = pagination.getData();
            }
        } catch (Exception e) {
            LOGGER.error("获取所有景区信息异常;error=", e);
        }
        return data;
    }

    /**
     * 获取所有景点
     *
     * @return
     */
    public List<SpotArea> getAllSpotArea() {
        RequestExample example = new RequestExample(Integer.MAX_VALUE, 1);
        RequestExample.Criteria criteria = example.create();
        RequestExample.Param param = example.createParam();
        param.addTerm("is_deleted", "N");
        criteria.getMust().add(param);
        List<SpotArea> data = null;
        try {
            Pagination<SpotArea> pagination = runner.queryListByExample(SpotArea.class, TicketTables.TICKET_SPOT_TABLE, example);
            if (pagination.getCount() > 0) {
                data = pagination.getData();
            }
        } catch (Exception e) {
            LOGGER.error("获取所有景点信息异常,error=", e);
        }
        return data;
    }


    /**
     * 保存|更新 景区信息
     *
     * @param addScenic
     * @param updateScenic
     */
    public void saveOrUpdateScenic(List<ScenicArea> addScenic, List<ScenicArea> updateScenic) {
        if (addScenic.size() > 0) {
            RestMessage insert = runner.insertBatch(TicketTables.TICKET_SCENIC_TABLE, JSON.toJSON(addScenic));
            if (insert != null) {
                LOGGER.info("景区信息新增成功;scenicList=" + addScenic);
            } else {
                LOGGER.info("景区信息新增失败;");
            }
        } else {
            LOGGER.info("没有新增景区信息;");
        }
        if (updateScenic.size() > 0) {
            RestMessage update = runner.updateBatch(TicketTables.TICKET_SCENIC_TABLE, JSON.toJSON(updateScenic));
            if (update != null) {
                LOGGER.info("景区信息更新成功;scenicList=" + updateScenic);
            } else {
                LOGGER.info("景区信息更新失败;");
            }
        } else {
            LOGGER.info("没有景区信息需要更新;");
        }
    }

    /**
     * 保存|更新 景点信息
     *
     * @param addSpot
     * @param updateSpot
     */
    public void saveOrUpdateSpot(List<SpotArea> addSpot, List<SpotArea> updateSpot) {
        if (addSpot.size() > 0) {
            RestMessage insert = runner.insertBatch(TicketTables.TICKET_SPOT_TABLE, JSON.toJSON(addSpot));
            if (null != insert) {
                LOGGER.info("景点信息新增成功;spotList=" + addSpot);
            } else {
                LOGGER.info("景点信息新增失败;");
            }
        } else {
            LOGGER.info("没有新增景点信息;");
        }
        if (updateSpot.size() > 0) {
            RestMessage update = runner.updateBatch(TicketTables.TICKET_SPOT_TABLE, JSON.toJSON(updateSpot));
            if (null != update) {
                LOGGER.info("景点信息更新成功;spotList=" + updateSpot);
            } else {
                LOGGER.info("景点信息更新失败;");
            }
        } else {
            LOGGER.info("没有景点信息需要更新;");
        }
    }

    /**
     * 保存|更新售票信息
     *
     * @param addTicket
     * @param updateTicket
     */
    public void saveOrUpdateSaleTicket(List<SaleTicket> addTicket, List<SaleTicket> updateTicket) {
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(TicketTables.SALE_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("售票数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("售票数据新增失败;");
            }
        } else {
            LOGGER.info("没有产生售票数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(TicketTables.SALE_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("售票数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("售票数据更新失败;");
            }
        } else {
            LOGGER.info("没有售票数据更新;");
        }
    }

    /**
     * 保存|更新 检票信息
     *
     * @param addTicket
     * @param updateTicket
     */
    public void saveOrUpdateCheckTicket(List<CheckTicket> addTicket, List<CheckTicket> updateTicket) {
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(TicketTables.CHECK_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("检票数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("检票数据新增失败;");
            }
        } else {
            LOGGER.info("没有产生检票数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(TicketTables.CHECK_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("检票数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("检票数据更新失败;");
            }
        } else {
            LOGGER.info("没有检票数据更新;");
        }
    }
}
