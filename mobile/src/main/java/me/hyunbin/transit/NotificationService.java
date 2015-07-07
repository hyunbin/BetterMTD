package me.hyunbin.transit;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import me.hyunbin.transit.activities.StopsActivity;
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
    private String mVehicleIdString;
    private RestClient mRestClient;
    private Callback<DeparturesByStopResponse> mCallback;

    public NotificationService(){
        super(TAG);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mRestClient = new RestClient();
        mCallback = new Callback<DeparturesByStopResponse>() {
            @Override
            public void success(DeparturesByStopResponse departuresByStopResponse, Response response) {
                Log.d(TAG, "Retrofit success!");
                List<Departure> departures = departuresByStopResponse.getDepartures();
                for (int i = 0; i < departures.size(); i++) {
                    if(departures.get(i).getVehicleId().equals(mVehicleIdString)){
                        updateNotification(departures.get(i));
                        break;
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
        Log.e(TAG, departure.getExpectedMins() + " min remaining!");
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(0, this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(departure.getExpectedMins() + " min remaining")
                .setContentText("Until " + departure.getHeadsign() + " arrives")
                .addAction(R.drawable.ic_close, "Dismiss now", dismissIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mStopIdString = intent.getStringExtra("current_stop");
        mVehicleIdString = intent.getStringExtra("vehicle_id");
        mRestClient.getDeparturesByStop(mStopIdString, mCallback);
    }
}
