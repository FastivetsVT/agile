package com.agileengine.fastivets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class PropertyReaderUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReaderUtil.class);
    private static final String PROPERTIES_FILE = "application.properties";

    private static Properties applicationProperties;

    public static String getProperty(String key) {
        if (Objects.isNull(applicationProperties)) {
            load();
        }
        return applicationProperties.getProperty(key);
    }

    private static void load() {
        try {
            applicationProperties = new Properties();
            applicationProperties.load(PropertyReaderUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
