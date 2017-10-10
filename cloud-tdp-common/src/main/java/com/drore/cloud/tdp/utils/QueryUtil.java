package com.drore.cloud.tdp.utils;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.DeviceSpecification;
import com.drore.cloud.tdp.exception.BusinessException;

import java.util.Map;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/30  12:56.
 */
public class QueryUtil {

    private static CloudQueryRunner runner = CloudQueryRunnerUtil.getCloudQueryRunner();

    public static JSONObject queryConfigByFactoryModelName(String factoryModelName) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select config ")
                .append("from device_specification ")
                .append("where factory_model_name='").append(factoryModelName).append("' ")
                .append("and is_deleted='N'");
        Pagination<DeviceSpecification> pagination;
        JSONObject object;
        try {
            pagination = runner.sql(DeviceSpecification.class, buffer.toString(), 1, 1);
            String config = pagination.getData().get(0).getConfig();
            object = JSONObject.parseObject(config);
        } catch (Exception e) {
            throw new BusinessException("获取配置参数config异常,error=", e);
        }
        return object;
    }

    public static String checkRepeat(String tableName, String filed, Object object) {
        RequestExample example = new RequestExample(1, 1);
        RequestExample.Criteria cri = example.create();
        RequestExample.Param param = example.createParam();
        param.addTerm("is_deleted", "N");//匹配查询
        param.addTerm(filed, object);
        cri.getMust().add(param);
        Pagination<Map> mapPagination= runner.queryListByExample(tableName, example, new String[]{"id"});
        String id;
        if (mapPagination.getSuccess()) {
            if (mapPagination != null && mapPagination.getData().size() > 0) {
                id = mapPagination.getData().get(0).get("id").toString();
            } else {
                id = null;
            }
        } else {
            throw new BusinessException("重复校验异常error="+mapPagination.getMessage());
        }
        return id;
    }
}
