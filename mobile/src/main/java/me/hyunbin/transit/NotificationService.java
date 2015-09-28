package me.hyunbin.transit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.List;

import me.hyunbin.transit.activities.DeparturesActivity;
import me.hyunbin.transit.activities.MainActivity;
import me.hyunbin.transit.models.Departure;
import me.hyunbin.transit.models.DeparturesByStopResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Hyunbin on 7/6/2015.
 * This class borrows code from the core IntentService class to handle Intents,
 * but manages lifecycle to persist until alarm rings or notification is dismissed by user.
 *
 */
public class NotificationService extends Service {

    private static final String TAG = NotificationService.class.getSimpleName();

    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private int mServiceId;

    private String mStopIdString;
    private long mVehicleIdString;
    private RestClient mRestClient;
    private Callback<DeparturesByStopResponse> mCallback;
    private Handler mHandler;
    private int timeToRingAlarm;
    private NotificationManager mNotificationManager;
    private int mCachedTimeRemaining;
    private String mCachedHeadSign;
    private String mStopName;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            mServiceId = msg.arg1;
        }
    }

    public NotificationService(){
        super();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        onStart(intent, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");
        cancelNotification();
    }

    @Override
    public void onCreate(){
        super.onCreate();

        HandlerThread thread = new HandlerThread("IntentService[" + TAG + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

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
                mNotificationManager.cancel(1);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Network Error")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentText("Attempting to reconnect...")
                        .setOngoing(true)
                        .addAction(R.drawable.ic_close, "Dismiss now", getDismissIntent(2));;
                mNotificationManager.notify(2, builder.build());
            }
        };
    }

    private void updateNotification(Departure departure){
        PendingIntent dismissIntent = getDismissIntent(1);
        Intent intent = new Intent(this, DeparturesActivity.class);
        intent.putExtra(MainActivity.ARG_STOPID, mStopIdString);
        intent.putExtra(MainActivity.ARG_STOPNAME, mStopName);
        Log.d(TAG, "Web update of notification: " + departure.getExpectedMins() + " min remaining");

        mCachedTimeRemaining = departure.getExpectedMins();
        mCachedHeadSign = departure.getHeadsign();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(departure.getExpectedMins() + " min remaining")
                .setContentText("Until " + departure.getHeadsign() + " arrives")
                .setOngoing(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent);

        startForeground(1, builder.build());

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
        mNotificationManager.cancel(1);
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
        } else if(expectedMins <= timeToRingAlarm + 12){
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
        stopForeground(true);
        mNotificationManager.notify(2, builder.build());
//        stopSelf(mServiceId);
    }

    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
        if(intent.getBooleanExtra("dismiss", false)){
            // Case 1: Dismiss intent
            intent.getIntExtra("notification_id", 1);
            cancelNotification();
            stopForeground(true);
            stopSelf(mServiceId);
        } else{
            // Case 2: Initialization for intent calls (ie: called from long pressing a departure)
            // Restriction: only one alarm can be set right now
            cancelNotification();
            mStopIdString = intent.getStringExtra("current_stop");
            mStopName = intent.getStringExtra("stop_name");
            mVehicleIdString = intent.getLongExtra("unique_id", 1);
            timeToRingAlarm = intent.getIntExtra("alarm_time", 5);
            mRestClient.getDeparturesByStop(mStopIdString, mCallback);

            // Log metrics because I'm a sucker for data
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentName("NotificationAlarm")
                    .putCustomAttribute("Minutes", intent.getIntExtra("alarm_time", -1)));
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
            localUpdateNotification();
            mHandler.postDelayed(localUpdateTask, 70 * 1000);
        }
    };

    private void localUpdateNotification(){
        Log.d(TAG, "Local update of notification: " + (mCachedTimeRemaining - 1) + " min remaining");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(--mCachedTimeRemaining + " min remaining")
                .setContentText("Until " + mCachedHeadSign + " arrives")
                .setOngoing(true)
                .addAction(R.drawable.ic_close, "Dismiss now", getDismissIntent(1));

        startForeground(1, builder.build());
    }
}
