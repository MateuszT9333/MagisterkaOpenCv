package pl.polsl.aei.mateusz.agerecognizer.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private final Properties properties = new Properties();
    private static final Logger log = LogManager.getLogger("main");
    private static PropertiesLoader propertiesLoader = null;

    private PropertiesLoader() throws FileNotFoundException {
        InputStream inputStream = null;
        String propFileName = "config.properties";
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            log.error(String.format("Cannot find \"%s\" file", propFileName));
            log.catching(e);
        }

        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                log.error(String.format("Cannot load \"%s\" file", propFileName));
                log.catching(e);
            }
        } else {
            throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath");
        }
    }

    public static PropertiesLoader getInstance() {
        if (propertiesLoader == null) {
            try {
                propertiesLoader = new PropertiesLoader();
            } catch (FileNotFoundException e) {
                log.catching(e);
            }
        }
        return propertiesLoader;

    }

    public String getProperty(String property) {
        // get the property value and print it out
        return properties.getProperty(property);
    }
}
