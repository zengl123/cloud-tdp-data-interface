package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.*;
import com.drore.cloud.tdp.entity.ticket.CheckTicket;
import com.drore.cloud.tdp.entity.ticket.SaleTicket;
import com.drore.cloud.tdp.util.SignUtil;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/11  12:32.
 */
@Component
public class TicketServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(TicketServiceImpl.class);
    private static String appCode;
    private static String host;
    private static String method;
    private static String appKey;
    private static String areaCode;

    //private static CloudQueryRunner runner = CloudQueryRunnerUtil.getCloudQueryRunner();
    @Autowired
    private CloudQueryRunner runner;

    private static final String SCENIC_TABLE = "scenics_info";
    private static final String SPOT_TABLE = "spot_info";
    private static final String SALE_TICKET_TABLE = "third_ticket";
    private static final String CHECK_TICKET_TABLE = "third_check_ticket";

    private static final String BEGIN_TIME = "2017-10-15 00:00:00";

    /**
     * 获取深大票务配置参数
     */
    static {
        String factoryModelName = "shenDaTicketV1_0_2";
        JSONObject config = QueryUtil.queryConfigByFactoryModelName(factoryModelName);
        try {
            appCode = config.getString("appCode");
            host = config.getString("url");
            method = config.getString("method");
            appKey = config.getString("appKey");
            areaCode = config.getString("areaCode");
        } catch (Exception e) {
            LOGGER.error("获取深大票务配置参数异常,error=", e);
        }
    }

    public void syncScenicArea() {
        List<ScenicArea> addScenic = new ArrayList<>();
        List<ScenicArea> updateScenic = new ArrayList<>();
        Map param = new HashMap();
        param.put("areaCode", areaCode);
        String sign = SignUtil.buildMySign(param, appKey);
        param.put("sign", sign);
        param.put("appCode", appCode);
        String path = "getScenicsInfo.htm";
        JSONObject response = getResponse(param, path);
        try {
            ThirdData thirdData = JSONObject.toJavaObject(response, ThirdData.class);
            thirdData.getData().stream().map(object -> {
                ScenicThird scenicThird = JSONObject.toJavaObject((JSON) object, ScenicThird.class);
                return scenicThird;
            }).collect(Collectors.toList()).stream().forEach(scenicThird -> {
                String id = QueryUtil.checkRepeat(SCENIC_TABLE, "scenic_code", scenicThird.getGroupCode());
                ScenicArea scenicArea = new ScenicArea();
                scenicArea.setScenicName(scenicThird.getGroupName());
                scenicArea.setScenicNo(scenicThird.getGroupCode());
                if (null == id) {
                    addScenic.add(scenicArea);
                } else {
                    scenicArea.setId(id);
                    scenicArea.setModifiedTime(LocalDateTime.now().withNano(0).toString());
                    updateScenic.add(scenicArea);
                }
            });
        } catch (Exception e) {
            LOGGER.error("获取接口数据异常,error=", e);
        }
        try {
            if (addScenic.size() > 0) {
                RestMessage insert = runner.insertBatch(SCENIC_TABLE, JSON.toJSON(addScenic));
                if (insert != null) {
                    LOGGER.info("景区信息新增成功;scenicList=" + addScenic);
                }
            } else {
                LOGGER.info("没有新增景区信息;");
            }
            if (updateScenic.size() > 0) {
                RestMessage update = runner.updateBatch(SCENIC_TABLE, JSON.toJSON(updateScenic));
                if (update != null) {
                    LOGGER.info("景区信息更新成功;scenicList=" + updateScenic);
                }
            } else {
                LOGGER.info("没有景区信息需要更新;");
            }
        } catch (Exception e) {
            LOGGER.error("数据存储异常,error=", e);
        }
    }


    public void syncSpotArea() {
        List<SpotArea> addSpot = new ArrayList<>();
        List<SpotArea> updateSpot = new ArrayList<>();
        List<ScenicArea> scenicAreas = getAllScenicArea();
        if (null == scenicAreas) {
            return;
        }
        scenicAreas.stream().forEach(scenicArea -> {
            String groupCode = scenicArea.getScenicNo();
            Map param = new HashMap();
            param.put("appCode", appCode);
            param.put("groupCode", groupCode);
            String sign = SignUtil.buildMySign(param, appKey);
            param.put("sign", sign);
            String path = "getSpotsInfo.htm";
            JSONObject response = getResponse(param, path);
            try {
                ThirdData thirdData = JSONObject.toJavaObject(response, ThirdData.class);
                thirdData.getData().stream().map(object -> {
                    SpotThird spotThird = JSON.toJavaObject((JSON) object, SpotThird.class);
                    return spotThird;
                }).collect(Collectors.toList()).stream().forEach(spotThird -> {
                    String id = QueryUtil.checkRepeat(SPOT_TABLE, "spot_code", spotThird.getParkCode());
                    SpotArea spotArea = new SpotArea();
                    spotArea.setSpotName(spotThird.getParkFullName());
                    spotArea.setSpotNo(spotThird.getParkCode());
                    spotArea.setScenicNo(groupCode);
                    if (null == id) {
                        addSpot.add(spotArea);
                    } else {
                        spotArea.setId(id);
                        spotArea.setModifiedTime(LocalDateTime.now().withNano(0).toString());
                        updateSpot.add(spotArea);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("获取接口数据异常,error=", e);
            }
            try {
                if (addSpot.size() > 0) {
                    RestMessage insert = runner.insertBatch(SPOT_TABLE, JSON.toJSON(addSpot));
                    if (null != insert) {
                        LOGGER.info("景点信息新增成功;spotList=" + addSpot);
                    } else {
                        LOGGER.info("景点信息新增失败;restMessage=" + insert);
                    }
                } else {
                    LOGGER.info("没有新增景点信息;");
                }
                if (updateSpot.size() > 0) {
                    RestMessage update = runner.updateBatch(SPOT_TABLE, JSON.toJSON(updateSpot));
                    if (null != update) {
                        LOGGER.info("景点信息更新成功;spotList=" + updateSpot);
                    } else {
                        LOGGER.info("景点信息更新失败;restMessage=" + update);
                    }
                } else {
                    LOGGER.info("没有景点信息需要更新;");
                }
            } catch (Exception e) {
                LOGGER.error("数据存储异常,error=", e);
            }
        });
    }

    /**
     * 同步景点售票
     */
    public void syncSpotSaleInfo() {
        List<SaleTicket> addTicket = new ArrayList<>();
        List<SaleTicket> updateTicket = new ArrayList<>();
        List<SpotArea> allSpotArea = getAllSpotArea();
        if (null == allSpotArea) {
            return;
        }
        String beginTime = getBeginTime("spotSale");
        if (null == beginTime) {
            return;
        }
        String endTime = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        String nowTime = DateTimeUtil.timeAddMinusMinutes(now, -10, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        if (DateTimeUtil.compareDate(endTime, nowTime)) {
            endTime = nowTime;
        }
        Map term = new HashMap();
        term.put("beginTime", beginTime);//查询开始时间
        term.put("endTime", endTime);//查询结束时间
        LOGGER.info("beginTime=" + beginTime + ";endTime=" + endTime);
        allSpotArea.stream().forEach(spotArea -> {
            String scenicNo = spotArea.getScenicNo();
            String spotNo = spotArea.getSpotNo();
            term.put("groupCode", scenicNo);
            term.put("parkCode", spotNo);
            term.remove("sign");
            String sign = SignUtil.buildMySign(term, appKey);//构建签名
            term.put("sign", sign);
            term.put("appCode", appCode);
            String path = "getSpotsSaleInfo.htm";
            JSONObject response = getResponse(term, path);
            System.out.println("spot=" + spotArea.getSpotName() + ",SpotsSaleInfoResponse = " + response);
            ThirdData thirdData = JSONObject.toJavaObject(response, ThirdData.class);
            if (!"SUCCESS".equals(thirdData.getResult())) {
                return;
            }
            thirdData.getData().stream().map(object -> {
                SaleTicketThird saleTicketThird = JSON.toJavaObject((JSON) object, SaleTicketThird.class);
                return saleTicketThird;
            }).collect(Collectors.toList()).forEach(saleTicketThird -> {
                String tradeDetailId = saleTicketThird.getTradeDetailId();
                String id;
                try {
                    id = QueryUtil.checkRepeat(SALE_TICKET_TABLE, "detailId", tradeDetailId);
                } catch (Exception e) {
                    return;
                }
                SaleTicket saleTicket = new SaleTicket();
                saleTicket.setScenicAreaCode(saleTicketThird.getScenicAreaCode());
                saleTicket.setParkFullName(saleTicketThird.getParkFullName());
                saleTicket.setClientType(saleTicketThird.getClientType());
                saleTicket.setTicketModelName(saleTicketThird.getTicketModelName());
                saleTicket.setDictDetailName(saleTicketThird.getDictDetailName());
                saleTicket.setTakeTicketPlace(saleTicketThird.getTakeTicketPlace());
                saleTicket.setOperatorName(saleTicketThird.getOperatorName());
                saleTicket.setTradeDate(DateTimeUtil.msToDateStr(saleTicketThird.getTradeDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setTicketNoCount(saleTicketThird.getTicketNoCount());
                saleTicket.setTicketModelPrice(saleTicketThird.getTicketModelPrice());
                saleTicket.setPaySum(saleTicketThird.getPaySum());
                saleTicket.setBillType(saleTicketThird.getBillType());
                saleTicket.setAreaName(saleTicketThird.getAreaName());
                saleTicket.setTradeDetailId(saleTicketThird.getTradeDetailId());
                if (null == id) {
                    addTicket.add(saleTicket);
                } else {
                    saleTicket.setId(id);
                    saleTicket.setModifiedTime(LocalDate.now() + " " + LocalTime.now().withNano(0));
                    updateTicket.add(saleTicket);
                }
            });
        });
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(SALE_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("景点售票数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("景点售票数据新增失败,message=" + insertBatch);
            }
        } else {
            LOGGER.info("没有产生景点售票数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(SALE_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("景点售票数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("景点售票数据更新失败,message=" + updateBatch);
            }
        } else {
            LOGGER.info("没有景点售票数据更新;");
        }
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncSpotSaleInfo();
        }
    }

    /**
     * 同步景点检票
     */
    public void syncSpotCheckInfo() {
        List<CheckTicket> addTicket = new ArrayList<>();
        List<CheckTicket> updateTicket = new ArrayList<>();
        List<SpotArea> allSpotArea = getAllSpotArea();
        if (null == allSpotArea) {
            return;
        }
        String beginTime = getBeginTime("spotCheck");
        if (null == beginTime) {
            return;
        }
        String endTime = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        String nowTime = DateTimeUtil.timeAddMinusMinutes(now, -10, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        if (DateTimeUtil.compareDate(endTime, nowTime)) {
            endTime = nowTime;
        }
        Map term = new HashMap();
        term.put("beginTime", beginTime);//查询开始时间
        term.put("endTime", endTime);//查询结束时间
        LOGGER.info("beginTime=" + beginTime + ";endTime=" + endTime);
        allSpotArea.stream().forEach(spotArea -> {
            String scenicNo = spotArea.getScenicNo();
            String spotNo = spotArea.getSpotNo();
            term.put("groupCode", scenicNo);
            term.put("parkCode", spotNo);
            term.remove("sign");
            String sign = SignUtil.buildMySign(term, appKey);//构建签名
            term.put("sign", sign);
            term.put("appCode", appCode);
            String path = "getSpotsCheckInfo.htm";
            JSONObject response = getResponse(term, path);
            System.out.println("spot=" + spotArea.getSpotName() + ",SpotsCheckInfoResponse=" + response);
            ThirdData thirdData = JSONObject.toJavaObject(response, ThirdData.class);
            if (!"SUCCESS".equals(thirdData.getResult())) {
                return;
            }
            thirdData.getData().stream().map(object -> {
                CheckTicketThird checkTicketThird = JSON.toJavaObject((JSON) object, CheckTicketThird.class);
                return checkTicketThird;
            }).collect(Collectors.toList()).forEach(checkTicketThird -> {
                String checkDetailId = checkTicketThird.getCheckDetailId();
                String id;
                try {
                    id = QueryUtil.checkRepeat(CHECK_TICKET_TABLE, "checkDetailId", checkDetailId);
                } catch (Exception e) {
                    return;
                }
                CheckTicket checkTicket = new CheckTicket();
                checkTicket.setScenicAreaCode(checkTicketThird.getScenicAreaCode());
                checkTicket.setParkFullName(checkTicketThird.getParkFullName());
                checkTicket.setClientType(checkTicketThird.getClientType());
                checkTicket.setOperatorName(checkTicketThird.getOperatorName());
                checkTicket.setTicketModelName(checkTicketThird.getTicketModelName());
                checkTicket.setTicketKindName(checkTicketThird.getTicketKindName());
                checkTicket.setTakeTicketPlace(checkTicketThird.getTakeTicketPlace());
                checkTicket.setTradeDate(DateTimeUtil.msToDateStr(checkTicketThird.getTradeDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                checkTicket.setUseTime(DateTimeUtil.msToDateStr(checkTicketThird.getUseTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                checkTicket.setCheckPlace(checkTicketThird.getCheckPlace());
                checkTicket.setTicketModelPrice(checkTicketThird.getTicketModelPrice());
                checkTicket.setSaleModel(checkTicketThird.getSaleModel());
                checkTicket.setAlreadyUseCount(checkTicketThird.getAlreadyUseCount());
                checkTicket.setGateNo(checkTicketThird.getGateNo());
                checkTicket.setCheckDetailId(checkTicketThird.getCheckDetailId());
                if (null == id) {
                    addTicket.add(checkTicket);
                } else {
                    checkTicket.setId(id);
                    checkTicket.setModifiedTime(LocalDate.now() + " " + LocalTime.now().withNano(0));
                    updateTicket.add(checkTicket);
                }
            });
        });
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CHECK_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("景点检票数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("景点检票数据新增失败,message=" + insertBatch);
            }
        } else {
            LOGGER.info("没有产生景点检票数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(CHECK_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("景点检票数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("景点检票数据更新失败,message=" + updateBatch);
            }
        } else {
            LOGGER.info("没有景点检票数据更新;");
        }
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncSpotCheckInfo();
        }
    }

    /**
     * 同步电子订单
     */
    public void syncWebOrderInfo() {
        List<SaleTicket> addTicket = new ArrayList<>();
        List<SaleTicket> updateTicket = new ArrayList<>();
        List<ScenicArea> scenicAreas = getAllScenicArea();
        if (null == scenicAreas) {
            return;
        }
        String beginTime = getBeginTime("webSale");
        String endTime = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        String nowTime = DateTimeUtil.timeAddMinusMinutes(now, -10, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        if (DateTimeUtil.compareDate(endTime, nowTime)) {
            endTime = nowTime;
        }
        Map term = new HashMap();
        term.put("beginTime", beginTime);//查询开始时间
        term.put("endTime", endTime);//查询结束时间
        LOGGER.info("beginTime=" + beginTime + ";endTime=" + endTime);
        scenicAreas.stream().forEach(scenicArea -> {
            String scenicNo = scenicArea.getScenicNo();
            term.put("groupCode", scenicNo);
            String sign = SignUtil.buildMySign(term, appKey);//构建签名
            term.put("appCode", appCode);
            term.put("sign", sign);
            String path = "getWebOrderInfo.htm";
            JSONObject response = getResponse(term, path);
            System.out.println("WebOrderInfoResponse=" + response);
            ThirdData thirdData = JSON.toJavaObject(response, ThirdData.class);
            if (!"SUCCESS".equals(thirdData.getResult())) {
                return;
            }
            thirdData.getData().stream().map(object -> {
                WebSaleTicketThird webSaleTicketThird = JSON.toJavaObject((JSON) object, WebSaleTicketThird.class);
                return webSaleTicketThird;
            }).collect(Collectors.toList()).forEach(webSaleTicketThird -> {
                String billDetailId = webSaleTicketThird.getBillDetailId();
                String id;
                try {
                    id = QueryUtil.checkRepeat(SALE_TICKET_TABLE, "detailId", billDetailId);
                } catch (Exception e) {
                    return;
                }
                SaleTicket saleTicket = new SaleTicket();
                saleTicket.setTradeDetailId(billDetailId);
                saleTicket.setTradeDate(DateTimeUtil.msToDateStr(webSaleTicketThird.getBillDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setTicketNoCount(webSaleTicketThird.getTicketCount());
                saleTicket.setTicketModelPrice(webSaleTicketThird.getSellPrice());
                saleTicket.setTicketModelName(webSaleTicketThird.getTicketModelName());
                saleTicket.setScenicAreaCode(webSaleTicketThird.getScenicName());
                saleTicket.setPaySum(webSaleTicketThird.getTicketPrice());
                saleTicket.setUserName(webSaleTicketThird.getUserName());
                saleTicket.setTel(webSaleTicketThird.getTel());
                saleTicket.setCerNo(webSaleTicketThird.getCerNo());
                saleTicket.setBillNo(webSaleTicketThird.getBillNo());
                saleTicket.setTravelDateTime(DateTimeUtil.msToDateStr(webSaleTicketThird.getTravelDateTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setBillType(webSaleTicketThird.getClientName());
                if (null == id) {
                    addTicket.add(saleTicket);
                } else {
                    saleTicket.setId(id);
                    saleTicket.setModifiedTime(LocalDate.now() + " " + LocalTime.now().withNano(0));
                    updateTicket.add(saleTicket);
                }
            });
        });
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(SALE_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("电子订单数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("电子订单数据新增失败,message=" + insertBatch);
            }
        } else {
            LOGGER.info("没有产生电子订单数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(SALE_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("电子订单数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("电子订单数据更新失败,message=" + updateBatch);
            }
        } else {
            LOGGER.info("没有电子订单数据更新;");
        }
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncWebOrderInfo();
        }
    }

    /**
     *
     */
    public void syncWebCheckInfo() {
        List<CheckTicket> addTicket = new ArrayList<>();
        List<CheckTicket> updateTicket = new ArrayList<>();
        List<ScenicArea> scenicAreas = getAllScenicArea();
        if (null == scenicAreas) {
            return;
        }
        String beginTime = getBeginTime("webCheck");
        if (null == beginTime) {
            return;
        }
        String endTime = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
        String nowTime = DateTimeUtil.timeAddMinusMinutes(now, -10, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
        if (DateTimeUtil.compareDate(endTime, nowTime)) {
            endTime = nowTime;
        }
        Map term = new HashMap();
        term.put("beginTime", beginTime);//查询开始时间
        term.put("endTime", endTime);//查询结束时间
        LOGGER.info("beginTime=" + beginTime + ";endTime=" + endTime);
        scenicAreas.stream().forEach(scenicArea -> {
            String scenicNo = scenicArea.getScenicNo();
            term.put("groupCode", scenicNo);
            String sign = SignUtil.buildMySign(term, appKey);//构建签名
            term.put("appCode", appCode);
            term.put("sign", sign);
            String path = "getWebCheckInfo.htm";
            JSONObject response = getResponse(term, path);
            System.out.println("WebCheckInfoResponse=" + response);
            ThirdData thirdData = JSON.toJavaObject(response, ThirdData.class);
            if (!"SUCCESS".equals(thirdData.getResult())) {
                return;
            }
            thirdData.getData().stream().map(object -> {
                WebCheckTicketThird webCheckTicketThird = JSON.toJavaObject((JSON) object, WebCheckTicketThird.class);
                return webCheckTicketThird;
            }).collect(Collectors.toList()).forEach(webCheckTicketThird -> {
                String billCheckDetailId = webCheckTicketThird.getBillCheckDetailId();
                String id;
                try {
                    id = QueryUtil.checkRepeat(CHECK_TICKET_TABLE, "checkDetailId", billCheckDetailId);
                } catch (Exception e) {
                    return;
                }
                CheckTicket checkTicket = new CheckTicket();
                checkTicket.setCheckDetailId(billCheckDetailId);
                checkTicket.setTicketNo(webCheckTicketThird.getBillNo());
                checkTicket.setCheckPlace(webCheckTicketThird.getCheckName());
                checkTicket.setTicketModelName(webCheckTicketThird.getTicketModelName());
                checkTicket.setAlreadyUseCount(webCheckTicketThird.getCheckNum());
                checkTicket.setCheckWay(webCheckTicketThird.getCheckType());
                checkTicket.setUseTime(DateTimeUtil.msToDateStr(webCheckTicketThird.getUseTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                if (null == id) {
                    addTicket.add(checkTicket);
                } else {
                    checkTicket.setId(id);
                    checkTicket.setModifiedTime(LocalDate.now() + " " + LocalTime.now().withNano(0));
                    updateTicket.add(checkTicket);
                }
            });
        });
        if (addTicket.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CHECK_TICKET_TABLE, JSON.toJSON(addTicket));
            if (null != insertBatch) {
                LOGGER.info("电子订单使用数据新增成功,共新增：" + addTicket.size() + "条数据");
            } else {
                LOGGER.info("电子订单使用数据新增失败,message=" + insertBatch);
            }
        } else {
            LOGGER.info("没有产生电子订单使用数据;");
        }
        if (updateTicket.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(CHECK_TICKET_TABLE, JSON.toJSON(updateTicket));
            if (null != updateBatch) {
                LOGGER.info("电子订单使用数据更新成功,共更新：" + updateTicket.size() + "条数据;");
            } else {
                LOGGER.info("电子订单使用数据更新失败,message=" + updateBatch);
            }
        } else {
            LOGGER.info("没有电子订单使用数据更新;");
        }
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncWebCheckInfo();
        }
    }

    /**
     * 获取数据库最新的一条记录时间
     *
     * @return
     */
    private String getBeginTime(String type) {
        StringBuffer buffer = new StringBuffer();
        switch (type) {
            case "spotSale":
                buffer.append("select orderTime as beginTime")
                        .append(" from ")
                        .append(SALE_TICKET_TABLE)
                        .append(" where is_deleted='N' and user_name is null")
                        .append(" order by orderTime desc");
                break;
            case "webSale":
                buffer.append("select orderTime as beginTime")
                        .append(" from ")
                        .append(SALE_TICKET_TABLE)
                        .append(" where is_deleted='N' and user_name is not null")
                        .append(" order by orderTime desc");
                break;
            case "spotCheck":
                buffer.append("select use_time as beginTime")
                        .append(" from ")
                        .append(CHECK_TICKET_TABLE)
                        .append(" where is_deleted='N' and operator_name is not null")
                        .append(" order by use_time desc");
                break;
            case "webCheck":
                buffer.append("select use_time as beginTime")
                        .append(" from ")
                        .append(CHECK_TICKET_TABLE)
                        .append(" where is_deleted='N' and operator_name is null")
                        .append(" order by use_time desc");
                break;
            default:
                break;
        }
        String beginTime = null;
        try {
            Pagination<Map> pagination = runner.sql(buffer.toString(), 1, 1);
            if (pagination.getCount() > 0) {
                String time = String.valueOf(pagination.getData().get(0).get("beginTime"));
                beginTime = DateTimeUtil.timeAddMinusSeconds(time, 1);
            } else {
                beginTime = BEGIN_TIME;
            }
        } catch (Exception e) {
            LOGGER.error("获取同步时间异常,error=", e);
        }
        return beginTime;
    }

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
            Pagination<ScenicArea> pagination = runner.queryListByExample(ScenicArea.class, SCENIC_TABLE, example);
            if (pagination.getCount() > 0) {
                data = pagination.getData();
            }
        } catch (Exception e) {
            LOGGER.error("获取所有景区信息异常;error=", e);
        }
        return data;
    }

    public List<SpotArea> getAllSpotArea() {
        RequestExample example = new RequestExample(Integer.MAX_VALUE, 1);
        RequestExample.Criteria criteria = example.create();
        RequestExample.Param param = example.createParam();
        param.addTerm("is_deleted", "N");
        criteria.getMust().add(param);
        List<SpotArea> spots = null;
        try {
            Pagination<SpotArea> pagination = runner.queryListByExample(SpotArea.class, SPOT_TABLE, example);
            if (pagination.getCount() > 0) {
                spots = pagination.getData();
            }
        } catch (Exception e) {
            LOGGER.error("获取所有景点信息异常,error=", e);
        }
        return spots;
    }

    /**
     * @param term
     * @param path
     * @return
     */
    private JSONObject getResponse(Map term, String path) {
        String reqData = JSONObject.toJSONString(term);
        String url = host + method + path + "?req_data=" + reqData;
        String result = HttpClientUtil.httpGet(url);
        JSONObject response = null;
        if (StringUtils.isNotEmpty(result)) {
            try {
                response = JSONObject.parseObject(result);
            } catch (Exception e) {
                LOGGER.error(path + "接口返回数据异常,JSON 解析失败;response=" + response);
            }
        } else {
            LOGGER.info(path + "接口请求异常;result=" + result);
        }
        return response;
    }


}