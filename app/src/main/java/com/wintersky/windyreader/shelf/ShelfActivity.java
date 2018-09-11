package com.wintersky.windyreader.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

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

    private boolean exist = false;
    private Toast mToast;

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
            if (exist) {
                mToast.cancel();
                super.onBackPressed();
            } else {
                exist = true;
                mToast = Toast.makeText(this, "Press to Exist", Toast.LENGTH_SHORT);
                mToast.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exist = false;
                        mToast = null;
                    }
                }, 2000);
            }
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
