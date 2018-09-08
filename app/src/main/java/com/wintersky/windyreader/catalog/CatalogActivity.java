package com.wintersky.windyreader.catalog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class CatalogActivity extends DaggerAppCompatActivity {

    @Inject CatalogFragment catalogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);

        if (fragment == null) {
            fragment = catalogFragment;
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
    }
}
