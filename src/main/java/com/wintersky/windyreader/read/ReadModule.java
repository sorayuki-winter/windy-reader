package com.wintersky.windyreader.read;

import android.support.v4.app.FragmentManager;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wintersky.windyreader.read.ReadActivity.EXTRA_CHAPTER_URL;

@Module
public abstract class ReadModule {

    @ActivityScoped
    @Provides
    static FragmentManager provideFM(ReadActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ReadFragment readFragment();

    @ActivityScoped
    @Binds
    abstract ReadContract.Presenter readPresenter(ReadPresenter presenter);

    @ActivityScoped
    @Provides
    static String provideChapterUrl(ReadActivity activity) {
        return activity.getIntent().getStringExtra(EXTRA_CHAPTER_URL);
    }
}
