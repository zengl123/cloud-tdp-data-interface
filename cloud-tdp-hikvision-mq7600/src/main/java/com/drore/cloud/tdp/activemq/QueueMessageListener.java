package com.drore.cloud.tdp.activemq;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import java.io.UnsupportedEncodingException;

/**
 * 浙江卓锐科技有限公司
 * Author: zenlin
 * Date:2017-08-18
 * Description:
 * Project:cloud-tdp-data-interface
 */

@Component
public class QueueMessageListener extends SimpleMessageConverter {
    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        BytesMessage bytesMessage = (BytesMessage) message;
        byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
        bytesMessage.readBytes(bytes);
        String result = "";
        try {
            result = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
