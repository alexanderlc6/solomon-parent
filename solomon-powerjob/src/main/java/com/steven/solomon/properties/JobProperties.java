package com.steven.solomon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("powerjob.worker")
public class JobProperties {

    private String userName;

    private String password;

    private String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
