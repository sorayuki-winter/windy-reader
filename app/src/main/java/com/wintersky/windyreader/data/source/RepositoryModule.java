package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository}.
 */
@Module
abstract public class RepositoryModule {

    @Singleton
    @Provides
    static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Singleton
    @Provides
    static AppExecutors provideAppExecutors() {
        return new AppExecutors(Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
                new AppExecutors.MainThreadExecutor());
    }

    @Singleton
    @Binds
    @Local
    abstract DataSource provideBooksLocalDataSource(LocalDataSource dataSource);

    @Singleton
    @Binds
    @Remote
    abstract DataSource provideBooksRemoteDataSource(RemoteDataSource dataSource);
}
