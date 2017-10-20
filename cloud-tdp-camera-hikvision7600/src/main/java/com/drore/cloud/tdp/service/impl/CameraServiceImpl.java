package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.entity.CameraDeviceThird;
import com.drore.cloud.tdp.entity.CameraGroupThird;
import com.drore.cloud.tdp.entity.camera.CameraDto;
import com.drore.cloud.tdp.entity.camera.CameraGroup;
import com.drore.cloud.tdp.exception.BusinessException;
import com.drore.cloud.tdp.service.ICommonServiceStub;
import com.drore.cloud.tdp.tables.camera.CameraTables;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private CloudQueryRunner runner;
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CameraTables cameraTables;

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
        if (null == url) {
            try {
                initMethod();
            } catch (Exception e) {
                LOGGER.error("获取监控配置参数异常,error=", e);
                return;
            }
        }
        syncCameraGroup();//同步监控列表
        syncCameraDevice();//同步监控点

    }

    /**
     * 同步监控列表
     */
    private void syncCameraGroup() {
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
        LOGGER.info("监控列表：" + objectResponse);
        JSONArray row;
        try {
            row = objectResponse.getJSONObject("table").getJSONObject("rows").getJSONArray("row");
        } catch (Exception e) {
            LOGGER.error("获取监控列表数据异常,error=", e);
            return;
        }
        List<CameraGroup> addCameraGroup = new ArrayList<>();
        List<CameraGroup> updateCameraGroup = new ArrayList<>();
        row.stream().forEach(object -> {
            CameraGroup cameraGroup = new CameraGroup();
            CameraGroupThird cameraGroupThird = JSON.toJavaObject((JSON) object, CameraGroupThird.class);
            Integer iid = cameraGroupThird.getIid();
            String id = queryUtil.checkRepeat(CameraTables.CAMERA_GROUP, "region_id", iid);
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
        });
        cameraTables.saveOrUpdateCameraGroup(addCameraGroup, updateCameraGroup);
    }

    /**
     * 同步监控点
     */

    private void syncCameraDevice() {
        List<CameraGroup> allCameraGroup = getAllCameraGroup();
        if (null == allCameraGroup) {
            return;
        }
        List<CameraDto> addCamera = new ArrayList<>();
        List<CameraDto> updateCamera = new ArrayList<>();
        allCameraGroup.stream().forEach(cameraGroup -> {
            String orgCode = cameraGroup.getIndexCode();
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
            String responseResult = rt.get_return();
            JSONObject objectResponse = XmlUtil.xml2json(responseResult);
            LOGGER.info("监控点：" + objectResponse);
            JSONArray row;
            try {
                row = objectResponse.getJSONObject("table").getJSONObject("rows").getJSONArray("row");
            } catch (Exception e) {
                LOGGER.error("获取监控点数据异常,error=", e);
                return;
            }
            row.stream().forEach(object -> {
                CameraDto cameraDto = new CameraDto();
                CameraDeviceThird cameraDtoThird = JSON.toJavaObject((JSON) object, CameraDeviceThird.class);
                Integer iid = cameraDtoThird.getIid();
                cameraDto.setDeviceId(iid);
                cameraDto.setCameraListId(cameraDtoThird.getOrgId());
                cameraDto.setIndexCode(cameraDtoThird.getIndexCode());
                cameraDto.setIpAddress(cameraDtoThird.getDeviceIp());
                cameraDto.setNetworkPort(cameraDtoThird.getDevicePort());
                cameraDto.setChannelNo(cameraDtoThird.getChannelNo());
                cameraDto.setCameraName(cameraDtoThird.getName());
                cameraDto.setUserName(cameraDtoThird.getCreator());
                String id = queryUtil.checkRepeat(CameraTables.CAMERA_DEVICE, "device_id", iid);
                if (null == id) {
                    addCamera.add(cameraDto);
                } else {
                    cameraDto.setId(id);
                    cameraDto.setModifiedTime(DateTimeUtil.getNowTime());
                    updateCamera.add(cameraDto);
                }
            });
        });
        cameraTables.saveOrUpdateCameraDevice(addCamera, updateCamera);
    }

    /**
     * 获取所有监控列表
     */
    private List<CameraGroup> getAllCameraGroup() {
        List<CameraGroup> cameraGroups = null;
        try {
            Pagination<CameraGroup> cameraGroupPagination = runner.queryListByExample(CameraGroup.class, CameraTables.CAMERA_GROUP, 1, Integer.MAX_VALUE);
            if (cameraGroupPagination.getCount() > 0) {
                cameraGroups = cameraGroupPagination.getData();
            } else {
                LOGGER.info("监控列表数据不存在,cameraGroupPagination=" + cameraGroupPagination);
            }
        } catch (Exception e) {
            LOGGER.error("获取监控列表数据异常,error=", e);
        }
        return cameraGroups;
    }
}


