package com.example.allench.twitterclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.allench.twitterclient.R;
import com.example.allench.twitterclient.TwitterApplication;
import com.example.allench.twitterclient.TwitterClient;
import com.example.allench.twitterclient.activities.ProfileActivity;
import com.example.allench.twitterclient.adapters.TweetsArrayAdapter;
import com.example.allench.twitterclient.models.Media;
import com.example.allench.twitterclient.models.Tweet;
import com.example.allench.twitterclient.models.User;
import com.example.allench.twitterclient.utils.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    public enum TimelineMode {
        HOME,
        MENTIONS,
        USER,
        ACCOUNT
    }

    protected TimelineMode mTimelineMode = TimelineMode.HOME;
    public String mTitle = "Home";

    private final int INFINITE_SCROLL_VISIBLE_THRESHOILD = 5;
    private SwipeRefreshLayout ptrContainer;
    private MenuItem miLoading;
    private ArrayList<Tweet> mTweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private TwitterClient client;
    private long mMaxId = 0;
    private User mUser;
    private User mAccount;

    public static TimelineFragment newInstance(TimelineMode mode, String title, User user) {
        TimelineFragment fragment = new TimelineFragment();
        fragment.mTimelineMode = mode;
        fragment.mTitle = title;
        fragment.mUser = user;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init mTweets adapter
        mTweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), mTweets);

        // init twitter client
        client = TwitterApplication.getRestClient();
    }

    public void setProgressLoading(MenuItem mi) {
        miLoading = mi;
        miLoading.setVisible(false);
    }

    private void setupListView(View v) {
        // init mTweets ListView
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        // bind infinite scroll event
        lvTweets.setOnScrollListener(new EndlessScrollListener(INFINITE_SCROLL_VISIBLE_THRESHOILD) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                fetchTimeline();
            }
        });

        // bind item click event
        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showUserProfile(mTweets.get(position).user);
            }
        });
    }

    private void setupPullToRefresh(View v) {
        // init PullToRefresh Container
        ptrContainer = (SwipeRefreshLayout) v.findViewById(R.id.ptrContainer);
        ptrContainer.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // setup refresh listener which triggers new data loading
        ptrContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadTimeline();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timeline, parent, false);

        setupListView(v);
        setupPullToRefresh(v);

        // fetch mTweets
        reloadTimeline();

        return v;
    }

    private void reloadTimeline() {
        mMaxId = 0;
        fetchTimeline();
    }

    private void fetchTimeline() {
        if (!TwitterClient.isNetworkAvailable(getActivity())) {
            loadTweetsFromSQLite();
            // hide refreshing indicator
            ptrContainer.setRefreshing(false);
            return;
        }
        // if end, do nothing, just exit
        if (mMaxId == -1) {
            return;
        }
        // implement handler
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (miLoading != null) {
                    miLoading.setVisible(true);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // if first time, clear list
                if (mMaxId == 0) {
                    aTweets.clear();
                }
                // load mTweets into adapter
                ArrayList<Tweet> tweets = Tweet.fromJSONArray(response, mTimelineMode.ordinal());
                aTweets.addAll(tweets);
                // record max_id from last tweet
                mMaxId = (tweets.size() == 0) ? -1 : mTweets.get(mTweets.size() - 1).id;
                // cache status timeline
                if (mMaxId != -1) {
                    saveTweetsIntoSQLite();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                // mark as the end
                mMaxId = -1;
            }

            @Override
            public void onFinish() {
                super.onFinish();
                // hide refreshing indicator
                ptrContainer.setRefreshing(false);
                if (miLoading != null) {
                    miLoading.setVisible(false);
                }
            }
        };
        // do async http request
        switch (mTimelineMode) {
            case HOME:
                client.getHomeTimeline(handler, mMaxId);
                break;
            case MENTIONS:
                client.getMentionsTimeline(handler, mMaxId);
                break;
            case USER:
            case ACCOUNT:
                String userid = (mUser != null) ? mUser.userid : null;
                client.getUserTimeline(handler, mMaxId, userid);
                break;
        }
    }

    private void postNewTweet(String description) {
        if (!TwitterClient.isNetworkAvailable(getActivity())) {
            return;
        }
        client.postNewTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                // directly insert the new tweet into adapter
                aTweets.insert(Tweet.fromJSON(json, mTimelineMode.ordinal()), 0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, description);
    }

    private void showComposeDialog(User user) {
        if (user == null) {
            return;
        }
        // init dialog fields
        ComposeDialogFragment dialog = ComposeDialogFragment.newInstance(user);
        // bind dialog button click
        dialog.setOnButtonClickListener(new ComposeDialogFragment.OnButtonClickListener() {
            @Override
            public void onButtonApplyClick(String description) {
                postNewTweet(description);
            }
        });
        // show dialog
        dialog.show(getActivity().getSupportFragmentManager(), "fragment_compose_dialog");
    }

    public void compose() {
        if (mAccount != null) {
            showComposeDialog(mAccount);
            return;
        }
        if (!TwitterClient.isNetworkAvailable(getActivity())) {
            // load account info
            User user = new Select().from(User.class).where("aa = ?", TimelineMode.ACCOUNT.ordinal()).executeSingle();
            if (user != null) { mAccount = user; }
            showComposeDialog(mAccount);
            return;
        }
        // init twitter client
        client.getAccountInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                mAccount = User.fromJSON(json, TimelineMode.ACCOUNT.ordinal());
                mAccount.save();
                showComposeDialog(mAccount);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUserProfile(User user) {
        if (!TwitterClient.isNetworkAvailable(getActivity())) {
            return;
        }
        Intent i = new Intent(getActivity().getBaseContext(), ProfileActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }

    public void profile() {
        if (mAccount != null) {
            showUserProfile(mAccount);
            return;
        }
        if (!TwitterClient.isNetworkAvailable(getActivity())) {
            // load account info
            User user = new Select().from(User.class).where("aa = ?", TimelineMode.ACCOUNT.ordinal()).executeSingle();
            if (user != null) { mAccount = user; }
            showUserProfile(mAccount);
            return;
        }
        // init twitter client
        client.getAccountInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                mAccount = User.fromJSON(json, mTimelineMode.ordinal());
                mAccount.save();
                showUserProfile(mAccount);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveTweetsIntoSQLite() {
        // clean old data
        new Delete().from(Tweet.class).where("aa = ?", mTimelineMode.ordinal()).execute();
        new Delete().from(User.class).where("aa = ?", mTimelineMode.ordinal()).execute();
        new Delete().from(Media.class).where("aa = ?", mTimelineMode.ordinal()).execute();
        // save all mTweets
        for (int i = 0; i < mTweets.size(); i++) {
            Tweet tweet = mTweets.get(i);
            for (int k = 0; k < tweet.medias.size(); k++) {
                tweet.medias.get(k).save();
            }
            tweet.user.save();
            tweet.save();
        }
    }

    private void loadTweetsFromSQLite() {
        // load tweets
        List<Tweet> tweets = new Select().from(Tweet.class).where("aa = ?", mTimelineMode.ordinal()).execute();
        if (tweets != null) {
            aTweets.clear();
            aTweets.addAll(tweets);
        }
    }

}
