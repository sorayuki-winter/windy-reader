package com.wintersky.windyreader.di;

import com.wintersky.windyreader.catalog.CatalogActivity;
import com.wintersky.windyreader.catalog.CatalogModule;
import com.wintersky.windyreader.detail.DetailActivity;
import com.wintersky.windyreader.detail.DetailModule;
import com.wintersky.windyreader.read.ReadActivity;
import com.wintersky.windyreader.read.ReadModule;
import com.wintersky.windyreader.search.SearchActivity;
import com.wintersky.windyreader.search.SearchModule;
import com.wintersky.windyreader.shelf.ShelfActivity;
import com.wintersky.windyreader.shelf.ShelfModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module ActivityBindingModule is on,
 * in our case that will be AppComponent. The beautiful part about this setup is that you never need to tell AppComponent that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that AppComponent exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the specified modules and be aware of a scope annotation @ActivityScoped
 * When Dagger.Android annotation processor runs it will create 4 subcomponents for us.
 */
@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = ShelfModule.class)
    abstract ShelfActivity shelfActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = SearchModule.class)
    abstract SearchActivity searchActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = DetailModule.class)
    abstract DetailActivity detailActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = ReadModule.class)
    abstract ReadActivity readActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = CatalogModule.class)
    abstract CatalogActivity catalogActivity();
}
