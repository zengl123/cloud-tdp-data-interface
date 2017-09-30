package com.drore.cloud.tdp.utils;

import com.drore.cloud.sdk.basic.CloudBasicConnection;
import com.drore.cloud.sdk.basic.CloudBasicDataSource;
import com.drore.cloud.sdk.basic.CloudPoolingConnectionManager;
import com.drore.cloud.sdk.client.CloudQueryRunner;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/30  11:22.
 */
public class CloudQueryRunnerUtil {

    static String cloud_url = ConfigUtil.getConfig().getString("cloud_url");
    static Integer port = Integer.valueOf(ConfigUtil.getConfig().getString("cloud_port"));
    static String appId = ConfigUtil.getConfig().getString("appId");
    static String secret = ConfigUtil.getConfig().getString("secret");

    public static CloudQueryRunner getCloudQueryRunner() {
        CloudPoolingConnectionManager cloudPoolingConnectionManager = new CloudPoolingConnectionManager();
        cloudPoolingConnectionManager.setConnection(new CloudBasicConnection(cloud_url, port, appId, secret));
        CloudBasicDataSource cloudBasicDataSource = new CloudBasicDataSource();
        cloudBasicDataSource.setCloudPoolingConnectionManager(cloudPoolingConnectionManager);
        CloudQueryRunner cloudQueryRunner = new CloudQueryRunner();
        cloudQueryRunner.setDataSource(cloudBasicDataSource);
        return cloudQueryRunner;
    }
}
