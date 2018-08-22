package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import io.realm.RealmResults;

public interface DataSource {

    void getShelf(GetShelfCallback callback);

    void getBook(String url, GetBookCallback callback);

    void getCatalog(String url, GetCatalogCallback callback);

    void getChapter(String url, GetChapterCallback callback);

    void saveBook(Book book);

    void updateCheck(String url, UpdateCheckCallback callback);

    interface GetShelfCallback {

        void onLoaded(RealmResults<Book> list);

        void onDataNotAvailable(Exception e);
    }

    interface GetBookCallback {

        void onLoaded(Book book);

        void onDataNotAvailable(Exception e);
    }

    interface GetCatalogCallback {

        void onLoaded(List<Chapter> list);

        void onDataNotAvailable(Exception e);
    }

    interface UpdateCheckCallback {

        void onChecked();

        void onDataNotAvailable(Exception e);
    }

    interface GetChapterCallback {

        void onLoaded(Chapter chapter);

        void onDataNotAvailable(Exception e);
    }
}
