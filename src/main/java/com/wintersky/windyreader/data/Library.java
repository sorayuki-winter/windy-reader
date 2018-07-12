package com.wintersky.windyreader.data;

import io.realm.RealmObject;

public class Library extends RealmObject {
    private String name;
    private String baseUrl;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
