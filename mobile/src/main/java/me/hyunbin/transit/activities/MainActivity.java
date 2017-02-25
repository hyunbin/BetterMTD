package me.hyunbin.transit.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.hyunbin.transit.R;
import me.hyunbin.transit.ViewPagerAdapter;
import me.hyunbin.transit.fragments.DosSearchFragment;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();

  public static final String ARG_STOPID = "cashpa.bettermtd.STOPID";
  public static final String ARG_STOPNAME = "cashpa.bettermtd.STOPNAME";

  private DosSearchFragment mSearchFragment;
  private Toolbar mToolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    // Sets the toolbar as ActionBar
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    // Initialize the ViewPager and set an adapter
    ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
    pager.setOffscreenPageLimit(2);
    pager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(pager);

    mSearchFragment = (DosSearchFragment) getFragmentManager()
        .findFragmentById(R.id.search_fragment);
  }

  @Override
  public void onBackPressed() {
    if (!mSearchFragment.onBackPressed()) {
      super.onBackPressed();
    }
  }
}
