package com.codepath.apps.twitterhigh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.twitterhigh.adapters.TweetsAdapter;
import com.codepath.apps.twitterhigh.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    TwitterClient client;
    List<Tweet> tweetList = new ArrayList<Tweet>();
    TweetsAdapter tweetsAdapter;
    int page = 0;
    int REQUEST_CODE = 101;

    private SwipeRefreshLayout swipeContainer;

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_action_bar, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        client = TwitterApplication.getRestClient();
        ListView listView = (ListView) findViewById(R.id.lvTweets);
        tweetsAdapter = new TweetsAdapter(this, tweetList);
        listView.setAdapter(tweetsAdapter);

        showTweets();

        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                Log.i("feed", "Calling fetch due to scroll");
                showTweets();
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                tweetList.clear();
                showTweets();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


//        client.getUserProfile(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                try {
//                    String name = response.getString("name");
//                    String screenName = response.getString("screen_name");
//                    String profileImageUrl = response.getString("profile_image_url");
//                    SharedPreferences pref =
//                            PreferenceManager.getDefaultSharedPreferences(FeedActivity.this);
//                    SharedPreferences.Editor edit = pref.edit();
//                    edit.putString("name", name);
//                    edit.putString("screen_name", screenName);
//                    edit.putString("profile_image_url", profileImageUrl);
//                    edit.commit();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject userObject = response.getJSONObject("user");
                    String name = userObject.getString("name");
                    String screenName = userObject.getString("screen_name");
                    String profileImageUrl = userObject.getString("profile_image_url");
                    SharedPreferences pref =
                            PreferenceManager.getDefaultSharedPreferences(FeedActivity.this);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString("name", name);
                    edit.putString("screen_name", screenName);
                    edit.putString("profile_image_url", profileImageUrl);
                    edit.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showTweets() {
        page = page + 1;

        client.getHomeTimeline(page, new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, Header[] headers, JSONArray jsonArray) {
                Log.i("INFO", "timeline: " + jsonArray.toString());
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
                for (Tweet tweet : tweets) {
                    tweetList.add(tweet);
                }
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Start the compose activity and fetch the tweet back to upload to twitter.
     * @param item
     */
    public void startComposeActivity(MenuItem item) {
        Intent i = new Intent(FeedActivity.this, ComposeActivity.class);
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String name = pref.getString("name", "_name_");
        String screenName = pref.getString("screen_name", "_screen_name_");
        String profileImageUrl = pref.getString("profile_image_url", "http://findicons.com/files/icons/61/dragon_soft/512/user.png");
        i.putExtra("name", name);
        i.putExtra("screen_name", screenName);
        i.putExtra("profile_image_url", profileImageUrl);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String value = data.getExtras().getString("tweetMessage");
            client.postTweet(value, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.i("Info", "Tweet posted.");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("Error", "Tweet post failed.");
                }
            });
        }
    }

    /**
     * Signout user.
     * @param item
     */
    public void signout(MenuItem item) {
        client.clearAccessToken();
    }
}
