package com.drore.cloud.tdp.control;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.tdp.entity.RestMessage;
import com.drore.cloud.tdp.service.StrongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/16  15:28.
 */
@RestController
@RequestMapping(value = "/tdp/strong/")
@ControllerAdvice
public class StrongApplication {
    @Autowired
    private StrongService strongService;

    /**
     * 广播列表
     *
     * @return
     */
    @RequestMapping(value = "broadcastList")
    public RestMessage getBroadcastList() {
        return strongService.broadcastList();
    }

    /**
     * 水位列表
     *
     * @return
     */
    @RequestMapping(value = "waterList")
    public RestMessage getWaterList() {
        return strongService.waterList();
    }

    /**
     * 水闸列表
     *
     * @return
     */
    @RequestMapping(value = "gateList")
    public RestMessage getGateList() {
        return strongService.gateList();
    }

    @RequestMapping(value = "lampGroupList")
    public RestMessage getLampGroupList() {
        return strongService.lampGroupList();
    }

    /**
     * 路灯分组列表
     *
     * @return
     */
    @RequestMapping(value = "lampList")
    public RestMessage getLampList(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        return strongService.lampList(groupId);
    }

    /**
     * 远程开关列表
     *
     * @return
     */
    @RequestMapping(value = "remoteList")
    public RestMessage getRemoteList() {
        return strongService.remoteList();
    }

    /**
     * 网关列表
     *
     * @return
     */
    @RequestMapping(value = "gateWayList")
    public RestMessage getGateWayList() {
        return strongService.gateWayList();
    }

    /**
     * 根据广播id开启广播
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "openBroadcast")
    public RestMessage openBroadcast(@RequestBody JSONObject param) {
        String broadcastId = param.getString("broadcastId");
        return strongService.openBroadcast(broadcastId);
    }

    /**
     * 根据广播id关闭广播
     *
     * @return
     */
    @RequestMapping(value = "closeBroadcast")
    public RestMessage closeBroadcast(@RequestBody JSONObject param) {
        String broadcastId = param.getString("broadcastId");
        return strongService.closeBroadcast(broadcastId);
    }

    /**
     * 根据组id分组开启广播
     *
     * @return
     */
    @RequestMapping(value = "openBroadcastGroup")
    public RestMessage openBroadcastGroup(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        return strongService.openBroadcastGroup(groupId);
    }

    /**
     * 根据组id分组关闭广播
     *
     * @return
     */
    @RequestMapping(value = "closeBroadcastGroup")
    public RestMessage closeBroadcastGroup(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        return strongService.closeBroadcastGroup(groupId);
    }

    /**
     * 根据水闸id开启水闸
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "openGate")
    public RestMessage openGate(@RequestBody JSONObject param) {
        String gateId = param.getString("gateId");
        return strongService.openGate(gateId);
    }

    /**
     * 根据水闸id暂停水闸
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "stopGate")
    public RestMessage stopGate(@RequestBody JSONObject param) {
        String gateId = param.getString("gateId");
        return strongService.stopGate(gateId);
    }

    /**
     * 根据水闸id关闭水闸
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "closeGate")
    public RestMessage closeGate(@RequestBody JSONObject param) {
        String gateId = param.getString("gateId");
        return strongService.closeGate(gateId);
    }

    /**
     * 根据路灯id开启路灯
     *
     * @return
     */
    @RequestMapping(value = "openLamp")
    public RestMessage openLamp(@RequestBody JSONObject param) {
        String lampId = param.getString("lampId");
        return strongService.openLamp(lampId);
    }

    /**
     * 根据路灯id关闭路灯
     *
     * @return
     */
    @RequestMapping(value = "closeLamp")
    public RestMessage closeLamp(@RequestBody JSONObject param) {
        String lampId = param.getString("lampId");
        return strongService.closeLamp(lampId);
    }

    /**
     * 根据路灯分组id开启路灯
     *
     * @return
     */
    @RequestMapping(value = "openLampGroup")
    public RestMessage openLampGroup(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        return strongService.openLampGroup(groupId);
    }

    /**
     * 根据路灯分组id关闭路灯
     *
     * @return
     */
    @RequestMapping(value = "closeLampGroup")
    public RestMessage closeLampGroup(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        return strongService.closeLampGroup(groupId);
    }

    /**
     * 根据远程开关id开启远程开关
     *
     * @return
     */
    @RequestMapping(value = "openRemote")
    public RestMessage openRemote(@RequestBody JSONObject param) {
        String groupId = param.getString("groupId");
        String addressId = param.getString("addressId");
        return strongService.openRemote(groupId, addressId);
    }

    /**
     * 根据远程开关id关闭远程开关
     *
     * @return
     */
    @RequestMapping(value = "closeRemote")
    public RestMessage closeRemote(@RequestBody JSONObject param) {
        String remoteId = param.getString("remoteId");
        String addressId = param.getString("addressId");
        return strongService.closeRemote(remoteId, addressId);
    }

    /**
     * 设置定时开关时间
     *
     * @param
     * @return
     */
    @RequestMapping(value = "setReservation")
    public RestMessage setReservation(@RequestBody JSONObject param) {
        String remoteId = param.getString("remoteId");
        String addressId = param.getString("addressId");
        String openTime = param.getString("openTime");
        String closeTime = param.getString("closeTime");
        return strongService.setReservation(remoteId, addressId, openTime, closeTime);
    }

    /**
     * 获取定时开关时间
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "getReservation")
    public RestMessage getReservation(@RequestBody JSONObject param) {
        String remoteId = param.getString("remoteId");
        return strongService.getReservation(remoteId);
    }

    /**
     * 设置工作模式
     *
     * @return
     */
    @RequestMapping(value = "setWorkingMode")
    public RestMessage setWorkingMode(@RequestBody JSONObject param) {
        String remoteId = param.getString("remoteId");
        String mode = param.getString("mode");
        return strongService.setWorkingMode(remoteId, mode);
    }

    /**
     * 获取工作模式
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "getWorkingMode")
    public RestMessage getWorkingMode(@RequestBody JSONObject param) {
        String remoteId = param.getString("remoteId");
        return strongService.getWorkingMode(remoteId);
    }

    /**
     * 获取工作电压
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "getVC")
    public RestMessage getVC(@RequestBody JSONObject param) {
        String deviceId = param.getString("deviceId");
        return strongService.getVC(deviceId);
    }

    /**
     * 获取水位信息
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "getWaterLevel")
    public RestMessage getWaterLevel(@RequestBody JSONObject param) {
        String deviceId = param.getString("deviceId");
        return strongService.getWaterLevel(deviceId);
    }
}
