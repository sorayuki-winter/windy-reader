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
    private String url;
    private String title;
    private String catalogUrl;
    private RealmList<Chapter> catalog;
    private float index;
    private Date lastRead;
    private boolean hasNew;

    private String author;
    private String detail;
    private String imgUrl;
}
