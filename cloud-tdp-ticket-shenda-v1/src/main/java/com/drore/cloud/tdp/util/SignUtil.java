package com.drore.cloud.tdp.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 卓锐科技有限公司
 * Created by wmm on 2016/9/30.
 * email：6492178@gmail.com
 * decription:深大数据接口加密方法
 */
public class SignUtil {

    /**
     * 生成签名结果
     *
     * @param sArray 要签名的数组
     * @param appkey 通知密钥
     * @return 签名结果字符串
     */
    public static String buildMySign(Map<String, String> sArray, String appkey) {
        Map<String, String> result = paraFilter(sArray);
        String str = createLinkString(result, appkey); //把数组所有元素，按照“参数参数值”的模式拼接成字符串
        return str;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("appCode")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数参数值”的模式拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @param appKey
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params, String appKey) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            buffer.append(key).append("=").append(value);
            if (i != keys.size() - 1) {
                buffer.append("&");
            }
        }
        buffer.append(appKey);
        return DigestUtils.md5DigestAsHex((String.valueOf(buffer)).getBytes(Charset.forName("utf-8")));
    }
}
