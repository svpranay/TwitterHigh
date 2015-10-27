package com.codepath.apps.twitterhigh.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.codepath.apps.twitterhigh.CircleTransform;
import com.codepath.apps.twitterhigh.R;
import com.codepath.apps.twitterhigh.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetsAdapter extends ArrayAdapter<Tweet> {

    public TweetsAdapter(Context context, List<Tweet> objects) {
        super(context, 0, objects);
    }

    private static class ViewHolder {
        TextView twitterHandle;
        TextView name;
        ImageView profileImage;
        TextView tweetMessage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Tweet tweet = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.tweet_item, parent, false);
            viewHolder.profileImage = (ImageView)convertView.findViewById(R.id.ivProfileImage);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.twitterHandle = (TextView) convertView.findViewById(R.id.tvHandle);
            viewHolder.tweetMessage = (TextView) convertView.findViewById(R.id.etTweetMessage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate data into the template view using the data object
        viewHolder.twitterHandle.setText(tweet.userHandle);
        viewHolder.tweetMessage.setText(tweet.body);
        viewHolder.name.setText(tweet.userId);
        Picasso.with(getContext()).load(Uri.parse(tweet.profileImageUrl)).placeholder(R.drawable.girl_writing).transform(new CircleTransform()).into(viewHolder.profileImage);
        // Return the completed view to render on screen
        return convertView;
    }
}
