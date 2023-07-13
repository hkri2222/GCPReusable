package com.sample.Utils;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.MergeCombiner;
import org.apache.commons.configuration.tree.NodeCombiner;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileConfig {
    private static final Logger logger = LoggerFactory.getLogger(FileConfig.class);
    private static final transient String ENV_CONFIG_FILE = "envConfig.xml";
    private static transient String ROOT_KEY_STORYS = null;
    private static transient String ROOT_KEY_ENV = null;
    private static FileConfig instance = null;
    private XMLConfiguration configuration;
    private String envconfigurationFile = "envConfig.xml";
    private boolean isConfigLoaded = false;

    public static FileConfig getInstance() {
        ROOT_KEY_STORYS = System.getProperty("TestConfigName") != null && !System.getProperty("TestConfigName").isEmpty() ? "config[@type='testConfig' and @name='" + System.getProperty("TestConfigName") + "']." : "config[@type='testConfig' and @name='smoke'].";
        ROOT_KEY_ENV = System.getProperty("Exeenv") != null && !System.getProperty("Exeenv").isEmpty() ? "config[@type='env' and @name='" + System.getProperty("Exeenv") + "']." : "config[@type='env' and @name='" + System.getProperty("testenv") + "'].";

        if (instance == null) {
            instance = new FileConfig();
        }
        return instance;
    }

    private FileConfig() {
        this.loadDefaultConfig();
    }

    public List<String> getStringConfigValues(String key) {
        if (!this.isConfigLoaded) {
            return null;
        } else {
            List<String> ret = new ArrayList();
            Iterator var3 = this.configuration.getList((ROOT_KEY_STORYS + key).replace('.', '/')).iterator();

            Object o;
            while(var3.hasNext()) {
                o = var3.next();

                try {
                    ret.add((String)o);
                } catch (ClassCastException var7) {
                    logger.error("Bad data received " + o);
                }
            }

            var3 = this.configuration.getList((ROOT_KEY_ENV + key).replace('.', '/')).iterator();

            while(var3.hasNext()) {
                o = var3.next();

                try {
                    ret.add((String)o);
                } catch (ClassCastException var6) {
                    logger.error("Bad data received " + o);
                }
            }

            return ret;
        }
    }

    public String getStringConfigValue(String key) {
        String valueToReturn = System.getProperty(key);
        if (this.isConfigLoaded) {
            if (valueToReturn == null || valueToReturn.isEmpty()) {
                valueToReturn = this.configuration.getString((ROOT_KEY_ENV + key).replace('.', '/'));
            }

            if (valueToReturn == null || valueToReturn.isEmpty()) {
                valueToReturn = this.configuration.getString((ROOT_KEY_STORYS + key).replace('.', '/'));
            }
        }

        return valueToReturn;
    }

    public Integer getIntegerConfigValue(String key) {
        Integer valueToReturn = 0;
        if (!this.isConfigLoaded) {
            return null;
        } else {
            if (valueToReturn == null || valueToReturn.equals(0)) {
                valueToReturn = this.configuration.getInt((ROOT_KEY_ENV + key).replace('.', '/'));
            }

            if (valueToReturn == null || valueToReturn.equals(0)) {
                valueToReturn = this.configuration.getInt((ROOT_KEY_STORYS + key).replace('.', '/'));
            }

            return valueToReturn;
        }
    }

    private void loadConfig() throws ConfigurationException {
        try {
            NodeCombiner combiner = new MergeCombiner();
            CombinedConfiguration cc = new CombinedConfiguration(combiner);
            cc.addConfiguration(new XMLConfiguration(this.envconfigurationFile));
            XMLConfiguration xmlconfig = new XMLConfiguration(cc);
            xmlconfig.setExpressionEngine(new XPathExpressionEngine());
            this.configuration = xmlconfig;
        } catch (ConfigurationException var4) {
            throw new ConfigurationException(var4);
        }
    }

    private void loadDefaultConfig() {
        String cf = System.getProperty(FileConfig.class.getSimpleName());
        if (cf != null && cf.length() > 0) {
        }

        try {
            this.loadConfig();
            this.isConfigLoaded = true;
        } catch (ConfigurationException var5) {
            try {
                this.loadConfig();
                this.isConfigLoaded = true;
            } catch (ConfigurationException var4) {
                logger.error("Unable to load configuration", var4);
            }
        }

    }

    public static enum FileType {
        DEFAULT,
        ENV,
        URL;

        private FileType() {
        }
    }
}
