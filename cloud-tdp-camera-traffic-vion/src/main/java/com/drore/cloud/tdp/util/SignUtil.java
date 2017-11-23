package com.drore.cloud.tdp.util;

import com.drore.cloud.sdk.common.util.MD5Util;

import java.util.*;

/**
 * 浙江卓锐科技有限公司
 * Author: ZENLIN
 * Date: 2017/8/28
 * Description:
 * Project: cloud-tdp-data-interface
 */
public class SignUtil {

    public static String buildMySign(Map<String, Object> sArray, String appKey, String secret, String time) {
        String preStr = createLinkString(sArray);
        preStr = appKey + "&" + secret + "&" + time + preStr;
        String mySign = MD5Util.getMD5Str(preStr);
        if (mySign != null) {
            mySign = mySign.toUpperCase();
        }
        return mySign;
    }

    public static String buildMySign(String appKey, String secret, String time) {
        return buildMySign(new HashMap<>(), appKey, secret, time);
    }

    public static String createLinkString(Map<String, Object> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        String preStr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = String.valueOf(params.get(key));
            preStr = preStr + "&" + value;
        }
        return preStr;
    }
}
