package com.drore.cloud.tdp.constant;

import com.alibaba.fastjson.JSONObject;
import org.json.JSONArray;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/11  9:48.
 */
public class Constant {
    public static void main(String[] args) {
        JSONObject object = new JSONObject();
        object.put("name","龙舌嘴车站");
        object.put("am", "['9:00','9:30','10:00','10:30','11:00','11:30']");
        object.put("pm","['9:00','9:30','10:00','10:30','11:00','11:30']");
        JSONObject object1 = new JSONObject();
        object1.put("name","龙舌嘴车站");
        object1.put("am", "['9:00','9:30','10:00','10:30','11:00','11:30']");
        object1.put("pm","['9:00','9:30','10:00','10:30','11:00','11:30']");
        JSONArray j = new JSONArray();
        j.put(object);
        j.put(object1);
        System.out.println("j = " + j);
    }
}
