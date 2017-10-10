package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.tdp.utils.QueryUtil;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/29  16:50.
 */
public class ExampleServiceImpl {
    public static void main(String[] args) {

        JSONObject object = QueryUtil.queryConfigByFactoryModelName("");
        System.out.println("object = " + object);
        String s = QueryUtil.checkRepeat("device_specification", "factory_model_name", "s");
        System.out.println("s = " + s);
    }
}
