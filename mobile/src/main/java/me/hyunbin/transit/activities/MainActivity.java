package me.hyunbin.transit.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.hyunbin.transit.R;
import me.hyunbin.transit.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  public static final String ARG_STOPID = "cashpa.bettermtd.STOPID";
  public static final String ARG_STOPNAME = "cashpa.bettermtd.STOPNAME";
  private static final String TAG_STOPID = "stop_id";
  private static final String TAG_STOPNAME = "stop_name";

  private Context mContext;

  private List<HashMap<String, String>> mHash;
  private List<String> mStopName;

  private AutoCompleteTextView mTextView;
  private ImageView mSearchClearButton;
  private LinearLayout mSearchContainer;
  private MenuItem mSearchItem;
  private Toolbar mToolbar;

  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private Location mLastLocation;
  private Location mPrevLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    mContext = getApplicationContext();

    // Sets the toolbar as ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle("Bus Stops");
    mToolbar.setTitleTextColor(-1);
    setSupportActionBar(mToolbar);

    // Setup search container
    setupSearchBar();

    // Initialize the ViewPager and set an adapter
    ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
    pager.setOffscreenPageLimit(2);
    pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(pager);
  }

  private void setupSearchBar() {
    // Setup search container view
    mSearchContainer = new LinearLayout(this);
    Toolbar.LayoutParams containerParams =
        new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    containerParams.gravity = Gravity.CENTER_VERTICAL;
    mSearchContainer.setLayoutParams(containerParams);

    // Setup search view
    mTextView = new AutoCompleteTextView(this);

    // Set width / height / gravity
    int[] textSizeAttr = new int[]{android.R.attr.actionBarSize};
    int indexOfAttrTextSize = 0;
    TypedArray a = obtainStyledAttributes(new TypedValue().data, textSizeAttr);
    int actionBarHeight = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
    a.recycle();
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, actionBarHeight);
    params.gravity = Gravity.CENTER_VERTICAL;
    params.weight = 1;
    mTextView.setLayoutParams(params);

    // Style the autocomplete suggestions box
    mTextView.setDropDownVerticalOffset(2);
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int width = size.x;
    mTextView.setDropDownWidth(width);

    // Setup display
    mTextView.setBackgroundColor(Color.TRANSPARENT);
    mTextView.setPadding(2, 0, 0, 0);
    mTextView.setTextColor(Color.WHITE);
    mTextView.setGravity(Gravity.CENTER_VERTICAL);
    mTextView.setSingleLine(true);
    mTextView.setHint("Search bus stops");
    mTextView.setHintTextColor(Color.parseColor("#b3ffffff"));
    mSearchContainer.addView(mTextView);

    try {
      // Set cursor colour to white using the custom cursor.xml file
      Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
      f.setAccessible(true);
      f.set(mTextView, R.drawable.cursor);
    } catch (Exception ignored) {
    }

    // Add autocomplete functionality to AutoCompleteTextView
    new ParseBusStops().execute();
    mTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
        String searchFor = parent.getItemAtPosition(pos).toString();
        for (HashMap<String, String> curItem : mHash) {
          if (curItem.get(searchFor) != null) {
            switchView(curItem.get(searchFor), searchFor);
          }
        }
      }
    });

    // Setup the clear button
    mSearchClearButton = new ImageView(this);
    Resources r = getResources();
    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
        r.getDisplayMetrics());
    LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    clearParams.gravity = Gravity.CENTER;
    mSearchClearButton.setLayoutParams(clearParams);
    mSearchClearButton.setImageResource(R.drawable.ic_close);
    mSearchClearButton.setPadding(px / 2, 0, px, 0);
    mSearchClearButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mTextView.getText().length() == 0) {
          displaySearchView(false);
        } else {
          mTextView.setText("");
        }
      }
    });
    mSearchContainer.addView(mSearchClearButton);

    // Add search view to toolbar and hide it
    mSearchContainer.setVisibility(View.GONE);
    mToolbar.addView(mSearchContainer);
  }

  @Override
  public void onRestart() {
    super.onRestart();
    displaySearchView(false);
  }

  @Override
  public void onBackPressed() {
    // Before exiting, the searchview should be cleared if available (ie: another layer of back)
    if (mSearchContainer.getVisibility() != View.GONE) {
      displaySearchView(false);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    mSearchItem = menu.findItem(R.id.action_search);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_search) {
      displaySearchView(true);
    }

    return super.onOptionsItemSelected(item);
  }

  private void switchView(String stopID, String stopName) {
    Intent intent = new Intent(this, DeparturesActivity.class);
    intent.putExtra(ARG_STOPID, stopID);
    intent.putExtra(ARG_STOPNAME, stopName);
    startActivity(intent);
  }

  private String loadJSONFromAsset() {
    // Loads the MTDStops.json file and returns it as a string
    String json;
    try {
      InputStream is = mContext.getAssets().open("MTDStops.json");
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      json = new String(buffer, "UTF-8");

    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
    return json;
  }

  private void displaySearchView(boolean visible) {
    if (visible) {
      // Hide search button, display EditText
      mSearchItem.setVisible(false);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        final float scale = getResources().getDisplayMetrics().density;
        int cx = mToolbar.getRight() - (int) (24 * scale + 0.5f);
        int cy = mToolbar.getTop() + (int) (24 * scale + 0.5f);

        // get the final radius for the clipping circle
        int finalRadius = Math.max(mToolbar.getWidth(), mToolbar.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator outerCircleAnimation = ViewAnimationUtils.createCircularReveal(mSearchContainer, cx, cy, 0, finalRadius);
        outerCircleAnimation.setInterpolator(new DecelerateInterpolator());
        outerCircleAnimation.setDuration(250);

        // Start animation
        mSearchContainer.setVisibility(View.VISIBLE);
        outerCircleAnimation.start();
      } else
        mSearchContainer.setVisibility(View.VISIBLE);

      // Shift focus to the search EditText
      mTextView.requestFocus();

      // Pop up the soft keyboard
      new Handler().postDelayed(new Runnable() {
        public void run() {
          mTextView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
              SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
          mTextView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
              SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }
      }, 200);
    } else {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // get the center for the clipping circle
        int cx = (mSearchClearButton.getLeft() + mSearchClearButton.getRight()) / 2;
        int cy = (mSearchClearButton.getTop() + mSearchClearButton.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = mToolbar.getWidth();

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(mSearchContainer, cx, cy, initialRadius, 0);
        anim.setInterpolator(new FastOutSlowInInterpolator());

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mTextView.setText("");

            mSearchContainer.setVisibility(View.GONE);
            mSearchItem.setVisible(true);

            // Hide the keyboard because the search box has been hidden
            InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTextView.getWindowToken(), 0);
          }
        });
        anim.start();
      } else {
        // Hide the EditText and put the search button back on the Toolbar.
        // This sometimes fails when it isn't postDelayed(), don't know why.
        mTextView.postDelayed(new Runnable() {
          @Override
          public void run() {
            mTextView.setText("");
            mSearchContainer.setVisibility(View.GONE);
            mSearchItem.setVisible(true);
          }
        }, 200);

        // Hide the keyboard because the search box has been hidden
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextView.getWindowToken(), 0);
      }
    }
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {

  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

  }

  private class ParseBusStops extends AsyncTask<Void, Void, Void> {
    protected void onPreExecute() {
      super.onPreExecute();
      mStopName = new ArrayList<>();
      mHash = new ArrayList<>();
    }

    protected Void doInBackground(Void... arg0) {
      String j = loadJSONFromAsset();
      JSONArray obj = null;

      // Create a JSON array from the string
      try {
        obj = new JSONArray(j);
      } catch (JSONException e) {
        e.printStackTrace();
      }

      // Parse through the array, filling in the HashMap for key IDs and ArrayList
      assert obj != null;
      for (int i = 0; i < obj.length(); i++) {
        try {
          JSONObject c = obj.getJSONObject(i);
          String stopID = c.getString(TAG_STOPID);
          String stopName = c.getString(TAG_STOPNAME);

          mStopName.add(stopName);
          HashMap<String, String> key = new HashMap<String, String>();
          key.put(stopName, stopID);
          mHash.add(key);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      return null;
    }

    protected void onPostExecute(Void result) {
      super.onPostExecute(result);
      // Sets the autocomplete adapter using the parsed JSON information
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(
          mContext,
          R.layout.simple_dropdown_item,
          mStopName);
      mTextView.setAdapter(adapter);
    }
  }
}
