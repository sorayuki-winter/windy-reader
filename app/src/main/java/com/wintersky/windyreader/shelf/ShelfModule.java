package com.wintersky.windyreader.shelf;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ShelfModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ShelfFragment shelfFragment();

    @ActivityScoped
    @Binds
    abstract ShelfContract.Presenter shelfPresenter(ShelfPresenter presenter);
}
