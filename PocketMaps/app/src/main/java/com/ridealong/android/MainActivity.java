package com.ridealong.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.junjunguo.pocketmaps.R;
import com.junjunguo.pocketmaps.activities.PocketMainActivity;
import com.ridealong.android.ui.login.LoginActivity;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    /**
     *  Application starts here. Calls what used to be PocketMaps 'MainActivity' class.
     */
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent login = new Intent(this, LoginActivity.class);

        startActivityForResult(login, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent pocketmaps = new Intent(this, PocketMainActivity.class);
        startActivity(pocketmaps);
    }

}
