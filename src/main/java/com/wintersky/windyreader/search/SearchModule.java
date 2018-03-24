package com.wintersky.windyreader.search;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SearchModule {

    @ActivityScoped
    @Provides
    static FragmentManager provideFM(SearchActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract SearchFragment searchFragment();

    @ActivityScoped
    @Binds
    abstract SearchContract.Presenter searchPresenter(SearchPresenter presenter);
}
