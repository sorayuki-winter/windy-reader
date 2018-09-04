package com.wintersky.windyreader.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chapter extends RealmObject {
    @PrimaryKey
    public String url;
    public String catalogUrl;
    public String title;
    public int index;
    public boolean read;
}
