package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.entity.CameraDtoThird;
import com.drore.cloud.tdp.entity.CameraGroupThird;
import com.drore.cloud.tdp.entity.camera.CameraDto;
import com.drore.cloud.tdp.entity.camera.CameraGroup;
import com.drore.cloud.tdp.service.ICommonServiceStub;
import com.drore.cloud.tdp.tables.camera.CameraTables;
import com.drore.cloud.tdp.utils.CloudQueryRunnerUtil;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.apache.axis2.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/17  14:04.
 */
@Component
public class CameraServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(CameraServiceImpl.class);

    //CloudQueryRunner runner = CloudQueryRunnerUtil.getCloudQueryRunner();
    @Autowired
    private CloudQueryRunner runner;
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CameraTables cameraTables;

    private static String url;//接口地址
    private static Integer resType1;//资源类型： 1000资源组织;3000用户组织;4000应用;30000编码设备;110000解码设备;100000视频综合平台;50000监视屏组;
    private static Integer resType2;//资源类型： 1000资源组织;3000用户组织;4000应用;30000编码设备;110000解码设备;100000视频综合平台;50000监视屏组;
    private static String nodeIndexCode;////服务的INDEXCODE（预留参数，调用方的indexCode，可填任意String类型值）
    private static Integer leave;//组织层级

    private void initMethod() {
        String factoryModelName = "hikvisionV9600";
        JSONObject config = queryUtil.queryConfigByFactoryModelName(factoryModelName);
        url = config.getString("url");
        resType1 = Integer.valueOf(config.getString("resType1"));
        resType2 = Integer.valueOf(config.getString("resType2"));
        nodeIndexCode = config.getString("nodeIndexCode");
        leave = config.getInteger("leave");
    }


    public void syncCamera() {
        if (null == url) {
            try {
                initMethod();
            } catch (Exception e) {
                LOGGER.error("获取海康9600配置参数异常,error=", e);
            }
        }
        syncCameraGroup();
        syncCameraDto();
    }

    private void syncCameraGroup() {
        ICommonServiceStub.GetAllResourceDetailResponse dt;
        try {
            ICommonServiceStub is = new ICommonServiceStub(url);
            ICommonServiceStub.GetAllResourceDetail detail = new ICommonServiceStub.GetAllResourceDetail();
            detail.setResType(resType1);
            detail.setNodeIndexCode(nodeIndexCode);
            dt = is.getAllResourceDetail(detail);
        } catch (RemoteException e) {
            LOGGER.error("请求监控列表接口异常,error=", e);
            return;
        }
        //获取监控区域信息表
        String aReturn = dt.get_return();
        JSONObject objectResponse = XmlUtil.xml2json(aReturn);
        LOGGER.info("监控列表数据：" + objectResponse);
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
            Integer leaves = cameraGroupThird.getLevel();
            if (!leave.equals(leaves)) {
                LOGGER.info("监控组织资源层级不正确,leave=" + leaves);
                return;
            }
            Integer id = cameraGroupThird.getId();
            cameraGroup.setCameraListNo(id);
            cameraGroup.setCameraListName(cameraGroupThird.getOrgName());
            cameraGroup.setIndexCode(cameraGroupThird.getIndexCode());
            try {
                String iid = queryUtil.checkRepeat(CameraTables.CAMERA_GROUP, "region_id", id);
                if (null == iid) {
                    addCameraGroup.add(cameraGroup);
                } else {
                    cameraGroup.setId(iid);
                    cameraGroup.setModifiedTime(DateTimeUtil.getNowTime());
                    updateCameraGroup.add(cameraGroup);
                }
            } catch (Exception e) {
                LOGGER.error("监控列表去重复异常,error=" + e);
                return;
            }
        });
        cameraTables.saveOrUpdateCameraGroup(addCameraGroup, updateCameraGroup);
    }

    private void syncCameraDto() {
        List<CameraGroup> allCameraGroup = getAllCameraGroup();
        if (null == allCameraGroup) {
            return;
        }
        List<CameraDto> addCamera = new ArrayList<>();
        List<CameraDto> updateCamera = new ArrayList<>();
        ICommonServiceStub.GetAllResourceDetailByOrg gr = new ICommonServiceStub.GetAllResourceDetailByOrg();

        allCameraGroup.stream().forEach(cameraGroup -> {
            ICommonServiceStub.GetAllResourceDetailByOrgResponse rt = null;
            try {
                ICommonServiceStub is = new ICommonServiceStub(url);
                String indexCode = cameraGroup.getIndexCode();
                gr.setOrgCode(indexCode);
                gr.setResType(resType2);
                gr.setNodeIndexCode(nodeIndexCode);
                rt = is.getAllResourceDetailByOrg(gr);
            } catch (RemoteException e) {
                LOGGER.error("获取监控点接口请求异常,error=", e);
            }
            String aReturn = rt.get_return();
            JSONObject objectResponse = XmlUtil.xml2json(aReturn);
            LOGGER.info("监控点数据：" + objectResponse);
            JSONArray row;
            try {
                row = objectResponse.getJSONObject("table").getJSONObject("rows").getJSONArray("row");
            } catch (Exception e) {
                LOGGER.error("获取监控点数据异常,error=", e);
                return;
            }
            row.stream().forEach(object -> {
                CameraDtoThird cameraDtoThird = JSON.toJavaObject((JSON) object, CameraDtoThird.class);
                CameraDto cameraDto = new CameraDto();
                Integer id = cameraDtoThird.getId();
                cameraDto.setDeviceId(id);
                cameraDto.setCameraListId(cameraDtoThird.getOrgId());
                cameraDto.setIndexCode(cameraDtoThird.getIndexCode());
                cameraDto.setIpAddress(cameraDtoThird.getDeviceIp());
                cameraDto.setNetworkPort(cameraDtoThird.getDevicePort());
                cameraDto.setChannelNo(cameraDtoThird.getChannelNo());
                cameraDto.setCameraName(cameraDtoThird.getName());
                cameraDto.setUserName(cameraDtoThird.getCreator());
                cameraDto.setPassword(cameraDtoThird.getPassword());
                try {
                    String iid = queryUtil.checkRepeat(CameraTables.CAMERA_DEVICE, "device_id", id);
                    if (null == iid) {
                        addCamera.add(cameraDto);
                    } else {
                        cameraDto.setId(iid);
                        cameraDto.setModifiedTime(DateTimeUtil.getNowTime());
                        updateCamera.add(cameraDto);
                    }
                } catch (Exception e) {
                    LOGGER.error("监控点去重复异常,error=", e);
                    return;
                }
            });
        });
        cameraTables.saveOrUpdateCameraDevice(addCamera, updateCamera);
    }

    private List<CameraGroup> getAllCameraGroup() {
        List<CameraGroup> cameraGroups = null;
        try {
            Pagination<CameraGroup> pagination = runner.queryListByExample(CameraGroup.class, CameraTables.CAMERA_GROUP, 1, Integer.MAX_VALUE);
            if (pagination != null && pagination.getCount() > 0) {
                cameraGroups = pagination.getData();
            } else {
                LOGGER.info("监控列表数据不存在,cameraGroupPagination=" + pagination);
            }
        } catch (Exception e) {
            LOGGER.error("获取所有监控列表异常;error=", e);
        }
        return cameraGroups;
    }
}
