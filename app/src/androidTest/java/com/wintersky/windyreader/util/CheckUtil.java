package com.wintersky.windyreader.util;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import static com.wintersky.windyreader.util.LogUtil.LOG;

public class CheckUtil {

    public static void checkShelf(List<Book> list) {
        StringBuilder sb = new StringBuilder();
        for (Book book : list) {
            sb.append(book.getTitle()).append(" ").append(book.getUrl()).append("\n");
        }
        LOG("shelf", sb.toString());
    }

    public static void checkBook(Book book) {
        LOG("book", book.getTitle() + " " + book.getUrl() + "\n" + book.getCatalogUrl());
    }

    public static void checkCatalog(List<Chapter> list) {
        StringBuilder sb = new StringBuilder();
        for (Chapter c : list) {
            sb.append(c.getIndex()).append(" ").append(c.getTitle()).append(" ").append(c.getUrl()).append("\n");
        }
        LOG("catalog", sb.toString());
    }
}
