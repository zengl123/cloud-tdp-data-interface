package com.drore.cloud.tdp.activemq;

import com.hikvision.ivms6.cms.core.mq.component.HmqComponent;
import com.hikvision.ivms6.cms.core.mq.component.IMqComponent;
import com.hikvision.ivms6.cms.core.mq.component.SpringMqComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 浙江卓锐科技有限公司
 * Author: zhangz
 * Date:2017-08-22
 * Description:
 * Project:cloud-tdp-data-interface
 */

@Component
@PropertySource("classpath:config_mq.properties")
public class IMqComponentFactory   {
    private static Log log = LogFactory.getLog(IMqComponentFactory.class);
    @Value("${mqComponent_type}")
    private String mqComponentType;
    @Value("${broke_url}")
    private String brokeUrl;
    @Value("${remote_connection_factory}")
    private String jndiConnectFactoryName;
    private List<IMqComponent> mqComponents = new CopyOnWriteArrayList();

    public IMqComponentFactory() {
    }

    public IMqComponent createMqComponent(String destionName, boolean isTopic) throws JMSException {
        IMqComponent mqComponent = null;
        if(this.mqComponentType.equalsIgnoreCase("hmq")) {
            mqComponent = new HmqComponent(this.brokeUrl, destionName, isTopic);
        } else if("tonglinkq".equalsIgnoreCase(this.mqComponentType) || "activemq".equalsIgnoreCase(this.mqComponentType) || "weblogicjms".equalsIgnoreCase(this.mqComponentType)) {
            mqComponent = new SpringMqComponent(this.mqComponentType, this.brokeUrl, destionName, isTopic);
        }
        if(mqComponent != null) {
            this.mqComponents.add(mqComponent);
        }
        return (IMqComponent)mqComponent;
    }

    public String getMqComponentType() {
        return this.mqComponentType;
    }

    public void setMqComponentType(String mqComponentType) {
        this.mqComponentType = mqComponentType;
    }

    public String getBrokeUrl() {
        return this.brokeUrl;
    }

    public void setBrokeUrl(String brokeUrl) {
        if(StringUtils.isNotBlank(this.brokeUrl) && !this.brokeUrl.equals(brokeUrl)) {
            this.brokeUrl = brokeUrl;
            Iterator i$ = this.mqComponents.iterator();
            while(i$.hasNext()) {
                IMqComponent mqComponent = (IMqComponent)i$.next();
                try {
                    mqComponent.setBrokeUrl(brokeUrl);
                    mqComponent.restart();
                } catch (JMSException var5) {
                    log.error("MqComponentFactory setBrokeUrl stop JMSException:");
                }
            }
        } else {
            this.brokeUrl = brokeUrl;
        }
    }

    public String getJndiConnectFactoryName() {
        return this.jndiConnectFactoryName;
    }

    public void setJndiConnectFactoryName(String jndiConnectFactoryName) {
        this.jndiConnectFactoryName = jndiConnectFactoryName;
    }
}
