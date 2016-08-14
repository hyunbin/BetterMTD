package me.hyunbin.transit;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import me.hyunbin.transit.fragments.FavoritesFragment;
import me.hyunbin.transit.fragments.NearMeFragment;
import me.hyunbin.transit.fragments.NavigationFragment;

/**
 * This adapter contains the logic for the top navigation bar used in the main screen of the app.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

  private static final CharSequence PAGE_TITLES[] = {"Favorites", "Near Me", "Navigation"};

  private int mTabCount;

  public ViewPagerAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
    mTabCount = 3;
  }

  @Override
  public Fragment getItem(int position) {
    if (position == 0) {
      return new FavoritesFragment();
    } else if (position == 1) {
      return new NearMeFragment();
    } else if (position == 2) {
      return new NavigationFragment();
    }
    return null;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return PAGE_TITLES[position];
  }

  @Override
  public int getCount() {
    return mTabCount;
  }
}
