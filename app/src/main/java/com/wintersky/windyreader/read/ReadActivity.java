package com.wintersky.windyreader.read;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class ReadActivity extends DaggerAppCompatActivity {

    public static final String CHAPTER_URL = "CHAPTER_URL";

    @Inject
    ReadFragment readFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame_read);

        if (fragment == null) {
            fragment = readFragment;
            fm.beginTransaction().replace(R.id.frame_read, fragment).commit();
        }
    }
}
