package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.tdp.entity.RestMessage;
import com.drore.cloud.tdp.service.StrongService;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/16  15:27.
 */
@Service
public class StrongServiceImpl implements StrongService {
    @Value("${strong_url}")
    private String host;
    @Autowired
    private ResultUtil resultUtil;
    private static final String STATUS = "1";

    /**
     * 广播列表
     *
     * @return
     */
    @Override
    public RestMessage broadcastList() {
        String url = host + "/BroadcastList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println("data = " + data);
        JSONArray info = JSONArray.parseArray(String.valueOf(data.get("info")));
        List<LinkedHashMap> collect = info.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String notes = map.getString("notes");
            Integer nowState = map.getInteger("nowState");
            String devID = map.getString("devID");
            object.put("device_id", devID);
            object.put("notes", notes);
            object.put("status", nowState);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取广播列表成功");
    }

    /**
     * 水位列表
     *
     * @return
     */
    @Override
    public RestMessage waterList() {
        String url = host + "/WaterList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println("data = " + data);
        JSONArray waterBean = JSONArray.parseArray(String.valueOf(data.get("waterBean")));
        List<LinkedHashMap> collect = waterBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String notes = map.getString("notes");
            Integer nowState = map.getInteger("state");
            String devID = map.getString("water");
            String gtwy = map.getString("gtwy");
            object.put("device_id", devID);
            object.put("notes", notes);
            object.put("status", nowState);
            object.put("gtwy", gtwy);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取水位列表成功");
    }

    /**
     * 水闸列表
     *
     * @return
     */
    @Override
    public RestMessage gateList() {
        String url = host + "/GateList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        JSONArray gateBean = JSONArray.parseArray(String.valueOf(data.get("gateBean")));
        List<LinkedHashMap> collect = gateBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String notes = map.getString("notes");
            Integer openState = map.getInteger("openState");
            Integer stopState = map.getInteger("stopState");
            Integer closeState = map.getInteger("closeState");
            Integer nowState = map.getInteger("nowState");
            String devID = map.getString("gate");
            object.put("device_id", devID);
            object.put("notes", notes);
            object.put("status", nowState);
            object.put("open_state", openState);
            object.put("stop_state", stopState);
            object.put("close_state", closeState);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取水闸列表成功");
    }

    /**
     * 路灯分组列表
     *
     * @return
     */
    @Override
    public RestMessage lampGroupList() {
        String url = host + "/getList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println(data);
        JSONArray groupListBean = JSONArray.parseArray(String.valueOf(data.get("groupListBean")));
        List<LinkedHashMap> collect = groupListBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String groupId = map.getString("groupid");
            Integer remoteId = map.getInteger("remoteid");
            String address = map.getString("addr");
            String notes = map.getString("notes");
            Integer state = map.getInteger("state");
            object.put("group_id", groupId);
            object.put("notes", notes);
            object.put("remote_id", remoteId);
            object.put("address_id", address);
            object.put("status", state);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取路灯分组列表成功");
    }

    /**
     * 路灯列表
     *
     * @return
     */
    @Override
    public RestMessage lampList(String groupId) {
        String url = host + "/LampList?group=" + groupId;
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println(data);
        JSONArray info = JSONArray.parseArray(String.valueOf(data.get("info")));
        List<LinkedHashMap> collect = info.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String devID = map.getString("devID");
            String road = map.getString("road");
            String devbh = map.getString("devbh");
            String group = map.getString("group");
            String remoteId = map.getString("remoteid");
            Integer nowState = map.getInteger("nowState");
            object.put("device_id", devID);
            object.put("road", road);
            object.put("group", group);
            object.put("remote", remoteId);
            object.put("devbh", devbh);
            object.put("status", nowState);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取路灯列表成功");
    }

    /**
     * 远程开关列表
     *
     * @return
     */
    @Override
    public RestMessage remoteList() {
        String url = host + "/RemoteList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println(data);
        JSONArray remoteBean = JSONArray.parseArray(String.valueOf(data.get("remoteBean")));
        List<LinkedHashMap> collect = remoteBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String devID = map.getString("remote");
            String workMode = map.getString("workmode");
            String status = map.getString("nowState");
            String nowStat1 = map.getString("nowStat1");
            String nowStat2 = map.getString("nowStat2");
            String nowStat3 = map.getString("nowStat3");
            String nowStat4 = map.getString("nowStat4");
            String openTime1 = map.getString("open_time1");
            String openTime2 = map.getString("open_time2");
            String openTime3 = map.getString("open_time3");
            String openTime4 = map.getString("open_time4");
            String closeTime1 = map.getString("close_time1");
            String closeTime2 = map.getString("close_time2");
            String closeTime3 = map.getString("close_time3");
            String closeTime4 = map.getString("close_time4");
            object.put("device_id", devID);
            object.put("work_mode", workMode);
            object.put("status", status);
            object.put("status1", nowStat1);
            object.put("status2", nowStat2);
            object.put("status3", nowStat3);
            object.put("status4", nowStat4);
            object.put("openTime1", openTime1);
            object.put("openTime2", openTime2);
            object.put("openTime3", openTime3);
            object.put("openTime4", openTime4);
            object.put("closeTime1", closeTime1);
            object.put("closeTime2", closeTime2);
            object.put("closeTime3", closeTime3);
            object.put("closeTime4", closeTime4);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取远程开关列表成功");
    }

    /**
     * 网关列表
     *
     * @return
     */
    @Override
    public RestMessage gateWayList() {
        String url = host + "/GtwyList";
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSON.parseObject(result);
        System.out.println(data);
        JSONArray gateWayBean = JSONArray.parseArray(String.valueOf(data.get("gtwyBean")));
        List<LinkedHashMap> collect = gateWayBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String gateWayId = map.getString("gtwyID");
            String gateWayIp = map.getString("gtwy");
            String gateCount = map.getString("gateCount");
            String broCount = map.getString("broCount");
            String remoteCount = map.getString("remoteCount");
            String lampCount = map.getString("lampCount");
            String waterCount = map.getString("waterCount");
            object.put("gateWayId", gateWayId);
            object.put("gateWayIp", gateWayIp);
            object.put("gateCount", gateCount);
            object.put("broCount", broCount);
            object.put("remoteCount", remoteCount);
            object.put("lampCount", lampCount);
            object.put("waterCount", waterCount);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, "获取网关列表成功");
    }

    /**
     * 根据广播id关闭广播
     *
     * @return
     */
    @Override
    public RestMessage closeBroadcast(String broadcastId) {
        String url = host + "/CloseBroadcast" + "?bro=" + broadcastId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据广播id开启广播
     *
     * @param broadcastId
     * @return
     */
    @Override
    public RestMessage openBroadcast(String broadcastId) {
        String url = host + "/OpenBroadcast" + "?bro=" + broadcastId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据组id分组开启广播
     *
     * @return
     */
    @Override
    public RestMessage openBroadcastGroup(String groupId) {
        String url = host + "/OpenGroupBroadcast" + "?group=" + groupId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据组id分组关闭广播
     *
     * @return
     */
    @Override
    public RestMessage closeBroadcastGroup(String groupId) {
        String url = host + "/CloseGroupBroadcast" + "?group=" + groupId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 关闭|开启设备
     *
     * @param url
     * @return
     */
    private List<LinkedHashMap> openOrCloseDevice(String url) {
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println(data);
        JSONArray info = data.getJSONArray("info");
        return info.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("devID");
            String nowState = map.getString("nowState");
            String notes = map.getString("result");
            object.put("deviceId", deviceId);
            object.put("status", nowState);
            object.put("message", notes);
            return object;
        }).collect(Collectors.toList());
    }

    /**
     * 根据水闸id开启水闸
     *
     * @param gateId
     * @return
     */
    @Override
    public RestMessage openGate(String gateId) {
        String url = host + "/OpenGate" + "?gate=" + gateId;
        List<LinkedHashMap> list = gate(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据水闸id暂停水闸
     *
     * @param gateId
     * @return
     */
    @Override
    public RestMessage stopGate(String gateId) {
        String url = host + "/StopGate" + "?gate=" + gateId;
        List<LinkedHashMap> list = gate(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据水闸id关闭水闸
     *
     * @param gateId
     * @return
     */
    @Override
    public RestMessage closeGate(String gateId) {
        String url = host + "/CloseGate" + "?gate=" + gateId;
        List<LinkedHashMap> list = gate(url);
        return resultUtil.success(list, null);
    }

    /**
     * 开启|暂停|停止水闸
     *
     * @return
     */
    private List<LinkedHashMap> gate(String url) {
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println("data = " + data);
        JSONArray gateBean = data.getJSONArray("gateBean");
        return gateBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("gate");
            String nowState = map.getString("nowState");
            String notes = map.getString("result");
            object.put("deviceId", deviceId);
            object.put("status", nowState);
            object.put("message", notes);
            return object;
        }).collect(Collectors.toList());
    }

    /**
     * 根据路灯id开启路灯
     *
     * @return
     */
    @Override
    public RestMessage openLamp(String lampId) {
        String url = host + "/OpenLamp" + "?lamp=" + lampId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据路灯id关闭路灯
     *
     * @return
     */
    @Override
    public RestMessage closeLamp(String lampId) {
        String url = host + "/CloseLamp" + "?lamp=" + lampId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据路灯分组id开启路灯
     *
     * @return
     */
    @Override
    public RestMessage openLampGroup(String groupId) {
        String url = host + "/OpenGroupLamp" + "?group=" + groupId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据路灯分组id关闭路灯
     *
     * @return
     */
    @Override
    public RestMessage closeLampGroup(String groupId) {
        String url = host + "/CloseGroupLamp" + "?group=" + groupId;
        List<LinkedHashMap> list = openOrCloseDevice(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据远程开关id开启远程开关
     *
     * @return
     */
    @Override
    public RestMessage openRemote(String remoteId, String addressId) {
        String url = host + "/OpenRemote" + "?remote=" + remoteId + "&addr=" + addressId;
        List<LinkedHashMap> list = remote(url);
        return resultUtil.success(list, null);
    }

    /**
     * 根据远程开关id关闭远程开关
     *
     * @return
     */
    @Override
    public RestMessage closeRemote(String remoteId, String addressId) {
        String url = host + "/CloseRemote" + "?remote=" + remoteId + "&addr=" + addressId;
        List<LinkedHashMap> list = remote(url);
        return resultUtil.success(list, null);
    }

    /**
     * 开启|关闭远程开关
     *
     * @param url
     * @return
     */
    private List<LinkedHashMap> remote(String url) {
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println("data = " + data);
        JSONArray remoteBean = data.getJSONArray("remoteBean");
        return remoteBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("remote");
            String nowState = map.getString("nowState");
            String notes = map.getString("result");
            object.put("deviceId", deviceId);
            object.put("status", nowState);
            object.put("message", notes);
            return object;
        }).collect(Collectors.toList());
    }

    /**
     * 设置定时开关时间
     *
     * @param
     * @return
     */
    @Override
    public RestMessage setReservation(String remoteId, String addressId, String openTime, String closeTime) {
        return null;
    }

    /**
     * 获取定时开关时间(暂无)
     *
     * @param remoteId
     * @return
     */
    @Override
    public RestMessage getReservation(String remoteId) {
        String url = host + "/getReservation" + "?remote=" + remoteId;
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        JSONArray remoteBean = data.getJSONArray("remoteBean");
        return null;
    }

    /**
     * 设置工作模式
     *
     * @return
     */
    @Override
    public RestMessage setWorkingMode(String remoteId, String mode) {
        String url = host + "/setWorkingMode" + "?remote=" + remoteId + "&mode=" + mode;
        List<LinkedHashMap> list = workModel(url);
        return resultUtil.success(list, null);
    }

    /**
     * 获取工作模式
     *
     * @param remoteId
     * @return
     */
    @Override
    public RestMessage getWorkingMode(String remoteId) {
        String url = host + "/getWorkingMode" + "?remote=" + remoteId;
        List<LinkedHashMap> list = workModel(url);
        return resultUtil.success(list, null);
    }

    /**
     * 设置|获取工作模式
     *
     * @param url
     * @return
     */
    private List<LinkedHashMap> workModel(String url) {
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println("data = " + data);
        JSONArray remoteBean = data.getJSONArray("remoteBean");
        return remoteBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("remote");
            String workMode = map.getString("workmode");
            String nowState = map.getString("nowState");
            object.put("deviceId", deviceId);
            object.put("workMode", workMode);
            object.put("status", nowState);
            return object;
        }).filter(map -> STATUS.equals(String.valueOf(map.get("status")))).collect(Collectors.toList());//当status=1时，设置|获取工作模式成功
    }

    /**
     * 获取工作电压
     *
     * @param vcId
     * @return
     */
    @Override
    public RestMessage getVC(String vcId) {
        String url = host + "/getVC?vc=" + vcId;
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println("data = " + data);
        JSONArray vcBean = data.getJSONArray("vcbean");
        List<LinkedHashMap> collect = vcBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("vc");
            String createTime = map.getString("cjsj");
            String c1 = map.getString("c1");
            String c2 = map.getString("c2");
            String c3 = map.getString("c3");
            String v1 = map.getString("v1");
            String v2 = map.getString("v2");
            String v3 = map.getString("v3");
            object.put("deviceId", deviceId);
            object.put("createTime", createTime);
            object.put("c1", c1);
            object.put("c2", c2);
            object.put("c3", c3);
            object.put("v1", v1);
            object.put("v2", v2);
            object.put("v3", v3);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, null);
    }

    /**
     * 获取水位信息
     *
     * @param waterId
     * @return
     */
    @Override
    public RestMessage getWaterLevel(String waterId) {
        String url = host + "/getWaterLevel?water=" + waterId;
        String result = HttpClientUtil.httpGet(url);
        JSONObject data = JSONObject.parseObject(result);
        System.out.println("data = " + data);
        JSONArray waterBean = data.getJSONArray("waterBean");
        List<LinkedHashMap> collect = waterBean.stream().map(map -> (JSONObject) map).map(map -> {
            LinkedHashMap object = new LinkedHashMap();
            String deviceId = map.getString("water");
            String height = map.getString("swgd");
            String date = map.getString("date");
            String status = map.getString("state");
            object.put("deviceId", deviceId);
            object.put("height", height);
            object.put("date", date);
            object.put("status", status);
            return object;
        }).collect(Collectors.toList());
        return resultUtil.success(collect, null);
    }
}
