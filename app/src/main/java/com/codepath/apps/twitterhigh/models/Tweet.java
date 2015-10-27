package com.codepath.apps.twitterhigh.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;

@Table(name = "Tweets")
public class Tweet extends Model {
    // Define database columns and associated fields
    @Column(name = "userId")
    public String userId;
    @Column(name = "userHandle")
    public String userHandle;
    @Column(name = "timestamp")
    public String timestamp;
    @Column(name = "body")
    public String body;
    @Column(name = "profileImageUrl")
    public String profileImageUrl;

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }

    public Tweet(JSONObject object){
        super();
        try {
            JSONObject user = object.getJSONObject("user");
            this.userId = user.getString("name");
            this.userHandle = "@" + user.getString("screen_name");
            this.timestamp = object.getString("created_at");
            this.body = object.getString("text");
            this.profileImageUrl = user.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }
}