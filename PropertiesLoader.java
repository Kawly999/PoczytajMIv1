package org.example.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {
    private Properties properties;

    PropertiesLoader(String propertiesFileName) {
        properties = new Properties();
        loadProperties(propertiesFileName);
    }

    public void loadProperties(String propertiesFileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                throw new IOException("Cannot find " + propertiesFileName);
            }
            properties.load(input);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public Properties getProperties() {return properties;}
}
