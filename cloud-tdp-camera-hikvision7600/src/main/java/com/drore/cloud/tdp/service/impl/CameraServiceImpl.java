package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.tdp.common.camera.CameraCommon;
import com.drore.cloud.tdp.entity.CameraDeviceThird;
import com.drore.cloud.tdp.entity.CameraGroupThird;
import com.drore.cloud.tdp.entity.camera.CameraDto;
import com.drore.cloud.tdp.entity.camera.CameraGroup;
import com.drore.cloud.tdp.service.ICommonServiceStub;
import com.drore.cloud.tdp.tables.camera.CameraTables;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:海康监控7600数据同步
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/25  14:45.
 */
@Component
public class CameraServiceImpl {

    private static Logger LOGGER = LoggerFactory.getLogger(CameraServiceImpl.class);

    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CameraCommon cameraCommon;

    private String url;//接口请求地址
    private Integer resType1;
    private Integer resType2;
    private String nodeIndexCode;

    private void initMethod() {
        String factoryModelName = "hikVersion7600";
        JSONObject config = queryUtil.queryConfigByFactoryModelName(factoryModelName);
        url = config.getString("url");
        resType1 = Integer.parseInt(config.getString("resType1"));
        resType2 = Integer.parseInt(config.getString("resType2"));
        nodeIndexCode = config.getString("nodeIndexCode");
    }

    public void syncCamera() {
        try {
            initMethod();
        } catch (Exception e) {
            LOGGER.error("获取监控配置参数异常,error=", e);
            return;
        }
        List<CameraDto> addCamera = new ArrayList<>();
        List<CameraDto> updateCamera = new ArrayList<>();
        List<CameraGroup> addCameraGroup = new ArrayList<>();
        List<CameraGroup> updateCameraGroup = new ArrayList<>();
        ICommonServiceStub.GetAllResourceDetailResponse dt;
        try {
            ICommonServiceStub is = new ICommonServiceStub(url);
            ICommonServiceStub.GetAllResourceDetail detail = new ICommonServiceStub.GetAllResourceDetail();//获取全部组织资源
            detail.setResType(resType1);//资源类型： 1000资源组织;3000用户组织;4000应用;30000编码设备;110000解码设备;100000视频综合平台;50000监视屏组;
            detail.setNodeIndexCode(nodeIndexCode);//服务的INDEXCODE（预留参数，调用方的indexCode，可填任意String类型值）
            dt = is.getAllResourceDetail(detail);
        } catch (RemoteException e) {
            LOGGER.error("getAllResourceDetail 接口请求异常,error=", e);
            return;
        }
        String responseResult = dt.get_return();
        JSONObject objectResponse = XmlUtil.xml2json(responseResult);
        JSONArray row;
        try {
            row = objectResponse.getJSONObject("table").getJSONObject("rows").getJSONArray("row");
        } catch (Exception e) {
            LOGGER.error("获取监控列表数据异常,error=", e);
            return;
        }
        row.stream().forEach(object -> {
            CameraGroupThird cameraGroupThird = JSON.toJavaObject((JSON) object, CameraGroupThird.class);
            String orgCode = cameraGroupThird.getIndexCode();
            ICommonServiceStub.GetAllResourceDetailByOrgResponse rt;//获取指定组织下全部资源
            try {
                ICommonServiceStub is = new ICommonServiceStub(url);
                ICommonServiceStub.GetAllResourceDetailByOrg gr = new ICommonServiceStub.GetAllResourceDetailByOrg();
                gr.setOrgCode(orgCode);
                gr.setResType(resType2);
                gr.setNodeIndexCode(nodeIndexCode);
                rt = is.getAllResourceDetailByOrg(gr);
            } catch (RemoteException e) {
                LOGGER.error("GetAllResourceDetailByOrg接口请求异常,error=", e);
                return;
            }
            String responseResult1 = rt.get_return();
            JSONObject objectResponse1 = XmlUtil.xml2json(responseResult1);
            JSONArray row1;
            try {
                row1 = objectResponse1.getJSONObject("table").getJSONObject("rows").getJSONArray("row");
            } catch (Exception e) {
                LOGGER.error("获取监控点数据异常,error=", e);
                return;
            }
            CameraGroup cameraGroup = new CameraGroup();
            Integer iid = cameraGroupThird.getIid();
            String id;
            try {
                id = queryUtil.checkRepeat(CameraTables.CAMERA_GROUP, "region_id", iid);
            } catch (Exception e) {
                LOGGER.error("checkRepeat() 异常,error=", e);
                return;
            }
            cameraGroup.setCameraListName(cameraGroupThird.getOrgName());
            cameraGroup.setCameraListNo(iid);
            cameraGroup.setIndexCode(cameraGroupThird.getIndexCode());
            if (null == id) {
                addCameraGroup.add(cameraGroup);
            } else {
                cameraGroup.setId(id);
                cameraGroup.setModifiedTime(DateTimeUtil.getNowTime());
                updateCameraGroup.add(cameraGroup);
            }
            row1.stream().forEach(object1 -> {
                CameraDto cameraDto = new CameraDto();
                CameraDeviceThird cameraDtoThird = JSON.toJavaObject((JSON) object1, CameraDeviceThird.class);
                Integer iid1 = cameraDtoThird.getIid();
                cameraDto.setDeviceId(iid1);
                cameraDto.setCameraListId(cameraDtoThird.getOrgId());
                cameraDto.setIndexCode(cameraDtoThird.getIndexCode());
                cameraDto.setIpAddress(cameraDtoThird.getDeviceIp());
                cameraDto.setNetworkPort(cameraDtoThird.getDevicePort());
                Integer channelNo = cameraDtoThird.getChannelNo();
                if (channelNo > 32) {
                    channelNo -= 32;
                }
                cameraDto.setChannelNo(channelNo);
                String name = cameraDtoThird.getName();
                cameraDto.setCameraName(name);
                if (name.split("_").length > 1) {
                    Integer sort = Integer.parseInt(name.split("_")[1]);
                    cameraDto.setSort(sort);
                }
                cameraDto.setUserName(cameraDtoThird.getCreator());
                String id1;
                try {
                    id1 = queryUtil.checkRepeat(CameraTables.CAMERA_DEVICE, "device_id", iid);
                } catch (Exception e) {
                    LOGGER.error("checkRepeat() 异常,error=", e);
                    return;
                }
                if (null == id1) {
                    addCamera.add(cameraDto);
                } else {
                    cameraDto.setId(id1);
                    cameraDto.setModifiedTime(DateTimeUtil.getNowTime());
                    updateCamera.add(cameraDto);
                }
            });
        });
        try {
            cameraCommon.saveOrUpdateCameraGroup(addCameraGroup, updateCameraGroup);
        } catch (Exception e) {
            LOGGER.error("监控7600列表新增|更新异常,error=", e);
        }
        try {
            cameraCommon.saveOrUpdateCameraDevice(addCamera, updateCamera);
        } catch (Exception e) {
            LOGGER.error("监控7600设备新增|更新异常,error=", e);
        }
    }
}


