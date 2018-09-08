package com.wintersky.windyreader.shelf;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ShelfModule {

    @ActivityScoped
    @Provides
    static FragmentManager provideFragmentManager(ShelfActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract DeleteFragment deleteFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ShelfFragment shelfFragment();

    @ActivityScoped
    @Binds
    abstract ShelfContract.Presenter shelfPresenter(ShelfPresenter presenter);
}
