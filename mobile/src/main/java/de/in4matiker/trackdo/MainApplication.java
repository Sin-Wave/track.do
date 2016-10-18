package de.in4matiker.trackdo;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EApplication;

@EApplication
public class MainApplication extends Application {
    @AfterInject
    void init() {
        JodaTimeAndroid.init(this);
    }
}
