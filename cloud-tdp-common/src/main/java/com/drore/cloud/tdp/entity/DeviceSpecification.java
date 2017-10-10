package com.drore.cloud.tdp.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/30  13:25.
 */
@Data
public class DeviceSpecification {
    private String id;
    @SerializedName(value = "factory_model_name")
    private String factoryModelName;
    private String config;
}
