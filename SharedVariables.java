package org.example.model;

public class SharedVariables {
    private static SharedVariables instance;
    private String email;
    private SharedVariables() {};

    public static SharedVariables getInstance() {
        if (instance == null) {
            instance = new SharedVariables();
        }
        return instance;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
