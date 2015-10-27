package com.codepath.apps.twitterhigh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ComposeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String screenName = intent.getStringExtra("screen_name");
        String profileImageUrl = intent.getStringExtra("profile_image_url");
        TextView tv = (TextView) findViewById(R.id.tvUserHandle);
        tv.setText(screenName);
        tv = (TextView) findViewById(R.id.tvUserName);
        tv.setText(name);
        ImageView iv = (ImageView) findViewById(R.id.ivProfilePhoto);
        Picasso.with(getApplicationContext()).load(Uri.parse(profileImageUrl)).placeholder(R.drawable.girl_writing).into(iv);


        EditText et = (EditText) findViewById(R.id.etTweetMessage);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EditText tweetMessage = (EditText) findViewById(R.id.etTweetMessage);
                TextView charCount = (TextView) findViewById(R.id.tvCharCount);
                charCount.setText(String.valueOf(tweetMessage.getText().length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.compose_action_bar, menu);
        return true;
    }

    public void tweetMessage(MenuItem item) {
        EditText etMessage = (EditText) findViewById(R.id.etTweetMessage);
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("tweetMessage", etMessage.getText().toString());
        data.putExtra("code", 200);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
