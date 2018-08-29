package com.wintersky.windyreader.data;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class Chapter extends RealmObject {
    @PrimaryKey
    private String url;
    private String catalogUrl;
    private String title;
    private int index;
    private boolean read;
    @Ignore
    private String content;

    public String getCatalogUrl() {
        return catalogUrl;
    }

    public void setCatalogUrl(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
