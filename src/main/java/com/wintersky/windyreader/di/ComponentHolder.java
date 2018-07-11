package com.wintersky.windyreader.di;

/**
 * Created by tiandong on 18-3-15.
 */

public class ComponentHolder {
    private static AppComponent sAppComponent;

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    static void setAppComponent(AppComponent appComponent) {
        sAppComponent = appComponent;
    }
}
