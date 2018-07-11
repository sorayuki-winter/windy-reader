package com.wintersky.windyreader.data;

import java.util.Date;
import java.util.List;

public class Book {
    public String url;
    public String title;
    public String author;
    public String detail;
    public String imgUrl;
    public String chapterListUrl;
    public byte[] img;
    public Date lastTime;
    public int currentCId;
    public String currentCUrl;
    public List<Chapter> chapterList;

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

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public long getCurrentCId() {
        return currentCId;
    }

    public void setCurrentCId(int currentCId) {
        this.currentCId = currentCId;
    }

    public String getCurrentCUrl() {
        return currentCUrl;
    }

    public void setCurrentCUrl(String currentCUrl) {
        this.currentCUrl = currentCUrl;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }
}
