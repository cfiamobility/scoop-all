package ca.gc.inspection.scoop;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class searchPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public searchPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                topSearchResults tab1 = new topSearchResults();
                return tab1;
            case 1:
                peopleSearchResults tab2 = new peopleSearchResults();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
