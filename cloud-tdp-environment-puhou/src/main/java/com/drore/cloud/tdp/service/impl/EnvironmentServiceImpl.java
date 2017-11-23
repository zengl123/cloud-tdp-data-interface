package com.drore.cloud.tdp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.HttpClientUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.drore.cloud.sdk.common.util.MD5Util.getMD5Str;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/6  11:58.
 */
@Component
public class EnvironmentServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentServiceImpl.class);
    private static String url;
    private static String clientKey;
    private static String clientId;
    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CloudQueryRunner runner;

    private JSONObject initMethod() {
        String factoryModelName = "em_puhou";
        JSONObject object;
        try {
            object = queryUtil.queryConfigByFactoryModelName(factoryModelName);
            url = object.getString("url");
            clientKey = object.getString("clientKey");
            clientId = object.getString("clientId");
        } catch (Exception e) {
            object = null;
            LOGGER.error("获取环境监测数据url,clientKey,clientId异常,error=", e);
        }
        return object;
    }

    public void syncEnvironmentHistory() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
    }

    /**
     * 获取同步的开始时间
     *
     * @return
     */
    private Map<String, Object> getSyncTime() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("select max(coda_datetime) from environment_monitoring where is_deleted='N'");
        String beginTime;
        Map map = new HashMap();
        try {
            Pagination<Map> pagination = runner.sql(buffer.toString());
            if (pagination.getCount() > 0) {
                beginTime = pagination.getData().get(0).get("coda_datetime").toString();
                beginTime = DateTimeUtil.timeAddMinusMinutes(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);
            } else {
                beginTime = "2017-09-28 09:00:00";
            }
            String plusOneDay = DateTimeUtil.dateAddMinusDays(beginTime, 1, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);//最大时间间隔差(开始时间和结束时间间隔不能大于1天)
            String endTime = DateTimeUtil.timeAddMinusMinutes(DateTimeUtil.getNowTime(), -5, DateTimeUtil.YYYY_MM_DD_HH_MM_SS);//取当前时间前5分组时间
            if (plusOneDay.compareTo(endTime) < 0) {
                endTime = plusOneDay;
            }
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            return map;
        } catch (Exception e) {
            map = null;
            LOGGER.error("获取同步时间异常;error=", e);
        }
        return map;
    }

    /**
     * 非32位MD5加密
     *
     * @param str
     * @return
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException var5) {
            System.exit(-1);
        } catch (UnsupportedEncodingException var6) {
            var6.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; ++i) {
            md5StrBuff.append(Integer.toHexString(255 & byteArray[i]));
        }
        return md5StrBuff.toString();
    }


    /*public static void main(String[] args) {
        String url = "http://106.14.16.99:8080/web/interfaceuser/";
        String clientKey = "123456789";
        String clientId = "tongli";
        String begin1 = "2017-11-01 00:00:00";
        String end1 = "2017-11-02 00:00:00";
        Long begin = Long.parseLong(DateTimeUtil.dateStrToStamp(begin1));
        System.out.println("begin = " + begin);
        Long end =Long.parseLong(DateTimeUtil.dateStrToStamp(end1));
        Integer timestamp = Integer.valueOf(String.valueOf(new Date().getTime() / 1000));
        String encrypt = getMD5Str(clientKey + timestamp);
        String uri = url + "stationscaledata?encrypt=" + encrypt + "&clientId=" + clientId + "&timestamp=" + timestamp + "&start=" + begin + "&end=" + end;
        String result = HttpClientUtil.httpGet(uri);
        System.out.println("result = " + result);
    }*/
}
