package com.wintersky.windyreader.detail;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;

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

    @ActivityScoped
    @Provides
    static String provideBookUrl(DetailActivity activity) {
        return activity.getIntent().getStringExtra(BOOK_URL);
    }
}
