package com.wintersky.windyreader.data;

public class Chapter {
    public String title;
    public String url;
    public String content;

    public Chapter() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        String re = "";
        re += url == null ? "url" : url + " ";
        re += title == null ? "title" : title + " ";
        return re;
    }
}
