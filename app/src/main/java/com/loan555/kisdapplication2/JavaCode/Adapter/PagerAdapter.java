package com.loan555.kisdapplication2.JavaCode.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.loan555.kisdapplication2.JavaCode.Fragment.BlacklistFragment;
import com.loan555.kisdapplication2.JavaCode.Fragment.ChartFragment;
import com.loan555.kisdapplication2.JavaCode.Fragment.HistoryFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "KA.PagerAdapter";
    private int numOfTabs;
    public PagerAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs = numOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChartFragment();
            case 1:
                return new HistoryFragment();
            case 2:
                return new BlacklistFragment();
            default:
                return new ChartFragment();

        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
