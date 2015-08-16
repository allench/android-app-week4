package com.example.allench.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.allench.twitterclient.R;
import com.example.allench.twitterclient.models.User;
import com.squareup.picasso.Picasso;

public class ProfileHeaderFragment extends Fragment {

    private ImageView ivProfileAvatar;
    private TextView tvProfileUserId;
    private TextView tvProfileUserName;
    private TextView tvProfileDescription;
    private TextView tvProfileTweetsCount;
    private TextView tvProfileFollowingCount;
    private TextView tvProfileFollowersCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, container, false);
        ivProfileAvatar = (ImageView) v.findViewById(R.id.ivProfileAvatar);
        tvProfileUserId = (TextView) v.findViewById(R.id.tvProfileUserId);
        tvProfileUserName = (TextView) v.findViewById(R.id.tvProfileUserName);
        tvProfileDescription = (TextView) v.findViewById(R.id.tvProfileDescription);
        tvProfileTweetsCount = (TextView) v.findViewById(R.id.tvProfileTweetsCount);
        tvProfileFollowingCount = (TextView) v.findViewById(R.id.tvProfileFollowingCount);
        tvProfileFollowersCount = (TextView) v.findViewById(R.id.tvProfileFollowersCount);
        return v;
    }

    public void setUser(User user) {
        if (user == null) { return; }
        tvProfileUserId.setText("@" + user.userid);
        tvProfileUserName.setText(user.username);
        tvProfileDescription.setText(user.description);
        tvProfileTweetsCount.setText(String.valueOf(user.tweets_count) + " tweets");
        tvProfileFollowingCount.setText(String.valueOf(user.following_count) + " following");
        tvProfileFollowersCount.setText(String.valueOf(user.followers_count) + " followers");
        ivProfileAvatar.setImageResource(android.R.color.transparent);
        Picasso.with(getActivity().getBaseContext()).load(user.profile_image_url).placeholder(R.drawable.loading).into(ivProfileAvatar);
    }
}
