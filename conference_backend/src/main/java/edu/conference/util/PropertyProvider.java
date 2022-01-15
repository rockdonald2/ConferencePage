package edu.conference.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.conference.util.exception.InvalidPropertyException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertyProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyProvider.class);

    private static final Properties properties;

    static {
        properties = new Properties();
        try (InputStream is = PropertyProvider.class.getResourceAsStream("/conference.properties")) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            LOG.error("Failed to access resource.", e);
        }
    }

    public static String getStringProperty(String key) {
        return properties.getProperty(key);
    }

    public static int getIntProperty(String key) throws InvalidPropertyException {
        int ret;

        try {
            ret = Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            LOG.error("Invalid property value for key {}.", key, e);
            throw new InvalidPropertyException("Invalid property type.");
        }

        return ret;
    }

    public static boolean getBoolProperty(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

}
