package com.erplus.sync.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

@Slf4j
public class PropertiesSingleton {

    private static volatile Properties properties;

    public static Properties getProperties() {
        if (properties == null) {
            synchronized (PropertiesSingleton.class) {
                if (properties == null) {
                    URL url = JschSessionUtils.class.getClassLoader().getResource("variable.properties");
                    properties = new Properties();
                    Validate.notNull(url, "variable.properties not exist");
                    try {
                        properties.load(Files.newInputStream(Paths.get(url.getPath())));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    return properties;
                }
            }
        }
        return properties;
    }
}
