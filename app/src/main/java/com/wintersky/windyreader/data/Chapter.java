package com.wintersky.windyreader.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chapter extends RealmObject {
    @PrimaryKey
    private String url;
    private String catalogUrl;
    private String title;
    private int index;
    private boolean read;
}
