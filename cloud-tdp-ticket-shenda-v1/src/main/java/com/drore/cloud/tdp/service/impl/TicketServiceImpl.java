package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.common.ticket.TicketCommon;
import com.drore.cloud.tdp.entity.*;
import com.drore.cloud.tdp.entity.ticket.CheckTicket;
import com.drore.cloud.tdp.entity.ticket.SaleTicket;
import com.drore.cloud.tdp.tables.ticket.TicketTables;
import com.drore.cloud.tdp.util.SignUtil;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @Autowired
    private CloudQueryRunner runner;
    @Autowired
    private TicketCommon ticketCommon;
    @Autowired
    private QueryUtil queryUtil;

    private static final String BEGIN_TIME = "2017-10-23 00:00:00";

    /**
     * 获取深大票务配置参数
     */
    private void initMethod() {
        String factoryModelName = "shenDaTicketV1_0_2";
        JSONObject config = queryUtil.queryConfigByFactoryModelName(factoryModelName);
        appCode = config.getString("appCode");
        host = config.getString("url");
        method = config.getString("method");
        appKey = config.getString("appKey");
        areaCode = config.getString("areaCode");
    }

    private Boolean checkParam() {
        if (null == host) {
            try {
                initMethod();
                return true;
            } catch (Exception e) {
                LOGGER.error("获取深大票务配置参数异常,error=", e);
                return false;
            }
        } else {
            return true;
        }
    }

    public void syncScenicArea() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
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
                String id = queryUtil.checkRepeat(TicketTables.TICKET_SCENIC_TABLE, "scenic_no", scenicThird.getGroupCode());
                ScenicArea scenicArea = new ScenicArea();
                scenicArea.setScenicName(scenicThird.getGroupName());
                scenicArea.setScenicNo(scenicThird.getGroupCode());
                if (null == id) {
                    addScenic.add(scenicArea);
                } else {
                    scenicArea.setId(id);
                    scenicArea.setModifiedTime(DateTimeUtil.getNowTime());
                    updateScenic.add(scenicArea);
                }
            });
        } catch (Exception e) {
            LOGGER.error("获取接口数据异常,error=", e);
        }
        ticketCommon.saveOrUpdateScenic(addScenic, updateScenic);
    }


    public void syncSpotArea() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
        List<SpotArea> addSpot = new ArrayList<>();
        List<SpotArea> updateSpot = new ArrayList<>();
        List<ScenicArea> scenicAreas = ticketCommon.getAllScenicArea();
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
                    String id = queryUtil.checkRepeat(TicketTables.TICKET_SPOT_TABLE, "spot_no", spotThird.getParkCode());
                    SpotArea spotArea = new SpotArea();
                    spotArea.setSpotName(spotThird.getParkFullName());
                    spotArea.setSpotNo(spotThird.getParkCode());
                    spotArea.setScenicNo(groupCode);
                    if (null == id) {
                        addSpot.add(spotArea);
                    } else {
                        spotArea.setId(id);
                        spotArea.setModifiedTime(DateTimeUtil.getNowTime());
                        updateSpot.add(spotArea);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("获取接口数据异常,error=", e);
            }
        });
        ticketCommon.saveOrUpdateSpot(addSpot, updateSpot);
    }

    /**
     * 同步景点售票
     */
    public void syncSpotSaleInfo() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
        List<SaleTicket> addTicket = new ArrayList<>();
        List<SaleTicket> updateTicket = new ArrayList<>();
        List<SpotArea> allSpotArea = ticketCommon.getAllSpotArea();
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
            LOGGER.info("spot=" + spotArea.getSpotName() + ",SpotsSaleInfoResponse = " + response);
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
                    id = queryUtil.checkRepeat(TicketTables.SALE_TICKET_TABLE, "sale_detail_id", tradeDetailId);
                } catch (Exception e) {
                    LOGGER.error("syncSpotSaleInfo-checkRepeat()异常,error=", e);
                    return;
                }
                SaleTicket saleTicket = new SaleTicket();
                saleTicket.setScenicName(saleTicketThird.getScenicAreaCode());
                saleTicket.setSpotName(saleTicketThird.getParkFullName());
                saleTicket.setTouristType(saleTicketThird.getClientType());
                saleTicket.setTicketModelName(saleTicketThird.getTicketModelName());
                saleTicket.setTicketKindName(saleTicketThird.getDictDetailName());
                saleTicket.setSaleTicketPlace(saleTicketThird.getTakeTicketPlace());
                saleTicket.setOperatorName(saleTicketThird.getOperatorName());
                saleTicket.setSaleTime(DateTimeUtil.msToDateStr(saleTicketThird.getTradeDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setSaleTicketNum(saleTicketThird.getTicketNoCount());
                saleTicket.setUnitPrice(saleTicketThird.getTicketModelPrice());
                saleTicket.setTotalPrice(saleTicketThird.getPaySum());
                saleTicket.setChannel(saleTicketThird.getBillType());
                saleTicket.setProvinceName(saleTicketThird.getAreaName());
                saleTicket.setTradeDetailId(saleTicketThird.getTradeDetailId());
                if (null == id) {
                    addTicket.add(saleTicket);
                } else {
                    saleTicket.setId(id);
                    saleTicket.setModifiedTime(DateTimeUtil.getNowTime());
                    updateTicket.add(saleTicket);
                }
            });
        });
        ticketCommon.saveOrUpdateSaleTicket(addTicket, updateTicket);
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncSpotSaleInfo();
        }
    }

    /**
     * 同步景点检票
     */
    public void syncSpotCheckInfo() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
        List<CheckTicket> addTicket = new ArrayList<>();
        List<CheckTicket> updateTicket = new ArrayList<>();
        List<SpotArea> allSpotArea = ticketCommon.getAllSpotArea();
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
            LOGGER.info("spot=" + spotArea.getSpotName() + ",SpotsCheckInfoResponse=" + response);
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
                    id = queryUtil.checkRepeat(TicketTables.CHECK_TICKET_TABLE, "check_detail_id", checkDetailId);
                } catch (Exception e) {
                    LOGGER.error("syncSpotCheckInfo-checkRepeat()异常,error=", e);
                    return;
                }
                CheckTicket checkTicket = new CheckTicket();
                checkTicket.setScenicName(checkTicketThird.getScenicAreaCode());
                checkTicket.setSpotName(checkTicketThird.getParkFullName());
                checkTicket.setTouristType(checkTicketThird.getClientType());
                checkTicket.setOperatorName(checkTicketThird.getOperatorName());
                checkTicket.setTicketModelName(checkTicketThird.getTicketModelName());
                checkTicket.setTicketKindName(checkTicketThird.getTicketKindName());
                checkTicket.setSaleTicketPlace(checkTicketThird.getTakeTicketPlace());
                checkTicket.setSaleTime(DateTimeUtil.msToDateStr(checkTicketThird.getTradeDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                checkTicket.setUseTime(DateTimeUtil.msToDateStr(checkTicketThird.getUseTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                checkTicket.setCheckTicketPlace(checkTicketThird.getCheckPlace());
                checkTicket.setSaleTicketPrice(checkTicketThird.getTicketModelPrice());
                checkTicket.setSaleModel(checkTicketThird.getSaleModel());
                checkTicket.setUseNum(checkTicketThird.getAlreadyUseCount());
                checkTicket.setGateNo(checkTicketThird.getGateNo());
                checkTicket.setCheckDetailId(checkTicketThird.getCheckDetailId());
                if (null == id) {
                    addTicket.add(checkTicket);
                } else {
                    checkTicket.setId(id);
                    checkTicket.setModifiedTime(DateTimeUtil.getNowTime());
                    updateTicket.add(checkTicket);
                }
            });
        });
        ticketCommon.saveOrUpdateCheckTicket(addTicket, updateTicket);
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncSpotCheckInfo();
        }
    }

    /**
     * 同步电子订单
     */
    public void syncWebOrderInfo() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
        List<SaleTicket> addTicket = new ArrayList<>();
        List<SaleTicket> updateTicket = new ArrayList<>();
        List<ScenicArea> scenicAreas = ticketCommon.getAllScenicArea();
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
            LOGGER.info("WebOrderInfoResponse=" + response);
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
                    id = queryUtil.checkRepeat(TicketTables.SALE_TICKET_TABLE, "sale_detail_id", billDetailId);
                } catch (Exception e) {
                    LOGGER.error("syncWebOrderInfo-checkRepeat()异常,error=", e);
                    return;
                }
                SaleTicket saleTicket = new SaleTicket();
                saleTicket.setTradeDetailId(billDetailId);
                saleTicket.setSaleTime(DateTimeUtil.msToDateStr(webSaleTicketThird.getBillDate(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setSaleTicketNum(webSaleTicketThird.getTicketCount());
                saleTicket.setUnitPrice(webSaleTicketThird.getSellPrice());
                saleTicket.setTicketModelName(webSaleTicketThird.getTicketModelName());
                saleTicket.setScenicName(webSaleTicketThird.getScenicName());
                saleTicket.setTotalPrice(webSaleTicketThird.getTicketPrice());
                saleTicket.setUserName(webSaleTicketThird.getUserName());
                saleTicket.setPhoneNumber(webSaleTicketThird.getTel());
                saleTicket.setIdCard(webSaleTicketThird.getCerNo());
                saleTicket.setTicketNo(webSaleTicketThird.getBillNo());
                saleTicket.setUseTime(DateTimeUtil.msToDateStr(webSaleTicketThird.getTravelDateTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                saleTicket.setChannel(webSaleTicketThird.getClientName());
                if (null == id) {
                    addTicket.add(saleTicket);
                } else {
                    saleTicket.setId(id);
                    saleTicket.setModifiedTime(DateTimeUtil.getNowTime());
                    updateTicket.add(saleTicket);
                }
            });
        });
        ticketCommon.saveOrUpdateSaleTicket(addTicket, updateTicket);
        if (!DateTimeUtil.compareDate(endTime, nowTime)) {
            syncWebOrderInfo();
        }
    }

    /**
     *
     */
    public void syncWebCheckInfo() {
        Boolean aBoolean = checkParam();
        if (!aBoolean) {
            return;
        }
        List<CheckTicket> addTicket = new ArrayList<>();
        List<CheckTicket> updateTicket = new ArrayList<>();
        List<ScenicArea> scenicAreas = ticketCommon.getAllScenicArea();
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
            LOGGER.info("WebCheckInfoResponse=" + response);
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
                    id = queryUtil.checkRepeat(TicketTables.CHECK_TICKET_TABLE, "check_detail_id", billCheckDetailId);
                } catch (Exception e) {
                    LOGGER.error("syncWebCheckInfo-checkRepeat()异常,error=", e);
                    return;
                }
                CheckTicket checkTicket = new CheckTicket();
                checkTicket.setCheckDetailId(billCheckDetailId);
                checkTicket.setTicketNo(webCheckTicketThird.getBillNo());
                checkTicket.setCheckTicketPlace(webCheckTicketThird.getCheckName());
                checkTicket.setTicketModelName(webCheckTicketThird.getTicketModelName());
                checkTicket.setUseNum(webCheckTicketThird.getCheckNum());
                checkTicket.setCheckType(webCheckTicketThird.getCheckType());
                checkTicket.setUseTime(DateTimeUtil.msToDateStr(webCheckTicketThird.getUseTime(), DateTimeUtil.YYYY_MM_DD_HH_MM_SS));
                if (null == id) {
                    addTicket.add(checkTicket);
                } else {
                    checkTicket.setId(id);
                    checkTicket.setModifiedTime(DateTimeUtil.getNowTime());
                    updateTicket.add(checkTicket);
                }
            });
        });
        ticketCommon.saveOrUpdateCheckTicket(addTicket, updateTicket);
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
                buffer.append("select sale_time as beginTime")
                        .append(" from ")
                        .append(TicketTables.SALE_TICKET_TABLE)
                        .append(" where is_deleted='N' and user_name is null")
                        .append(" order by sale_time desc");
                break;
            case "webSale":
                buffer.append("select sale_time as beginTime")
                        .append(" from ")
                        .append(TicketTables.SALE_TICKET_TABLE)
                        .append(" where is_deleted='N' and user_name is not null")
                        .append(" order by sale_time desc");
                break;
            case "spotCheck":
                buffer.append("select use_time as beginTime")
                        .append(" from ")
                        .append(TicketTables.CHECK_TICKET_TABLE)
                        .append(" where is_deleted='N' and operator_name is not null")
                        .append(" order by use_time desc");
                break;
            case "webCheck":
                buffer.append("select use_time as beginTime")
                        .append(" from ")
                        .append(TicketTables.CHECK_TICKET_TABLE)
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
                LOGGER.error(path + "接口返回数据异常,JSON 解析失败;response=", response);
            }
        } else {
            LOGGER.info(path + "接口请求异常;result=" + result);
        }
        return response;
    }
}