package me.hyunbin.transit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import java.util.Calendar;

/**
 * Created by Hyunbin on 4/3/15.
 */
public class AlarmHandler {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private Intent intent;
    private PendingIntent pendingIntent;

    public AlarmHandler(Context sContext) {
        // Set an alarm to repeat every x min
        alarmMgr = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(sContext, AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(sContext, 0, intent, 0);

        // Cancels existing alarm, if such exists
        alarmMgr.cancel(pendingIntent);
    }

    public AlarmHandler(Context sContext, String vehicleID, String stopID) {
        // Set an alarm to repeat every x min
        alarmMgr = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(sContext, AlarmReceiver.class);
        intent.putExtra("vehicle_id", vehicleID);
        intent.putExtra("stop_id", stopID);

        pendingIntent = PendingIntent.getBroadcast(sContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Cancels existing alarm, if such exists
        alarmMgr.cancel(pendingIntent);
        setNewRepeatingAlarm();
    }

    public void setNewRepeatingAlarm(){
        long firstTrigger = 60000;
        long interval = 60000;
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, firstTrigger, interval, pendingIntent);
    }

    public void setNewSingleAlarm(long min){
        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + min * 1000, pendingIntent);
    }
}
