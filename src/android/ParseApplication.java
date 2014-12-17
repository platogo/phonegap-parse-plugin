package org.apache.cordova.core.parseplugin;

import android.app.Application;
import android.content.Context;

import com.parse.Parse;

public class ParseApplication extends Application {
    private static ParseApplication instance = new ParseApplication();

    public ParseApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        int appIdResId = getResources().getIdentifier("parse_app_id", "string", getPackageName());
        int clientKeyResId = getResources().getIdentifier("parse_client_key", "string", getPackageName());
        Parse.initialize(this, getString(appIdResId), getString(clientKeyResId));
    }
}
