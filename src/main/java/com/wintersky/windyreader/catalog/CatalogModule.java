package com.wintersky.windyreader.catalog;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class CatalogModule {

    @ActivityScoped
    @Provides
    static FragmentManager provideFM(CatalogActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract CatalogFragment catalogFragment();

    @ActivityScoped
    @Binds
    abstract CatalogContract.Presenter catalogPresenter(CatalogPresenter presenter);

    @ActivityScoped
    @ContributesAndroidInjector(modules = CatalogModule.class)
    abstract CatalogActivity catalogActivity();
}
