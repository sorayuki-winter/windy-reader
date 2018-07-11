package com.wintersky.windyreader.read;

import com.wintersky.windyreader.di.ActivityScoped;
import com.wintersky.windyreader.di.FragmentScoped;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

import static com.wintersky.windyreader.detail.DetailActivity.BOOK_URL;
import static com.wintersky.windyreader.read.ReadActivity.CHAPTER_URL;

@Module
public abstract class ReadModule {

    @ActivityScoped
    @Provides
    static String[] provideUrls(ReadActivity activity) {
        String[] strs = new String[2];
        strs[0] = activity.getIntent().getStringExtra(CHAPTER_URL);
        strs[1] = activity.getIntent().getStringExtra(BOOK_URL);
        return strs;
    }

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ReadFragment readFragment();

    @ActivityScoped
    @Binds
    abstract ReadContract.Presenter readPresenter(ReadPresenter presenter);
}
