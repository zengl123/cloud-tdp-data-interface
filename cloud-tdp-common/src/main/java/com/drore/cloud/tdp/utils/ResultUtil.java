package com.drore.cloud.tdp.utils;


import com.drore.cloud.tdp.entity.RestMessage;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/11/16  16:16.
 */
@Component
public class ResultUtil {


    public RestMessage success(Object data, String message) {
        RestMessage restMessage = new RestMessage();
        restMessage.setCode(8200);
        restMessage.setData(data);
        restMessage.setMessage(message);
        return restMessage;
    }

    public RestMessage error(String message) {
        RestMessage restMessage = new RestMessage();
        restMessage.setSuccess(false);
        restMessage.setCode(8500);
        restMessage.setMessage(message);
        return restMessage;
    }
}
