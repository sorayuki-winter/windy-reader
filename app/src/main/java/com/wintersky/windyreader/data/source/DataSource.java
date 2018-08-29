package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import java.util.List;

import io.realm.RealmResults;

public interface DataSource {

    void getBook(String url, GetBookCallback callback);

    void getCatalog(String url, GetCatalogCallback callback);

    void getContent(String url, GetContentCallback callback);

    interface Local {

        void getShelf(GetShelfCallback callback);

        void getChapter(String url, GetChapterCallback callback);

        void saveBook(Book book);

        void deleteBook(String url);

        void cacheChapter(Chapter chapter);
    }

    interface Remote {

    }

    interface Repository extends Local, Remote {

        void cacheBook(String url, CacheBookCallback callback);
    }

    interface GetShelfCallback {

        void onLoaded(RealmResults<Book> list);
    }

    interface GetBookCallback {

        void onLoaded(Book book);

        void onDataNotAvailable(Exception e);
    }

    interface GetCatalogCallback {

        void onLoaded(List<Chapter> list);

        void onDataNotAvailable(Exception e);
    }

    interface GetChapterCallback {

        void onLoaded(Chapter chapter);

        void onDataNotAvailable(Exception e);
    }

    interface CacheBookCallback {
        void onCached();
    }

    interface GetContentCallback {

        void onLoaded(String content);

        void onDataNotAvailable(Exception e);
    }
}
