package com.wintersky.windyreader.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wintersky.windyreader.R;

import dagger.android.support.DaggerAppCompatActivity;

public class SearchActivity extends DaggerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
