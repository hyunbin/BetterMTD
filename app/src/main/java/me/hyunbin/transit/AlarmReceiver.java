package me.hyunbin.transit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Hyunbin on 4/3/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String vehicleID = intent.getStringExtra("vehicle_id");
        String stopID = intent.getStringExtra("stop_id");

        Toast.makeText(context, "DEBUG: Alarm went off for " + vehicleID + " at " + stopID, Toast.LENGTH_SHORT).show();

    }

    public void setNextAlarm(){

    }
}
