package com.wintersky.windyreader.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Book extends RealmObject {
    @PrimaryKey
    private String url;
    private String title;
    private String author;
    private String detail;
    private String imgUrl;
    private String catalogUrl;
    private RealmList<Chapter> catalog;
    private int index;

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

    public String getCatalogUrl() {
        return catalogUrl;
    }

    public void setCatalogUrl(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    public RealmList<Chapter> getCatalog() {
        return catalog;
    }

    public void setCatalog(RealmList<Chapter> catalog) {
        this.catalog = catalog;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Chapter getPrev() {
        if (index > 0) {
            return catalog.get(index - 1);
        }
        return null;
    }

    public Chapter getCurrent() {
        if (index >= 0 && index < catalog.size()) {
            return catalog.get(index);
        }
        return null;
    }

    public Chapter getNext() {
        if (index < catalog.size() - 1) {
            return catalog.get(index + 1);
        }
        return null;
    }
}
