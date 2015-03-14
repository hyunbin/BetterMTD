package me.hyunbin.transit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import me.hyunbin.transit.R;

public class MainActivity extends ActionBarActivity {

    public final static String ARG_STOPID = "cashpa.bettermtd.STOPID";
    public final static String ARG_STOPNAME = "cashpa.bettermtd.STOPNAME";
    private static final String TAG_STOPID = "stop_id";
    private static final String TAG_STOPNAME = "stop_name";

    Context context;
    public ArrayList<String> mStopName;
    public ArrayList<HashMap<String, String>> mHash;
    AutoCompleteTextView textView;

    LinearLayout searchContainer;
    ImageView searchClearButton;
    MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        // Sets the toolbar as ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bus Stops");
        toolbar.setTitleTextColor(-1);
        setSupportActionBar(toolbar);

        // Setup search container view
        searchContainer = new LinearLayout(this);
        Toolbar.LayoutParams containerParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerParams.gravity = Gravity.CENTER_VERTICAL;
        searchContainer.setLayoutParams(containerParams);

        // Setup search view
        textView = new AutoCompleteTextView(this);
        // Set width / height / gravity
        int[] textSizeAttr = new int[]{android.R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(new TypedValue().data, textSizeAttr);
        int actionBarHeight = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, actionBarHeight);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.weight = 1;
        textView.setLayoutParams(params);

        // Style the autocomplete suggestions box
        textView.setDropDownVerticalOffset(2);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //textView.setDropDownWidth(width - width/12);
        textView.setDropDownWidth(width);

        // Setup display
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setPadding(2, 0, 0, 0);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setSingleLine(true);
        //textView.setImeActionLabel("Search", EditorInfo.IME_ACTION_UNSPECIFIED);
        textView.setHint("Search bus stops");
        textView.setHintTextColor(Color.parseColor("#b3ffffff"));
        ((LinearLayout) searchContainer).addView(textView);

        try {
            // Set cursor colour to white using the custom cursor.xml file
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(textView, R.drawable.cursor);
        } catch (Exception ignored) {
        }

        // Add autocomplete functionality to AutoCompleteTextView
        new ParseBusStops().execute();
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                //Toast.makeText(context, parent.getItemAtPosition(pos)+ " selected", Toast.LENGTH_SHORT).show();
                String searchFor = parent.getItemAtPosition(pos).toString();
                for(HashMap<String,String> curItem : mHash)
                {
                    if(curItem.get(searchFor) != null){
                        switchView(curItem.get(searchFor).toString(), searchFor);
                    }
                }
            }
        });

        // Setup the clear button
        searchClearButton = new ImageView(this);
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, r.getDisplayMetrics());
        LinearLayout.LayoutParams clearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clearParams.gravity = Gravity.CENTER;
        searchClearButton.setLayoutParams(clearParams);
        searchClearButton.setImageResource(R.drawable.ic_close);
        searchClearButton.setPadding(px, 0, px-24, 0);
        searchClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textView.getText().length() == 0)
                {
                    displaySearchView(false);
                }
                else{
                    textView.setText("");
                }
            }
        });
        ((LinearLayout) searchContainer).addView(searchClearButton);

        // Add search view to toolbar and hide it
        searchContainer.setVisibility(View.GONE);
        toolbar.addView(searchContainer);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);
        pager.setAdapter(new ViewPagerAdapter(context, getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Make the ViewPager look pretty
        tabs.setShouldExpand(true);
        tabs.setIndicatorColorResource(R.color.accent_alternative);
        //tabs.setTextColorResource(R.color.abc_primary_text_material_dark);
        tabs.setViewPager(pager);

    }

    @Override
    public void onRestart(){
        super.onRestart();
        displaySearchView(false);
    }

    @Override
    public void onBackPressed(){
        // Before exiting, the searchview should be cleared if available (ie: another layer of back)
        if(searchContainer.getVisibility() != View.GONE){
            displaySearchView(false);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);
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
            //Starts a new intent
            //Intent intent = new Intent(this, SearchActivity.class);
            //startActivity(intent);
            displaySearchView(true);
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchView(String stopID, String stopName)
    {
        Intent intent = new Intent(this, StopActivity.class);
        intent.putExtra(ARG_STOPID, stopID);
        intent.putExtra(ARG_STOPNAME, stopName);
        startActivity(intent);
    }

    public String loadJSONFromAsset() {
        // Loads the MTDStops.json file and returns it as a string
        String json = null;
        try {
            InputStream is = context.getAssets().open("MTDStops.json");
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

    public void displaySearchView(boolean visible) {
        if (visible) {
            // Hide search button, display EditText
            searchItem.setVisible(false);
            searchContainer.setVisibility(View.VISIBLE);

            // Shift focus to the search EditText
            textView.requestFocus();

            // Pop up the soft keyboard
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    textView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    textView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                }
            }, 200);

        } else {
            // Hide the EditText and put the search button back on the Toolbar.
            // This sometimes fails when it isn't postDelayed(), don't know why.
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setText("");
                    searchContainer.setVisibility(View.GONE);
                    searchItem.setVisible(true);
                }
            }, 200);

            // Hide the keyboard because the search box has been hidden
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
        }
    }

    private class ParseBusStops extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
            mStopName = new ArrayList<String>();
            mHash = new ArrayList<HashMap<String, String>>();
        }

        protected Void doInBackground(Void ... arg0) {
            String j = loadJSONFromAsset();
            JSONArray obj = null;
            // Create a JSON array from the string
            try {
                obj = new JSONArray(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Parse through the array, filling in the HashMap for key IDs and ArrayList
            for (int i = 0; i < obj.length(); i++) {
                try {
                    JSONObject c = obj.getJSONObject(i);
                    String stopID = c.getString(TAG_STOPID);
                    String stopName = c.getString(TAG_STOPNAME);

                    mStopName.add(stopName);
                    HashMap<String, String> key = new HashMap<String, String>();
                    key.put(stopName, stopID);
                    mHash.add(key);

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Sets the autocomplete adapter using the parsed JSON information
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_dropdown_item, mStopName);
            textView.setAdapter(adapter);
        }
    }
}
