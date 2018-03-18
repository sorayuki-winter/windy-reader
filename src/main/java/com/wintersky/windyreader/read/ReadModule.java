package com.wintersky.windyreader.read;

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
    static String provideChapterUrl(ReadActivity activity) {
        return activity.getIntent().getStringExtra(EXTRA_CHAPTER_URL);
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ReadFragment readFragment();

    @ActivityScoped
    @Binds
    abstract ReadContract.Presenter readPresenter(ReadPresenter presenter);
}
