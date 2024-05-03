package org.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    PropertiesLoader loader;
    private Properties properties;

    public DatabaseConnector() {
        this.loader = new PropertiesLoader("database.properties");
        this.properties = loader.getProperties();
    }

    public Connection connect() {
        try {
            String url = properties.getProperty("database.url");
            String user = properties.getProperty("database.user");
            String password = properties.getProperty("database.password");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
