package me.hyunbin.transit;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class TransitApplication extends Application {

    private static String TAG = TransitApplication.class.getSimpleName();
    public String mtdApiKey;

    @Override
    public void onCreate(){
        super.onCreate();

        // Initialize Crashlytics for debug builds
        if (!BuildConfig.DEBUG) Fabric.with(this, new Crashlytics());
        else Log.e(TAG, "**In Debug mode, Crashlytics is disabled**");

        mtdApiKey = getResources().getString(R.string.mtd_api_key);
    }
}
