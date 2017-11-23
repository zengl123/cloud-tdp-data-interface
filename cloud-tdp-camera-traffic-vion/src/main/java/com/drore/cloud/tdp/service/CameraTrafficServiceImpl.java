package com.drore.cloud.tdp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.common.util.DateUtil;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.traffic.CameraTrafficInfo;
import com.drore.cloud.tdp.entity.traffic.TrafficScenicArea;
import com.drore.cloud.tdp.entity.traffic.TrafficSpotArea;
import com.drore.cloud.tdp.exception.BusinessException;
import com.drore.cloud.tdp.tables.traffic.CameraTrafficTables;
import com.drore.cloud.tdp.util.SignUtil;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/2  14:25.
 */
@Component
public class CameraTrafficServiceImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CameraTrafficServiceImpl.class);
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CloudQueryRunner runner;

    private String url;
    private String appKey;
    private String secret;

    private static final String BEGIN_TIME = "2017-10-30 00:00:00";

    public JSONObject initMethod() {
        String factoryModelName = "cameraTraffic_vion";
        JSONObject object;
        try {
            object = queryUtil.queryConfigByFactoryModelName(factoryModelName);
            url = object.getString("url");
            appKey = object.getString("appKey");
            secret = object.getString("secret");
        } catch (Exception e) {
            object = null;
            LOGGER.error("获取文安客流监控配置参数url,appKey,secret异常,error=", e);
        }
        return object;
    }


    /**
     * 同步景区列表
     */
    public void syncScenicArea() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String time = String.valueOf(new Date().getTime() / 1000);
        StringBuffer sb = new StringBuffer();
        sb.append("getPlazaList?");
        sb.append("appkey=" + appKey);
        sb.append("&time=" + time);
        sb.append("&token=" + SignUtil.buildMySign(appKey, secret, time));
        String get_url = url + sb.toString();
        String responseXml = HttpClientUtil.httpGet(get_url);
        if (StringUtils.isEmpty(responseXml)) {
            LOGGER.error("请求getPlazaList接口有误;");
            return;
        }
        JSONObject response = XmlUtil.xml2json(responseXml);
        if (null == response) {
            LOGGER.error("xml转换失败;");
            return;
        }
        List<TrafficScenicArea> add = new ArrayList<>();
        List<TrafficScenicArea> update = new ArrayList<>();
        try {
            JSONArray data = response.getJSONArray("data");
            data.stream().forEach(json -> {
                JSONObject jsonObject = (JSONObject) json;
                TrafficScenicArea scenicArea = new TrafficScenicArea();
                String plazaId = jsonObject.getString("plazaid");
                scenicArea.setScenicName(jsonObject.getString("plazaid"));
                scenicArea.setScenicNo(jsonObject.getString("plazaname"));
                String id;
                try {
                    id = queryUtil.checkRepeat(CameraTrafficTables.TRAFFIC_SCENIC, "scenic_no", plazaId);
                } catch (Exception e) {
                    LOGGER.error("syncScenicArea->checkRepeat()异常,error=", e);
                    return;
                }
                if (null == id) {
                    add.add(scenicArea);
                } else {
                    scenicArea.setId(id);
                    update.add(scenicArea);
                }
            });
        } catch (Exception e) {
            LOGGER.error("getPlazaList接口返回数据异常;error=", e);
            return;
        }
        try {
            if (add.size() > 0) {
                RestMessage insertBatch = runner.insertBatch(CameraTrafficTables.TRAFFIC_SCENIC, JSON.toJSON(add));
                if (null != insertBatch) {
                    LOGGER.info("景区信息新增成功;");
                } else {
                    LOGGER.info("景区信息新增失败;");
                }
            } else {
                LOGGER.info("没有新增景区信息;");
            }
            if (update.size() > 0) {
                RestMessage updateBatch = runner.updateBatch(CameraTrafficTables.TRAFFIC_SCENIC, JSON.toJSON(update));
                if (null != updateBatch) {
                    LOGGER.info("景区信息更新成功;");
                } else {
                    LOGGER.info("景区信息更新失败;");
                }
            } else {
                LOGGER.info("没有景区信息需要跟新;");
            }
        } catch (Exception e) {
            LOGGER.error("syncScenicArea->数据存储异常;", e);
            return;
        }
    }

    /**
     * 同步景点信息
     */
    public void syncSpotArea() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        List<TrafficScenicArea> scenicAreas = getScenicArea();
        if (scenicAreas.isEmpty()) return;
        List<TrafficSpotArea> add = new ArrayList<>();
        List<TrafficSpotArea> update = new ArrayList<>();
        for (TrafficScenicArea scenicArea : scenicAreas) {
            String time = String.valueOf(new Date().getTime() / 1000);
            String plazaId = scenicArea.getScenicNo();
            String scenicName = scenicArea.getScenicName();
            Map map = new HashMap();
            map.put("plazaId", plazaId);
            StringBuffer sb = new StringBuffer();
            sb.append("getZoneList?");
            sb.append("appkey=" + appKey);
            sb.append("&time=" + time);
            sb.append("&token=" + SignUtil.buildMySign(map, appKey, secret, time));
            sb.append("&plazaid=" + plazaId);
            String get_url = url + sb.toString();
            String responseXml = HttpClientUtil.httpGet(get_url);
            if (StringUtils.isEmpty(responseXml)) {
                LOGGER.error("请求getZoneList接口有误;");
                return;
            }
            JSONObject response = XmlUtil.xml2json(responseXml);
            if (null == response) {
                LOGGER.error("xml转换失败;");
                return;
            }
            JSONArray data = response.getJSONArray("data");
            data.stream().forEach(json -> {
                JSONObject jsonObject = (JSONObject) json;
                TrafficSpotArea spotArea = new TrafficSpotArea();
                String zoneId = jsonObject.getString("zoneid");
                String zoneName = jsonObject.getString("zonename");
                spotArea.setScenicName(scenicName);
                spotArea.setScenicNo(plazaId);
                spotArea.setSpotName(zoneName);
                spotArea.setSpotNo(zoneId);
                String id;
                try {
                    id = queryUtil.checkRepeat(CameraTrafficTables.TRAFFIC_SPOT, "spot_no", zoneId);
                } catch (Exception e) {
                    LOGGER.error("syncSpotArea->checkRepeat()异常,error=", e);
                    return;
                }
                if (null == id) {
                    add.add(spotArea);
                } else {
                    spotArea.setId(id);
                    update.add(spotArea);
                }
            });
        }
        if (add.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CameraTrafficTables.TRAFFIC_DATA, JSON.toJSON(add));
            if (null == insertBatch) {
                LOGGER.info("新增景点信息成功,insertBatch=" + JSON.toJSON(add));
            } else {
                LOGGER.info("新增景点信息失败;");
            }
        } else {
            LOGGER.info("没有新增景点信息;");
        }
        if (update.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(CameraTrafficTables.TRAFFIC_DATA, JSON.toJSON(update));
            if (null == updateBatch) {
                LOGGER.info("景点信息更新成功,updateBatch=" + JSON.toJSON(update));
            } else {
                LOGGER.info("景点信息更新失败;");
            }
        } else {
            LOGGER.info("没有静待你信息需要更新;");
        }
    }

    /**
     * 获取区域数据
     */
    public void syncZonePassenger() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        List<TrafficSpotArea> spotAreas = getSpotArea();
        if (spotAreas.isEmpty()) return;
        Map<String, String> timeMap = getTime();
        if (timeMap.isEmpty()) return;
        String beginTime = timeMap.get("beginTime");
        String endTime = timeMap.get("endTime");
        String nowTime = timeMap.get("nowTime");
        Integer granularity = 0;
        List<CameraTrafficInfo> list = new ArrayList<>();
        spotAreas.stream().forEach(trafficSpotArea -> {
            String time = String.valueOf(new Date().getTime() / 1000);
            String plazaId = trafficSpotArea.getScenicNo();
            String zoneId = trafficSpotArea.getSpotNo();
            String zoneName = trafficSpotArea.getSpotName();
            Map map = new HashMap();
            map.put("plazaId", plazaId);
            map.put("zoneId", zoneId);
            map.put("startTime", beginTime);
            map.put("endTime", endTime);
            map.put("granularity", granularity);
            StringBuffer sb = new StringBuffer();
            sb.append("getZonePassenger?");
            sb.append("appkey=" + appKey);
            sb.append("&time=" + time);
            sb.append("&token=" + SignUtil.buildMySign(map, appKey, secret, time));
            sb.append("&plazaid=" + plazaId);
            sb.append("&zoneid=" + zoneId);
            sb.append("&starttime=" + beginTime);
            sb.append("&endtime=" + endTime);
            sb.append("&granularity=" + granularity);
            String get_url = url + sb.toString();
            String responseXml = HttpClientUtil.httpGet(get_url);
            if (StringUtils.isEmpty(responseXml)) {
                LOGGER.info("请求getZoneList接口有误;");
                return;
            }
            JSONObject response = XmlUtil.xml2json(responseXml);
            if (response.isEmpty()) {
                LOGGER.error("xml转换失败;");
                return;
            }
            try {
                JSONArray data = response.getJSONArray("data");
                List<CameraTrafficInfo> trafficInfos = data.stream().map(json -> (JSONObject) json).map(json -> {
                    Integer inNum = json.getInteger("innum");
                    Integer outNum = json.getInteger("outnum");
                    Integer fromIn = inNum - outNum < 0 ? 0 : inNum - outNum;//在园人数
                    String countTime = json.getString("counttime");
                    CameraTrafficInfo trafficInfo = new CameraTrafficInfo();
                    trafficInfo.setOrgIndex(plazaId);
                    trafficInfo.setFromIn(fromIn);
                    trafficInfo.setStartTime(countTime);
                    trafficInfo.setObjectIndexCode(zoneId);
                    trafficInfo.setEnterNum(inNum);
                    trafficInfo.setLeaveNum(outNum);
                    trafficInfo.setOrgName(zoneName);
                    return trafficInfo;
                }).collect(Collectors.toList());
                list.addAll(trafficInfos);
            } catch (Exception e) {
                LOGGER.error("数据结构异常,error=", e);
                return;
            }
        });
        if (list.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CameraTrafficTables.TRAFFIC_DATA, JSON.toJSON(list));
            if (null != insertBatch) {
                LOGGER.info("新增区域客流信息成功;共新增：" + list.size() + "条数据;");
            } else {
                LOGGER.info("新增区域客流信息失败;");
            }
        } else {
            LOGGER.info("没有新增区域客流数据;startTime=" + beginTime + ",endTime=" + endTime + ",nowTime=" + nowTime);
            return;
        }
        if (endTime.compareTo(nowTime) < 0) syncZonePassenger();
    }

    /**
     * 获取没次的同步时间
     *
     * @return
     */
    private Map<String, String> getTime() {
        Map<String, String> map = new HashMap<>();
        RequestExample example = new RequestExample(1, 1);
        example.addSort("start_time", "desc");//获取数据库最后一条数据的时间
        Pagination<CameraTrafficInfo> cameraTrafficInfoPagination;
        try {
            cameraTrafficInfoPagination = runner.queryListByExample(CameraTrafficInfo.class, CameraTrafficTables.TRAFFIC_DATA, example);
            String startTime;
            if (cameraTrafficInfoPagination.getCount() > 0) {
                startTime = cameraTrafficInfoPagination.getData().get(0).getStartTime();
                startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).minusSeconds(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));//避免数据重复,在原有时间上加1s
            } else {
                startTime = BEGIN_TIME;//如果数据库没数据,启用默认时间同步
            }
            String endTime;
            String plusOne = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String nowTime = LocalDateTime.now().withNano(0).minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (plusOne.compareTo(nowTime) > 0) {
                endTime = nowTime;
            } else {
                endTime = plusOne;
            }
            map.put("beginTime", startTime);
            map.put("endTime", endTime);
            map.put("nowTime", nowTime);
        } catch (Exception e) {
            map = null;
            LOGGER.error("获取监控客流同步时间异常;error=", e);
        }
        return map;
    }

    private List<TrafficScenicArea> getScenicArea() {
        RequestExample example = new RequestExample();
        example.addSort("sort", "asc");
        List<TrafficScenicArea> data = null;
        try {
            Pagination<TrafficScenicArea> scenicArea = runner.queryListByExample(TrafficScenicArea.class, CameraTrafficTables.TRAFFIC_SCENIC, example);
            if (scenicArea.getCount() > 0) data = scenicArea.getData();
            return data;
        } catch (Exception e) {
            LOGGER.error("获取景区列表异常,error=", e);
        }
        return data;
    }

    private List<TrafficSpotArea> getSpotArea() {
        RequestExample example = new RequestExample();
        example.addSort("sort", "asc");
        List<TrafficSpotArea> data = null;
        try {
            Pagination<TrafficSpotArea> scenicArea = runner.queryListByExample(TrafficSpotArea.class, CameraTrafficTables.TRAFFIC_SPOT, example);
            if (scenicArea.getCount() > 0) data = scenicArea.getData();
            return data;
        } catch (Exception e) {
            LOGGER.error("获取景点列表异常,error=", e);
        }
        return data;
    }
}
