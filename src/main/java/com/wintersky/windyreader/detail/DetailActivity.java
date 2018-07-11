package com.wintersky.windyreader.detail;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class DetailActivity extends DaggerAppCompatActivity {

    public static final String BOOK_URL = "BOOK_URL";

    @Inject
    DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        FragmentManager fm = getSupportFragmentManager();
        DetailFragment fragment = (DetailFragment) fm.findFragmentById(R.id.frame_detail);

        if (fragment == null) {
            fragment = detailFragment;
            fm.beginTransaction().replace(R.id.frame_detail, fragment).commit();
        }
    }
}
