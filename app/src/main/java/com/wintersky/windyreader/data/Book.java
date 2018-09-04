package com.wintersky.windyreader.data;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book extends RealmObject {
    @PrimaryKey
    public String url;
    public String title;
    public String author;
    public String detail;
    public String imgUrl;
    public String catalogUrl;
    public RealmList<Chapter> catalog;
    public float index;
    public Date lastRead;
    public boolean hasNew;
}
