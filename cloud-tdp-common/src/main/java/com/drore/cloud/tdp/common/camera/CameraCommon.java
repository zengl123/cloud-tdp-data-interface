package com.drore.cloud.tdp.common.camera;

import com.alibaba.fastjson.JSON;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.entity.camera.CameraDto;
import com.drore.cloud.tdp.entity.camera.CameraGroup;
import com.drore.cloud.tdp.tables.camera.CameraTables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/25  16:52.
 */
@Component
public class CameraCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(CameraCommon.class);
    @Autowired
    private CloudQueryRunner runner;

    public List<CameraGroup> getAllCameraGroup() {
        List<CameraGroup> cameraGroups = null;
        try {
            Pagination<CameraGroup> pagination = runner.queryListByExample(CameraGroup.class, CameraTables.CAMERA_GROUP, 1, Integer.MAX_VALUE, new String[]{"id", "region_id", "name", "index_code"});
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

    public void saveOrUpdateCameraGroup(List<CameraGroup> addCameraGroup, List<CameraGroup> updateCameraGroup) {
        if (addCameraGroup.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CameraTables.CAMERA_GROUP, JSON.toJSON(addCameraGroup));
            if (insertBatch != null) {
                LOGGER.info("新增监控列表成功;共新增：" + addCameraGroup.size() + "条数据,addCameraGroup=" + JSON.toJSON(addCameraGroup));
            } else {
                LOGGER.info("新增监控列表失败;");
            }
        } else {
            LOGGER.info("没有新增监控列表;");
        }
        if (updateCameraGroup.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(CameraTables.CAMERA_GROUP, JSON.toJSON(updateCameraGroup));
            if (updateBatch != null) {
                LOGGER.info("监控列表数据更新成功,共更新：" + updateCameraGroup.size() + "条数据,updateCameraGroup=" + JSON.toJSON(updateCameraGroup));
            } else {
                LOGGER.info("监控列表数据更新失败;");
            }
        } else {
            LOGGER.info("没有监控列表的数据要更新;");
        }
    }

    public void saveOrUpdateCameraDevice(List<CameraDto> addCamera, List<CameraDto> updateCamera) {
        if (addCamera.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(CameraTables.CAMERA_DEVICE, JSON.toJSON(addCamera));
            if (null != insertBatch) {
                LOGGER.info("监控点新增成功,共新增：" + addCamera.size() + "条数据,info=" + JSON.toJSON(addCamera));
            } else {
                LOGGER.info("监控点新增失败;");
            }
        } else {
            LOGGER.info("没有新增监控点;");
        }
        if (updateCamera.size() > 0) {
            RestMessage updateBatch = runner.updateBatch(CameraTables.CAMERA_DEVICE, JSON.toJSON(updateCamera));
            if (null != updateBatch) {
                LOGGER.info("监控点信息更新成功,共更新：" + updateCamera.size() + "条数据,info=" + JSON.toJSON(updateCamera));
            } else {
                LOGGER.info("监控点信息更新失败;");
            }
        } else {
            LOGGER.info("没有监控点信息更新;");
        }
    }
}
