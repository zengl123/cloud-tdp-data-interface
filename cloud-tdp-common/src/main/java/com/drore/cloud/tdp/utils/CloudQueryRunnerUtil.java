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

    static String cloud_url = "y.drore.com";
    //static String cloud_url = "192.168.10.48";
    static Integer port = 80;
    //static String appId = "xxsdhyaaQpnrD4";
    //static String secret = "QKLC9qbO";
//    static String appId = "ccgkiP9UOUcn";
//    static String secret = "0OtuodpQ";
    static String appId = "fourLpSfZuBc";
    static String secret = "VmDGhgCc";

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
