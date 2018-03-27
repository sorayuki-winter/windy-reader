package com.wintersky.windyreader.catalog;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.R;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class CatalogActivity extends DaggerAppCompatActivity {

    @Inject
    FragmentManager fm;

    @Inject
    CatalogFragment catalogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        CatalogFragment fragment = (CatalogFragment) fm.findFragmentById(R.id.frame_catalog);

        if (fragment == null) {
            fragment = catalogFragment;
            fm.beginTransaction().replace(R.id.frame_catalog, fragment).commit();
        }
    }
}
