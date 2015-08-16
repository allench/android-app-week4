package com.example.allench.twitterclient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
    public static final String REST_URL = "https://api.twitter.com/1.1";
    public static final String REST_CONSUMER_KEY = "OFCFGzgU9UcXh5Bfuj8bFZGor";
    public static final String REST_CONSUMER_SECRET = "zgrlUWqZjzOvzyDhMOu23p17Y3GruRYGQYqRN2ISReNtVRGlqI";
    public static final String REST_CALLBACK_URL = "oauth://cptwitterclient";

    private int DEFAULT_COUNT = 25;

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // detect if network available
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetOK = (activeNetworkInfo != null) && activeNetworkInfo.isConnectedOrConnecting();
        // if network is unavailable, show a Toast to user
        if (!isNetOK) {
            Toast.makeText(activity, "Internet Connection is NOT available!", Toast.LENGTH_LONG).show();
        }
        return isNetOK;
    }

    // GetAccountInfo
    public void getAccountInfo(AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        getClient().get(apiUrl, null, handler);
    }

    // PostNewTweet
    public void postNewTweet(AsyncHttpResponseHandler handler, String description) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", description);
        getClient().post(apiUrl, params, handler);
    }

    // GetTimeline
    public void getTimeline(String apiName, AsyncHttpResponseHandler handler, long maxId, String userid) {
        RequestParams params = new RequestParams();
        if (userid != null) {
            params.put("screen_name", userid);
        }
        if (maxId > 0) {
            params.put("max_id", maxId - 1);
        }
        params.put("count", DEFAULT_COUNT);
        getClient().get(getApiUrl(apiName), params, handler);
    }

    // GetTimeline: Home
    public void getHomeTimeline(AsyncHttpResponseHandler handler, long maxId) {
        getTimeline("statuses/home_timeline.json", handler, maxId, null);
    }

    // GetTimeline: Mentions
    public void getMentionsTimeline(AsyncHttpResponseHandler handler, long maxId) {
        getTimeline("statuses/mentions_timeline.json", handler, maxId, null);
    }

    // GetTimeline: Other User
    public void getUserTimeline(AsyncHttpResponseHandler handler, long maxId, String userid) {
        getTimeline("statuses/user_timeline.json", handler, maxId, userid);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}