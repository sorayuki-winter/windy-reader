package com.wintersky.windyreader.shelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ShelfActivity extends DaggerAppCompatActivity {

    public static final String BOOK_URL = "BOOK_URL";
    public static final String BOOK_TIT = "BOOK_TIT";

    @Inject
    ShelfFragment shelfFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame_shelf);

        if (fragment == null) {
            fragment = shelfFragment;
            fm.beginTransaction().replace(R.id.frame_shelf, fragment).commit();
        }
    }
}
