package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.entity.camera.CameraDto;
import com.drore.cloud.tdp.entity.camera.CameraGroup;
import com.drore.cloud.tdp.exception.BusinessException;
import com.drore.cloud.tdp.service.ICommonServiceStub;
import com.drore.cloud.tdp.utils.QueryUtil;
import com.drore.cloud.tdp.utils.XmlUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.drore.cloud.tdp.utils.QueryUtil.checkRepeat;

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

    private String url = new String();//接口请求地址
    private Integer resType1 = -1;
    private Integer resType2 = -1;
    private String nodeIndexCode = new String();

    private void initMethod() {
        String factoryModelName = "hikVersion7600";
        JSONObject config = QueryUtil.queryConfigByFactoryModelName(factoryModelName);
        try {
            url = config.getString("url");
            resType1 = Integer.parseInt(config.getString("resType1"));
            resType2 = Integer.parseInt(config.getString("resType2"));
            nodeIndexCode = config.getString("nodeIndexCode");
        } catch (Exception e) {
            throw new BusinessException("获取监控配置参数有误,error=", e);
        }
    }

    public void syncCamera() {
        initMethod();
        Map<String, Object> cameraGroupAndDeviceDTO = getCameraGroupAndDeviceDTO(url, nodeIndexCode, resType1, resType2);
        List addCameraGroup = (List) cameraGroupAndDeviceDTO.get("addCameraGroup");
        List addDeviceDTO = (List) cameraGroupAndDeviceDTO.get("addDeviceDTO");
        List updateCameraGroup = (List) cameraGroupAndDeviceDTO.get("updateCameraGroup");
        List updateDeviceDto = (List) cameraGroupAndDeviceDTO.get("updateDeviceDto");
        try {
            RestMessage restMessage = runner.insertBatch("cameragroup", JSON.toJSON(addCameraGroup));
            if (null != restMessage) {
                LOGGER.info("监控列表数据新增成功,共新增：" + addCameraGroup.size() + "条数据;");
            } else {
                LOGGER.info("监控列表数据新增失败;");
            }
            restMessage = runner.insertBatch("device_dto", addDeviceDTO);
            if (null != restMessage) {
                LOGGER.info("监控点数据新增成功,共新增：" + addDeviceDTO.size() + "条数据;");
            } else {
                LOGGER.info("监控点数据新增失败;");
            }
            restMessage = runner.updateBatch("cameragroup", updateCameraGroup);
            if (null != restMessage) {
                LOGGER.info("监控列表数据更新成功,共更新：" + updateCameraGroup.size() + "条数据;");
            } else {
                LOGGER.info("监控列表数据更新失败;");
            }
            restMessage = runner.updateBatch("device_dto", updateDeviceDto);
            if (null != restMessage) {
                LOGGER.info("监控点数据更新成功,共更新：" + updateDeviceDto.size() + "条数据;");
            } else {
                LOGGER.info("监控点数据更新失败;");
            }
        } catch (Exception e) {
            LOGGER.error("监控数据新增|更新异常,error=", e);
        }
    }

    public Map<String, Object> getCameraGroupAndDeviceDTO(String url, String nodeIndexCode, Integer resType1, Integer resType2) {
        List<CameraGroup> addCameraGroupList = new ArrayList<>();
        List<CameraDto> addDeviceDtoList = new ArrayList<>();
        List<CameraGroup> updateCameraGroupList = new ArrayList<>();
        List<CameraDto> updateDeviceDtoList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        try {
            ICommonServiceStub is = new ICommonServiceStub(url);
            ICommonServiceStub.GetAllResourceDetail detail = new ICommonServiceStub.GetAllResourceDetail();//获取全部组织资源
            detail.setResType(resType1);//资源类型： 1000资源组织;3000用户组织;4000应用;30000编码设备;110000解码设备;100000视频综合平台;50000监视屏组;
            detail.setNodeIndexCode(nodeIndexCode);//服务的INDEXCODE（预留参数，调用方的indexCode，可填任意String类型值）
            ICommonServiceStub.GetAllResourceDetailResponse dt = is.getAllResourceDetail(detail);
            String responseResult = dt.get_return();
            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(XmlUtil.xml2json(responseResult)));
            //System.out.println("jsonObject = " + jsonObject);
            JSONObject table = jsonObject.getJSONObject("table");
            Object object1 = table.get("rows");
            if (object1 != null && !"".equals(object1)) {
                JSONObject rows = (JSONObject) JSONObject.toJSON(object1);
                JSONArray jsonArray = rows.getJSONArray("row");
                for (int i = 0; i < jsonArray.size(); i++) {
                    CameraGroup cameraGroup = new CameraGroup();
                    cameraGroup.setCameraListName(jsonArray.getJSONObject(i).get("c_org_name").toString());
                    cameraGroup.setCameraListNo(jsonArray.getJSONObject(i).get("i_id").toString());
                    String value = String.valueOf(jsonArray.getJSONObject(i).get("i_id"));
                    String id = QueryUtil.checkRepeat("cameragroup", "region_id", value);
                    if (StringUtils.isEmpty(id)) {
                        addCameraGroupList.add(cameraGroup);
                    } else {
                        cameraGroup.setId(id);
                        updateCameraGroupList.add(cameraGroup);
                    }
                    ICommonServiceStub.GetAllResourceDetailByOrg gr = new ICommonServiceStub.GetAllResourceDetailByOrg();
                    String orgCode = String.valueOf(jsonArray.getJSONObject(i).get("c_index_code"));
                    gr.setOrgCode(orgCode);
                    gr.setResType(resType2);
                    gr.setNodeIndexCode(nodeIndexCode);
                    ICommonServiceStub.GetAllResourceDetailByOrgResponse rt = is.getAllResourceDetailByOrg(gr);//获取指定组织下全部资源
                    String responseResult2 = rt.get_return();
                    JSONObject jsonObject2 = JSONObject.parseObject(String.valueOf(XmlUtil.xml2json(responseResult2)));
                    //System.out.println("jsonObject2 = " + jsonObject2);
                    JSONObject table1 = jsonObject2.getJSONObject("table");
                    Object object = table1.get("rows");
                    if (object != null && !"".equals(object)) {
                        JSONObject rows1 = (JSONObject) JSONObject.toJSON(object);
                        JSONArray jsonArray2 = rows1.getJSONArray("row");
                        for (int j = 0; j < jsonArray2.size(); j++) {
                            CameraDto cameraDto = new CameraDto();
                            cameraDto.setUserName(String.valueOf(jsonArray2.getJSONObject(j).get("c_creator")));
                            cameraDto.setCameraName(String.valueOf(jsonArray2.getJSONObject(j).get("c_name")));
                            cameraDto.setDeviceId(String.valueOf(jsonArray2.getJSONObject(j).get("i_id")));
                            cameraDto.setChannelNo(String.valueOf(jsonArray2.getJSONObject(j).get("i_channel_no")));
                            cameraDto.setIpAddress(String.valueOf(jsonArray2.getJSONObject(j).get("c_device_ip")));
                            cameraDto.setNetworkPort(String.valueOf(jsonArray2.getJSONObject(j).get("i_device_port")));
                            cameraDto.setCameraListId(String.valueOf(jsonArray2.getJSONObject(j).get("i_org_id")));
                            cameraDto.setIndexCode(String.valueOf(jsonArray2.getJSONObject(j).get("c_index_code")));
                            String value2 = String.valueOf(jsonArray2.getJSONObject(j).get("c_index_code"));
                            String idd = checkRepeat("device_dto", "index_code", value2);//去重复
                            if (StringUtils.isEmpty(idd)) {
                                addDeviceDtoList.add(cameraDto);
                            } else {
                                cameraDto.setId(idd);
                                updateDeviceDtoList.add(cameraDto);
                            }
                        }
                    }
                }
            }
            map.put("addCameraGroup", addCameraGroupList);
            map.put("addDeviceDTO", addDeviceDtoList);
            map.put("updateCameraGroup", updateCameraGroupList);
            map.put("updateDeviceDto", updateDeviceDtoList);
        } catch (Exception e) {
            LOGGER.error("数据错误,error=", e);
        }
        return map;
    }
}
