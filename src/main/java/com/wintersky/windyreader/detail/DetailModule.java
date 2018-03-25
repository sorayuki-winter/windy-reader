package com.wintersky.windyreader.detail;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class DetailModule {

    @ActivityScoped
    @Provides
    static FragmentManager provideFM(DetailActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DetailFragment detailFragment();

    @ActivityScoped
    @Binds
    abstract DetailContract.Presenter detailPresenter(DetailPresenter presenter);
}
