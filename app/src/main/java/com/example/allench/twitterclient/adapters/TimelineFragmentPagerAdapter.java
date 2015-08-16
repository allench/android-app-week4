package com.example.allench.twitterclient.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.allench.twitterclient.fragments.TimelineFragment;

import java.util.List;

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<TimelineFragment> tfList;

    public TimelineFragmentPagerAdapter(FragmentManager fm, List<TimelineFragment> fragmentList) {
        super(fm);
        tfList = fragmentList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tfList.get(position).mTitle;
    }

    @Override
    public Fragment getItem(int position) {
        return tfList.get(position);
    }

    @Override
    public int getCount() {
        return tfList.size();
    }
}
