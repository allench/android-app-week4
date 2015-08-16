package com.example.allench.twitterclient.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/*
{
  "id": 15113708,
  "id_str": "15113708",
  "name": "黑輪醬",
  "screen_name": "allen0960",
  "location": "Taiwan",
  "description": "Love Is a Beautiful Pain, Endless Tears.",
  "url": null,
  "entities": {
    "description": {
      "urls": []
    }
  },
  "protected": false,
  "followers_count": 117,
  "friends_count": 100,
  "listed_count": 0,
  "created_at": "Sat Jun 14 02:35:53 +0000 2008",
  "favourites_count": 1,
  "utc_offset": 28800,
  "time_zone": "Taipei",
  "geo_enabled": true,
  "verified": false,
  "statuses_count": 824,
  "lang": "en",
  "contributors_enabled": false,
  "is_translator": false,
  "is_translation_enabled": false,
  "profile_background_color": "1A1B1F",
  "profile_background_image_url": "http://pbs.twimg.com/profile_background_images/16624278/ice-cream-mice.jpg",
  "profile_background_image_url_https": "https://pbs.twimg.com/profile_background_images/16624278/ice-cream-mice.jpg",
  "profile_background_tile": false,
  "profile_image_url": "http://pbs.twimg.com/profile_images/57039617/cat_normal.jpg",
  "profile_image_url_https": "https://pbs.twimg.com/profile_images/57039617/cat_normal.jpg",
  "profile_link_color": "2FC2EF",
  "profile_sidebar_border_color": "181A1E",
  "profile_sidebar_fill_color": "252429",
  "profile_text_color": "666666",
  "profile_use_background_image": true,
  "has_extended_profile": false,
  "default_profile": false,
  "default_profile_image": false,
  "following": false,
  "follow_request_sent": false,
  "notifications": false
}
*/

@Table(name = "User")
public class User extends Model implements Serializable {
    @Column(name = "aa")
    public int aa;
    @Column(name = "userid")
    public String userid;
    @Column(name = "username")
    public String username;
    @Column(name = "description")
    public String description;
    @Column(name = "profile_image_url")
    public String profile_image_url;
    @Column(name = "tweets_count")
    public int tweets_count;
    @Column(name = "following_count")
    public int following_count;
    @Column(name = "followers_count")
    public int followers_count;

    public static User fromJSON(JSONObject json, int aa) {
        User user = null;
        try {
            user = new User();
            user.aa = aa;
            user.userid = json.getString("screen_name");
            user.username = json.getString("name");
            user.description = json.getString("description");
            user.profile_image_url = json.getString("profile_image_url");
            user.tweets_count = json.getInt("statuses_count");
            user.following_count = json.getInt("friends_count");
            user.followers_count = json.getInt("followers_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
