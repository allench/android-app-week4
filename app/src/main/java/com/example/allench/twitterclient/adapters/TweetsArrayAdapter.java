package com.example.allench.twitterclient.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.allench.twitterclient.fragments.TweetFragment;
import com.example.allench.twitterclient.models.Tweet;

import java.util.List;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        TweetFragment tweetView = (TweetFragment) convertView;
        if (tweetView == null) {
            tweetView = new TweetFragment(getContext());
        }
        tweetView.setItem(getItem(position));
        return tweetView;
    }
}
