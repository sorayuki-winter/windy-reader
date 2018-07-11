package com.wintersky.windyreader.search;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class SearchActivity extends DaggerAppCompatActivity {

    @Inject
    SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FragmentManager fm = getSupportFragmentManager();
        SearchFragment fragment = (SearchFragment) fm.findFragmentById(R.id.frame_search);

        if (fragment == null) {
            fragment = searchFragment;
            fm.beginTransaction().replace(R.id.frame_search, fragment).commit();
        }
    }
}
