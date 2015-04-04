package me.hyunbin.transit;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Hyunbin on 3/9/15.
 * Adapted with liberty from http://www.android4devs.com/2015/01/how-to-make-material-design-sliding-tabs.html
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[] = {"Favorites","Near Me"}; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    Context context;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(Context c, FragmentManager fm) {
        super(fm);
        this.context = c;
        this.NumbOfTabs = 2;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Tab1 tab1 = new Tab1();
            return tab1;
        }
        else if(position==1) // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Tab3 tab3 = new Tab3();
            return tab3;
        }
        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

}
