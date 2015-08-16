package com.example.allench.twitterclient.fragments;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.allench.twitterclient.R;
import com.example.allench.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

public class TweetFragment extends RelativeLayout {
    private TextView tvUserId;
    private TextView tvUserName;
    private TextView tvCreateAt;
    private TextView tvDescription;
    private TextView tvFavoriteCount;
    private TextView tvRetweetCount;
    private ImageView ivAvatar;
    private ImageView ivPhoto;

    public TweetFragment(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.fragment_tweet_item, this, true);
        setupChildren();
    }

    private void setupChildren() {
        tvUserId = (TextView) findViewById(R.id.tvUserId);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvCreateAt = (TextView) findViewById(R.id.tvCreateAt);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvFavoriteCount = (TextView) findViewById(R.id.tvFavoriteCount);
        tvRetweetCount = (TextView) findViewById(R.id.tvRetweetCount);
        ivAvatar = (ImageView) findViewById(R.id.ivAvatar);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
    }

    public void setItem(Tweet tweet) {
        tvUserId.setText("@" + tweet.user.userid);
        tvUserName.setText(tweet.user.username);
        tvCreateAt.setText(tweet.created_at);
        tvDescription.setText(tweet.text);
        tvFavoriteCount.setText(String.valueOf(tweet.favorite_count) + " favorites");
        tvRetweetCount.setText(String.valueOf(tweet.retweet_count) + " retweets");
        ivAvatar.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.user.profile_image_url).placeholder(R.drawable.loading).into(ivAvatar);
        if ((tweet.medias != null) && (tweet.medias.size() > 0) && ("photo".equals(tweet.medias.get(0).type))) {
            ivPhoto.setVisibility(VISIBLE);
            ivPhoto.setImageResource(android.R.color.transparent);
            Picasso.with(getContext()).load(tweet.medias.get(0).media_url).placeholder(R.drawable.loading).into(ivPhoto);
        } else {
            ivPhoto.setVisibility(INVISIBLE);
            ivPhoto.setImageResource(android.R.color.transparent);
        }
    }
}
