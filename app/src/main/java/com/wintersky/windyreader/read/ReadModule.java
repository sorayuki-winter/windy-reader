package com.wintersky.windyreader.read;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wintersky.windyreader.shelf.ShelfActivity.BOOK_URL;

@Module
public abstract class ReadModule {

    @ActivityScoped
    @Provides
    static FragmentManager sFragmentManager(ReadActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @ActivityScoped
    @Provides
    static String provideUrl(ReadActivity activity) {
        return activity.getIntent().getStringExtra(BOOK_URL);
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ReadFragment readFragment();

    @ActivityScoped
    @Binds
    abstract ReadContract.Presenter readPresenter(ReadPresenter presenter);
}
