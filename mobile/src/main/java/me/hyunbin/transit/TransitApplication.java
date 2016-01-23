package me.hyunbin.transit;

import android.app.Application;
import android.app.backup.BackupManager;
import android.content.SharedPreferences;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class TransitApplication extends Application {

    private static String TAG = TransitApplication.class.getSimpleName();
    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(){
        super.onCreate();
        // Initialize Crashlytics only if release mode
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());

        // Initiate auto-backup on migration from versionCode > 15
        mSharedPrefs = this.getSharedPreferences("transit_backup", 0);
        if(mSharedPrefs.getBoolean("first", true)){
            new BackupManager(this).dataChanged();
            mSharedPrefs.edit().putBoolean("first", false).commit();
        }
    }
}
