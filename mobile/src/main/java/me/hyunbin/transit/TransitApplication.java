package me.hyunbin.transit;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import me.hyunbin.transit.activities.DeparturesActivity;
import me.hyunbin.transit.activities.MainActivity;
import me.hyunbin.transit.helpers.MostUsedStopsLogger;
import me.hyunbin.transit.models.SimpleCountingStop;

/**
 * Created by Hyunbin on 7/1/15.
 */
public class TransitApplication extends Application {

  private static String TAG = TransitApplication.class.getSimpleName();
  private SharedPreferences mSharedPrefs;

  @Override
  public void onCreate() {
    super.onCreate();
    // Initialize Paper db
    Paper.init(getApplicationContext());

    // Initialize Crashlytics only if release mode
    CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
    Fabric.with(this, new Crashlytics.Builder().core(core).build(), core);

    // Initiate auto-backup on migration from versionCode > 15
    mSharedPrefs = this.getSharedPreferences("transit_backup", 0);
    if (mSharedPrefs.getBoolean("first", true)) {
      new BackupManager(this).dataChanged();
      mSharedPrefs.edit().putBoolean("first", false).apply();
    }

    updateDynamicShortcuts();
  }

  @TargetApi(25)
  private void updateDynamicShortcuts() {
    if(android.os.Build.VERSION.SDK_INT < 25) {
      return;
    }

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        MostUsedStopsLogger logger = new MostUsedStopsLogger();
        List<SimpleCountingStop> stops = logger.getMostFrequentStops();

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
        for (SimpleCountingStop stop : stops) {
          Intent intent = new Intent(getApplicationContext(), DeparturesActivity.class);
          intent.setAction(Intent.ACTION_VIEW);
          intent.putExtra(MainActivity.ARG_STOPID, stop.getStopId());
          intent.putExtra(MainActivity.ARG_STOPNAME, stop.getStopName());
          ShortcutInfo shortcut = new ShortcutInfo.Builder(
              getApplicationContext(),
              stop.getStopId())
              .setShortLabel(stop.getStopName())
              .setLongLabel(stop.getStopName())
              .setIcon(Icon.createWithResource(
                  getApplicationContext(),
                  R.drawable.ic_shortcut_pin_drop))
              .setIntent(intent)
              .build();
          shortcutInfoList.add(shortcut);
        }

        shortcutManager.setDynamicShortcuts(shortcutInfoList);
      }
    };
    AsyncTask.execute(runnable);
  }
}
