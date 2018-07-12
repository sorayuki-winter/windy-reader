package com.wintersky.windyreader.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Book extends RealmObject {
    @PrimaryKey
    private String url;
    private String title;
    private String author;
    private String detail;
    private String imgUrl;
    private String chapterListUrl;
    private String currentCUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getChapterListUrl() {
        return chapterListUrl;
    }

    public void setChapterListUrl(String chapterListUrl) {
        this.chapterListUrl = chapterListUrl;
    }

    public String getCurrentCUrl() {
        return currentCUrl;
    }

    public void setCurrentCUrl(String currentCUrl) {
        this.currentCUrl = currentCUrl;
    }
}
