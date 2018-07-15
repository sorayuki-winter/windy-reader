package com.wintersky.windyreader.data.source;

import com.wintersky.windyreader.data.source.local.LocalDataSource;
import com.wintersky.windyreader.data.source.remote.RemoteDataSource;
import com.wintersky.windyreader.util.AppExecutors;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link Repository}.
 */
@Module
abstract public class RepositoryModule {

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
