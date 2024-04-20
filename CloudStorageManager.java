package org.example.model;

public class CloudStorageManager {
    PropertiesLoader loader;
    public CloudStorageManager() {
        loader = new PropertiesLoader("cloudStorage.properties");

    }
}
