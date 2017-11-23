package com.drore.cloud.tdp.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import com.drore.cloud.tdp.entity.RestMessage;
import lombok.Data;

/**
 * 说明: 封装对象结果的json结果
 * 创建日期: 2016年12月14日 下午3:20:05
 * 作者: xiangwb
 */
@Data
public class JSONObjResult {
    /**
     * 是否成功
     */
    private boolean isSuccess;

    /***
     * 新增、修改主鍵返回id
     */
    private String id;

    /**
     * 信息提示
     */
    private String message = "未知异常";
    /**
     * 返回数据
     */
    private Object data;

    private Integer code;

    public static JSONObject toJSONObj(Object o, boolean isSuccess, String errorMessage, Integer code) {
        JSONObjResult jsonObjResult = new JSONObjResult();
        jsonObjResult.setSuccess(isSuccess);
        jsonObjResult.setMessage(errorMessage);
        jsonObjResult.setCode(code);
        jsonObjResult.setData(JSONUtil.beanToMap(o));
        return JSON.parseObject(JSON.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }

    public static JSONObject toJSONObj(RestMessage rest) {
        JSONObjResult jsonObjResult = new JSONObjResult();
        jsonObjResult.setSuccess(rest.isSuccess());
        jsonObjResult.setMessage(rest.getMessage());
        jsonObjResult.setCode(rest.getCode());
        jsonObjResult.setData(JSONUtil.beanToMap(rest.getData()));
        return JSON.parseObject(JSON.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }

    /**
     * 设置错误信息 （默认isSuccess是false）
     *
     * @param error
     * @return
     */
    public static JSONObject toJSONObj(String error) {
        JSONObjResult jsonObjResult = new JSONObjResult();
        jsonObjResult.setSuccess(false);
        jsonObjResult.setCode(8500);
        jsonObjResult.setMessage(error);
        return JSON.parseObject(JSONObject.toJSONString(jsonObjResult, SerializerFeature.WriteMapNullValue));
    }
}
