package com.wintersky.windyreader.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;
import com.wintersky.windyreader.data.source.BookCache;
import com.wintersky.windyreader.data.source.UpdateCheck;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ShelfActivity extends DaggerAppCompatActivity {

    public static final String BOOK_URL = "BOOK_URL";

    @Inject BookCache mBookCache;
    @Inject UpdateCheck mUpdateCheck;
    @Inject ShelfFragment shelfFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);

        if (fragment == null) {
            fragment = shelfFragment;
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            shelfFragment.onNewIntent(getIntent());
        }

        mBookCache.cache();
        mUpdateCheck.check();
    }

    @Override
    public void onBackPressed() {
        if (!shelfFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shelfFragment.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        mBookCache.close();
        mUpdateCheck.close();
        super.onDestroy();
    }
}
