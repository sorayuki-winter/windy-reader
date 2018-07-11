package com.wintersky.windyreader.catalog;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;

@Module
public abstract class CatalogModule {

    @ActivityScoped
    @Provides
    static String providesUrl(CatalogActivity activity) {
        return activity.getIntent().getStringExtra(BOOK_URL);
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract CatalogFragment catalogFragment();

    @ActivityScoped
    @Binds
    abstract CatalogContract.Presenter catalogPresenter(CatalogPresenter presenter);
}
