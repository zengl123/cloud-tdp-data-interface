package com.drore.cloud.tdp.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/9/30  11:23.
 */
public class ConfigUtil {
    public static PropertiesConfiguration getConfig() {
        PropertiesConfiguration pc = null;
        try {
            pc = new PropertiesConfiguration("application.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return pc;
    }
}
