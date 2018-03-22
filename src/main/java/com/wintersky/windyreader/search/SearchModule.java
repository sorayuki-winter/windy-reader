package com.wintersky.windyreader.search;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SearchModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract SearchFragment searchFragment();

    @ActivityScoped
    @Binds
    abstract SearchContract.Presenter searchPresenter(SearchPresenter presenter);
}
