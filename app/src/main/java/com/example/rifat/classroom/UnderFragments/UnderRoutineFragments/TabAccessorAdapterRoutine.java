package com.example.rifat.classroom.UnderFragments.UnderRoutineFragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAccessorAdapterRoutine extends FragmentPagerAdapter {
    public TabAccessorAdapterRoutine(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Today todayFragment = new Today();
                return todayFragment;
            case 1:
                Full fullFragment = new Full();
                return fullFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Today's Routine";
            case 1:
                return "Full Routine";
            default:
                return null;
        }
    }
}
