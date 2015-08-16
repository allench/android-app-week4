package com.example.allench.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.example.allench.twitterclient.R;
import com.example.allench.twitterclient.adapters.TimelineFragmentPagerAdapter;
import com.example.allench.twitterclient.fragments.TimelineFragment;

import java.util.ArrayList;

public class TimelineActivity extends ActionBarActivity {

    private MenuItem miLoading;
    private ViewPager vpPager;
    private ArrayList<TimelineFragment> tfList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // action bar icon
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // create page fragments
        tfList.add(new TimelineFragment().newInstance(TimelineFragment.TimelineMode.HOME, "#Home", null));
        tfList.add(new TimelineFragment().newInstance(TimelineFragment.TimelineMode.MENTIONS, "@Mentions", null));
        // setup viewpager
        vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpPager.setAdapter(new TimelineFragmentPagerAdapter(getSupportFragmentManager(), tfList));
        // setup psTabs
        PagerSlidingTabStrip psTabs = (PagerSlidingTabStrip) findViewById(R.id.psTabs);
        psTabs.setViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        // setup miLoading
        miLoading = menu.findItem(R.id.action_loading);
        for (int i = 0; i < tfList.size(); i++) {
            tfList.get(i).setProgressLoading(miLoading);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // compose a new tweet
        if (id == R.id.action_compose) {
            tfList.get(0).compose();
            return true;
        }

        // view account profile
        if (id == R.id.action_profile) {
            tfList.get(0).profile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
