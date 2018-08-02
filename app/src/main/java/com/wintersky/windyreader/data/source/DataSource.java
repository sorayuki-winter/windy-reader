package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;
import com.wintersky.windyreader.data.Library;

import java.util.List;

import io.realm.RealmResults;

public interface DataSource {

    void getLList(GetLListCallback callback);

    void getShelf(GetShelfCallback callback);

    void getBook(String url, GetBookCallback callback);

    void getCatalog(String url, GetCatalogCallback callback);

    void getChapter(String url, GetChapterCallback callback);

    void saveBook(Book book);

    void updateCheck(String url, UpdateCheckCallback callback);

    interface GetLListCallback {

        void onLoaded(List<Library> list);

        void onDataNotAvailable();
    }

    interface GetShelfCallback {

        void onLoaded(RealmResults<Book> list);

        void onDataNotAvailable();
    }

    interface GetBookCallback {

        void onLoaded(Book book);

        void onDataNotAvailable();
    }

    interface GetCatalogCallback {

        void onLoaded(List<Chapter> list);

        void onDataNotAvailable();
    }

    interface UpdateCheckCallback {

        void onChecked();

        void onDataNotAvailable();
    }

    interface GetChapterCallback {

        void onLoaded(Chapter chapter);

        void onDataNotAvailable(Exception e);
    }
}
