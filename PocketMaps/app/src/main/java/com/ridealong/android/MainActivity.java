package com.ridealong.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.junjunguo.pocketmaps.R;
import com.junjunguo.pocketmaps.activities.PocketMainActivity;

public class MainActivity extends AppCompatActivity {

    /**
     *  Application starts here. Calls what used to be PocketMaps 'MainActivity' class.
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PocketMainActivity activity = new PocketMainActivity();
    }

}
