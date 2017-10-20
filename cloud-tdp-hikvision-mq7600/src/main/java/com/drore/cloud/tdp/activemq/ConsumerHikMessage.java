package com.drore.cloud.tdp.activemq;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.tdp.entity.EventNotify;
import com.drore.cloud.tdp.service.CameraEventNotifyServiceImpl;
import com.drore.cloud.tdp.utils.XmlUtil;
import com.hikvision.ivms6.cms.core.mq.component.IMqComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;


/**
 * 浙江卓锐科技有限公司
 * Author: zhangz
 * Date:2017-08-18
 * Description:
 * Project:cloud-tdp-data-interface
 */

@Component
@PropertySource("classpath:config_mq.properties")
public class ConsumerHikMessage implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHikMessage.class);
    @Autowired
    private QueueMessageListener simpleMessageConverter;
    @Autowired
    private IMqComponentFactory mqComponentFactory;
    private IMqComponent mqComponent;
    @Value("${destination_name}")
    private String destinationName;//队列名称
    private boolean topicFlag = true;
    @Autowired
    private CameraEventNotifyServiceImpl cameraTraffic;

    /**
     * 启动监听器
     */
    public void start() {
        try {
            getMqComponent().start();
            //DefaultMessageListenerContainer d=new DefaultMessageListenerContainer();
            //d.setConnectionFactory();
            LOGGER.info("LogConsumer has start ");
        } catch (JMSException e) {
            LOGGER.error("启动监听器时出现异常", e);
        }
    }

    /**
     * 停止监听器
     */
    public void stop() {
        try {
            if (mqComponent != null) {
                mqComponent.stop();
            }
        } catch (Exception e) {
            LOGGER.error("停止监听器时出现异常", e);
        }
    }

    /**
     * 接受处理日志消息recvLogMsgrecvLogMsg
     * <bean id="eventReportProducer" class="com.hikvision.vmcc.hpp.producer.EventReportProducer" >
     * <property name="destionName" value="MyQueue" />
     * <property name="topicFlag" value="false" />
     * <property name="eventReportConvert" ref="eventReportConvert" />
     * <property name="mqComponentFactory" ref="mqComponentFactory" />
     * </bean>
     *
     * @param message
     */
    public void recvLogMsg(String message) {
        try {
            JSONObject object = XmlUtil.xml2json(message);
            LOGGER.info("解析后:" + object.toJSONString());
            EventNotify eventNotify = JSONObject.toJavaObject(object.getJSONObject("EventNotify"), EventNotify.class);
            if (eventNotify != null) {
                String eventType = eventNotify.getEventType();
                switch (eventType) {
                    case "327681": //边防报警
                        cameraTraffic.cameraBorder(eventNotify);
                        break;
                    case "131616"://监控客流
                        cameraTraffic.cameraTraffic(eventNotify);
                        break;
                    default:
                        break;
                }
            } else {
                LOGGER.info("EventNotify is null");
            }
        } catch (Exception e) {
            LOGGER.error("处理上报的日志信息时出现异常", e);
        }
    }

    public IMqComponent getMqComponent() throws JMSException {
        if (mqComponent == null) {
            mqComponent = mqComponentFactory.createMqComponent(destinationName, topicFlag);
            mqComponent.setBizListener(this);
            //QueueMessageListener simpleMessageConverter=new QueueMessageListener();
            mqComponent.setMsgConverter(simpleMessageConverter);
            mqComponent.setMethodName("recvLogMsg");
        }
        return mqComponent;
    }

    public void setMqComponent(IMqComponent mqComponent) {
        this.mqComponent = mqComponent;
    }

    @Override
    public void run(String... strings) throws Exception {
        getMqComponent().start();
        System.out.println("LogConsumer has start ");
    }
}
