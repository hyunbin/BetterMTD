package me.hyunbin.transit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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

    public NotificationService(){
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mHandler = new Handler();
        mRestClient = new RestClient();
        mCallback = new Callback<DeparturesByStopResponse>() {
            @Override
            public void success(DeparturesByStopResponse departuresByStopResponse, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<Departure> departures = departuresByStopResponse.getDepartures();
                for (int i = 0; i < departures.size(); i++) {
                    if(departures.get(i).getUniqueId() == mVehicleIdString){
                        updateNotification(departures.get(i));
                        break;
                    } else {
                        // TODO @HyunbinTodo: What to do if bus suddenly disappears off tracker?
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Retrofit Error: " + error.toString());
            }
        };
    }

    private void updateNotification(Departure departure){
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(0, this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(departure.getExpectedMins() + " min remaining")
                .setContentText("Until " + departure.getHeadsign() + " arrives")
                .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());

        // Call helper function to set a delay until next refresh
        postNextRefresh(departure.getExpectedMins());
    }

    private void postNextRefresh(int expectedMins){
        if(expectedMins <= timeToRingAlarm){
            // Case: if it's time to ring the alarm
            // TODO @HyunbinTodo: Cancel all update notifications and ring the alarm!
        } else if(expectedMins <= timeToRingAlarm + 8){
            // Case: if it's close to ringing the alarm, web update once a minute-ish
            mHandler.postDelayed(webUpdateTask, 70 * 1000);
        } else if(expectedMins <= timeToRingAlarm + 15){
            // Case: if it's approaching set time to ring alarm, web update once every 3 minutes
            mHandler.postDelayed(webUpdateTask, 3 * 60 * 1000);
        } else{
            // Case: if it's not even close to set time to ring alarm, web update once every 5 minutes
            mHandler.postDelayed(webUpdateTask, 5 * 60 * 1000);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Initialization for intent calls (ie: called from long pressing a departure)
        mStopIdString = intent.getStringExtra("current_stop");
        mVehicleIdString = intent.getLongExtra("unique_id", 0);
        timeToRingAlarm = 5;
        mRestClient.getDeparturesByStop(mStopIdString, mCallback);
    }

    private final Runnable webUpdateTask = new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh notifcation data at a predetermined interval
            // Makes a web call to Retrofit to pull latest info
            mRestClient.getDeparturesByStop(mStopIdString, mCallback);
        }
    };

    private final Runnable localUpdateTask = new Runnable() {
        @Override
        public void run() {
            // A runnable task to refresh notifcation data at a predetermined interval
            // Just guesstimates the time remaining using previously synchronized data

        }
    };
}
