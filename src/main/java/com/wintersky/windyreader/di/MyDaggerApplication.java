package com.wintersky.windyreader.di;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class MyDaggerApplication extends DaggerApplication {

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
        ComponentHolder.setAppComponent(appComponent);
    }
}
