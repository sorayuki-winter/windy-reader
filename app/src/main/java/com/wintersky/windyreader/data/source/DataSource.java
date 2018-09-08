package com.wintersky.windyreader.data.source;

import android.support.annotation.NonNull;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.Chapter;

import io.realm.RealmList;
import io.realm.RealmResults;

public interface DataSource {

    void getShelf(@NonNull GetShelfCallback callback);

    void getBook(@NonNull String url, @NonNull GetBookCallback callback);

    void getCatalog(@NonNull String url, @NonNull GetCatalogCallback callback);

    void getContent(@NonNull String url, @NonNull GetContentCallback callback);

    void saveBook(@NonNull Book book, @NonNull SaveBookCallback callback);

    void deleteBook(@NonNull String url);

    interface GetShelfCallback {

        void onLoaded(@NonNull RealmResults<Book> list);
    }

    interface GetBookCallback {

        void onLoaded(@NonNull Book book);

        void onFailed(@NonNull Exception e);
    }

    interface GetCatalogCallback {

        void onLoaded(@NonNull RealmList<Chapter> list);

        void onFailed(@NonNull Exception e);
    }

    interface GetContentCallback {

        void onLoaded(@NonNull String content);

        void onFailed(@NonNull Exception e);
    }

    interface SaveBookCallback {

        void onSaved(@NonNull Book book);

        void onFailed(@NonNull Exception e);
    }
}
