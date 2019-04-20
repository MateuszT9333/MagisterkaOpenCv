package pl.polsl.aei.mateusz.agerecognizer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private Properties properties;
    private static final Logger log = LogManager.getLogger("main");

    public PropertiesLoader() {
        properties = new Properties();
        String propFileName = "config.properties";

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            log.catching(e);
        }

        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                log.catching(e);
            }
        } else {
            try {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            } catch (FileNotFoundException e) {
                log.catching(e);
                ;
            }
        }
    }

    public String getProperty(String property) {
        // get the property value and print it out
        return properties.getProperty(property);
    }
}
