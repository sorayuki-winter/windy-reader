package com.wintersky.windyreader.di;

import com.wintersky.windyreader.data.source.local.Migration;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.wintersky.windyreader.data.source.local.Migration.REALM_VERSION;

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
        Component.set(appComponent);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(REALM_VERSION)
                .migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
