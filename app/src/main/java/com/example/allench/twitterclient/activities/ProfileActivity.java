package com.example.allench.twitterclient.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.allench.twitterclient.R;
import com.example.allench.twitterclient.fragments.ProfileHeaderFragment;
import com.example.allench.twitterclient.fragments.TimelineFragment;
import com.example.allench.twitterclient.models.User;

public class ProfileActivity extends ActionBarActivity {

    private MenuItem miLoading;
    private ProfileHeaderFragment flUserProfile;
    private TimelineFragment flUserTimeline;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // get User
        mUser = (User) getIntent().getSerializableExtra("user");

        // setup title
        getSupportActionBar().setTitle("@" + mUser.userid);

        // back home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // action bar icon
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // init flUserProfile
        flUserProfile = (ProfileHeaderFragment) getSupportFragmentManager().findFragmentById(R.id.flUserProfile);
        flUserProfile.setUser(mUser);

        // init flUserTimeline
        if (mUser.aa == TimelineFragment.TimelineMode.ACCOUNT.ordinal()) {
            flUserTimeline = TimelineFragment.newInstance(TimelineFragment.TimelineMode.ACCOUNT, "Account", mUser);
        } else {
            flUserTimeline = TimelineFragment.newInstance(TimelineFragment.TimelineMode.USER, "User", mUser);
        }
        FragmentTransaction ft = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flUserTimeline, flUserTimeline);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        // setup miLoading
        miLoading = menu.findItem(R.id.action_loading);
        flUserTimeline.setProgressLoading(miLoading);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
