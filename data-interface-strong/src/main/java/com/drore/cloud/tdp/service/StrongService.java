package com.drore.cloud.tdp.service;

import com.drore.cloud.tdp.entity.RestMessage;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/16  15:24.
 */
public interface StrongService {
    /**
     * 广播列表
     *
     * @return
     */
    RestMessage broadcastList();

    /**
     * 水位列表
     *
     * @return
     */
    RestMessage waterList();

    /**
     * 水闸列表
     *
     * @return
     */
    RestMessage gateList();

    /**
     * 路灯分组列表
     *
     * @return
     */
    RestMessage lampGroupList();

    /**
     * 路灯列表
     *
     * @return
     */
    RestMessage lampList(String groupId);

    /**
     * 远程开关列表
     *
     * @return
     */
    RestMessage remoteList();

    /**
     * 网关列表
     *
     * @return
     */
    RestMessage gateWayList();

    /**
     * 根据广播id开启广播
     *
     * @param broadcastId
     * @return
     */
    RestMessage openBroadcast(String broadcastId);

    /**
     * 根据广播id关闭广播
     *
     * @return
     */
    RestMessage closeBroadcast(String broadcastId);

    /**
     * 根据组id分组开启广播
     *
     * @return
     */
    RestMessage openBroadcastGroup(String groupId);

    /**
     * 根据组id分组关闭广播
     *
     * @return
     */
    RestMessage closeBroadcastGroup(String groupId);

    /**
     * 根据水闸id开启水闸
     *
     * @param gateId
     * @return
     */
    RestMessage openGate(String gateId);

    /**
     * 根据水闸id暂停水闸
     *
     * @param gateId
     * @return
     */
    RestMessage stopGate(String gateId);

    /**
     * 根据水闸id关闭水闸
     *
     * @param gateId
     * @return
     */
    RestMessage closeGate(String gateId);

    /**
     * 根据路灯id开启路灯
     *
     * @return
     */
    RestMessage openLamp(String lampId);

    /**
     * 根据路灯id关闭路灯
     *
     * @return
     */
    RestMessage closeLamp(String lampId);

    /**
     * 根据路灯分组id开启路灯
     *
     * @return
     */
    RestMessage openLampGroup(String groupId);

    /**
     * 根据路灯分组id关闭路灯
     *
     * @return
     */
    RestMessage closeLampGroup(String groupId);

    /**
     * 根据远程开关id开启远程开关
     *
     * @return
     */
    RestMessage openRemote(String remoteId, String addressId);

    /**
     * 根据远程开关id关闭远程开关
     *
     * @return
     */
    RestMessage closeRemote(String remoteId, String addressId);

    /**
     * 设置定时开关时间
     *
     * @param
     * @return
     */
    RestMessage setReservation(String remoteId, String addressId, String openTime, String closeTime);

    /**
     * 获取定时开关时间
     *
     * @param remoteId
     * @return
     */
    RestMessage getReservation(String remoteId);


    /**
     * 设置工作模式
     *
     * @return
     */
    RestMessage setWorkingMode(String remoteId, String mode);

    /**
     * 获取工作模式
     *
     * @param remoteId
     * @return
     */
    RestMessage getWorkingMode(String remoteId);

    /**
     * 获取工作电压
     *
     * @param vcId
     * @return
     */
    RestMessage getVC(String vcId);

    /**
     * 获取水位信息
     *
     * @param waterId
     * @return
     */
    RestMessage getWaterLevel(String waterId);

}
