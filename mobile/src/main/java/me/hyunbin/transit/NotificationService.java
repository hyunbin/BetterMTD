package me.hyunbin.transit;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import me.hyunbin.transit.models.Departure;
import me.hyunbin.transit.models.DeparturesByStopResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 7/6/2015.
 */
public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getSimpleName();
    private String mStopIdString;
    private long mVehicleIdString;
    private RestClient mRestClient;
    private Callback<DeparturesByStopResponse> mCallback;
    private Handler mHandler;
    private int timeToRingAlarm;
    private NotificationManager mNotificationManager;
    private int mCachedTimeRemaining;
    private String mCachedHeadSign;

    public NotificationService(){
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mHandler = new Handler();
        mRestClient = new RestClient();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mCallback = new Callback<DeparturesByStopResponse>() {
            @Override
            public void success(DeparturesByStopResponse departuresByStopResponse, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<Departure> departures = departuresByStopResponse.getDepartures();
                for (int i = 0; i < departures.size(); i++) {
                    if(departures.get(i).getUniqueId() == mVehicleIdString){
                        updateNotification(departures.get(i));
                        break;
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
                // If bus disappears off tracker, then cancel notification and post an error
                mNotificationManager.cancel(0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Network Error")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText("Attempting to reconnect...");
                mNotificationManager.notify(1, builder.build());
            }
        };
    }

    private void updateNotification(Departure departure){
        PendingIntent dismissIntent = getDismissIntent(0);

        mCachedTimeRemaining = departure.getExpectedMins();
        mCachedHeadSign = departure.getHeadsign();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(departure.getExpectedMins() + " min remaining")
                .setContentText("Until " + departure.getHeadsign() + " arrives")
                .setOngoing(true)
                .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent);

        mNotificationManager.notify(0, builder.build());

        // Call helper function to set a delay until next refresh
        postNextRefresh(departure.getExpectedMins());
    }

    private PendingIntent getDismissIntent(int notificationId){
        Intent intent = new Intent(this, NotificationService.class);
        intent.putExtra("dismiss", true);
        intent.putExtra("notification_id", notificationId);
        PendingIntent dismissIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }

    private void cancelNotification(){
        // Removes current notification and all related callbacks
        mNotificationManager.cancel(0);
        mHandler.removeCallbacks(webUpdateTask);
        mHandler.removeCallbacks(localUpdateTask);
    }

    private void postNextRefresh(int expectedMins){
        if(expectedMins <= timeToRingAlarm){
            // Case: if it's time to ring the alarm
            cancelNotification();
            ringAlarm(expectedMins);
        } else if(expectedMins <= timeToRingAlarm + 6){
            // Case: if it's close to ringing the alarm, web update once a minute-ish
            mHandler.postDelayed(webUpdateTask, 60 * 1000);
        } else if(expectedMins <= timeToRingAlarm + 15){
            // Case: if it's approaching set time to ring alarm, web update once every 3 minutes
            mHandler.postDelayed(webUpdateTask, 3 * 60 * 1000);
            mHandler.removeCallbacks(localUpdateTask);
            mHandler.postDelayed(localUpdateTask, 70 * 1000);
        } else{
            // Case: if it's not even close to set time to ring alarm, web update once every 6 minutes
            mHandler.postDelayed(webUpdateTask, 6 * 60 * 1000);
            mHandler.removeCallbacks(localUpdateTask);
            mHandler.postDelayed(localUpdateTask, 70 * 1000);
        }
    }

    private void ringAlarm(int min) {
        long[] vibratePattern = {0, 600, 1000, 600, 1000, 600};
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("It's time to leave")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentText("Bus will arrive in " + min + " min.");
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(vibratePattern, -1);
        mNotificationManager.notify(1, builder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent.getBooleanExtra("dismiss", false)){
            // Case 1: Dismiss intent
            intent.getIntExtra("notification_id", 0);
            cancelNotification();
        } else{
            // Case 2: Initialization for intent calls (ie: called from long pressing a departure)
            // Restriction: only one alarm can be set right now
            cancelNotification();
            mStopIdString = intent.getStringExtra("current_stop");
            mVehicleIdString = intent.getLongExtra("unique_id", 0);
            timeToRingAlarm = intent.getIntExtra("alarm_time", 5);
            mRestClient.getDeparturesByStop(mStopIdString, mCallback);
        }
    }

    private final Runnable webUpdateTask = new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh notification data at a predetermined interval
            // Makes a web call to Retrofit to pull latest info
            mRestClient.getDeparturesByStop(mStopIdString, mCallback);
        }
    };

    private final Runnable localUpdateTask = new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh notification data at a predetermined interval
            // Just guesstimates the time remaining using previously synchronized data
            Log.d(TAG, "Local update of notification");
            localUpdateNotification();
            mHandler.postDelayed(localUpdateTask, 70 * 1000);
        }
    };

    private void localUpdateNotification(){
        PendingIntent dismissIntent = getDismissIntent(0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(--mCachedTimeRemaining + " min remaining")
                .setContentText("Until " + mCachedHeadSign + " arrives")
                .setOngoing(true)
                .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent);

        mNotificationManager.notify(0, builder.build());
    }
}
