package com.junjunguo.pocketmaps.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.junjunguo.pocketmaps.R;
import com.junjunguo.pocketmaps.model.map.DownloadFiles;
import com.junjunguo.pocketmaps.model.util.MapDownloadListener;
import com.junjunguo.pocketmaps.model.util.MyApp;
import com.junjunguo.pocketmaps.model.util.MyMap;
import com.junjunguo.pocketmaps.model.util.MyMapAdapter;
import com.junjunguo.pocketmaps.model.util.RVItemTouchListener;
import com.junjunguo.pocketmaps.model.util.SetStatusBarColor;
import com.junjunguo.pocketmaps.model.util.Variable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                   MapDownloadListener {
    private GoogleApiClient mGoogleApiClient;
    private MyMapAdapter mapAdapter;
    private Location mLastLocation;
    /**
     * used to show or hide item action
     */
    private View vh;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Variable.getVariable().setContext(getApplicationContext());
        // set status bar
        new SetStatusBarColor().setStatusBarColor(findViewById(R.id.statusBarBackgroundMain),
                getResources().getColor(R.color.my_primary_dark), this);
        buildGoogleApiClient();

        //         greater Or Equal to Kitkat
        if (Build.VERSION.SDK_INT >= 19) {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(this, "Pocket Maps is not usable without an external storage!", Toast.LENGTH_SHORT)
                        .show();
                MyApp.tracker().send(new HitBuilders.ExceptionBuilder()
                        .setDescription("Pocket Maps is not usable without an external storage!").setFatal(false)
                        .build());
                return;
            }
            Variable.getVariable().setMapsFolder(
                    new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            Variable.getVariable().getMapDirectory()));
        } else Variable.getVariable().setMapsFolder(
                new File(Environment.getExternalStorageDirectory(), Variable.getVariable().getMapDirectory()));

        if (!Variable.getVariable().getMapsFolder().exists()) Variable.getVariable().getMapsFolder().mkdirs();
        activeAddBtn();
        activeRecyclerView(new ArrayList());
        generateList();
        DownloadFiles.getDownloader().addListener(this);
        vh = null;
        // start map activity if load succeed
        if (Variable.getVariable().loadVariables()) {
            //            startMapActivity();
        }
        Log.d(tag, "In the onCreate() event");

    }

    /**
     * add button will move user to download activity
     */
    private void activeAddBtn() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.my_maps_add_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startDownloadActivity();
            }
        });
    }


    @Override public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override public void onConnectionSuspended(int i) {
    }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    /**
     * choose area form local files
     */
    private void generateList() {
        if (Variable.getVariable().getLocalMaps().isEmpty()) {
            refreshList();
        } else {
            mapAdapter.addAll(Variable.getVariable().getLocalMaps());
        }
    }

    /**
     * read local files and build a list then add the list to mapAdapter
     */
    private void refreshList() {
        String[] files = Variable.getVariable().getMapsFolder().list(new FilenameFilter() {
            @Override public boolean accept(File dir, String filename) {
                return (filename != null && (filename.endsWith(".ghz") || filename.endsWith("-gh")));
            }
        });
        for (String file : files) {
            Variable.getVariable().addLocalMap(new MyMap(file));
        }
        if (!Variable.getVariable().getLocalMaps().isEmpty()) {
            mapAdapter.addAll(Variable.getVariable().getLocalMaps());
        }
    }


    /**
     * active directions, and directions view
     */
    private void activeRecyclerView(List myMaps) {
        RecyclerView mapsRV;
        RecyclerView.LayoutManager layoutManager;

        mapsRV = (RecyclerView) findViewById(R.id.my_maps_recycler_view);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setRemoveDuration(1000);
        mapsRV.setItemAnimator(animator);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mapsRV.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        mapsRV.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mapAdapter = new MyMapAdapter(myMaps);
        mapsRV.setAdapter(mapAdapter);
        onItemTouchHandler(mapsRV);
    }

    /**
     * perform actions when item touched
     *
     * @param mapsRV
     */
    private void onItemTouchHandler(RecyclerView mapsRV) {
        mapsRV.addOnItemTouchListener(new RVItemTouchListener(new RVItemTouchListener.OnItemTouchListener() {
            public boolean onItemTouch(View view, int position, MotionEvent e) {
                if (view != vh) {
                    switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            view.setBackgroundColor(getResources().getColor(R.color.my_primary_light));
                            return true;
                        case MotionEvent.ACTION_UP:
                            view.setBackgroundColor(getResources().getColor(R.color.my_icons));
                            itemActions(view, position);
                            return true;
                    }
                }
                return false;
            }
        }));
    }


    /**
     * item is clicked actions : a new item layout remove, cancel, OK
     *
     * @param view
     * @param position
     */
    private void itemActions(View view, final int position) {
        if (vh == view) {

        } else {
            if (vh != null) {
                ViewGroup item = (ViewGroup) vh.findViewById(R.id.my_maps_item_rl);
                ViewGroup action = (ViewGroup) vh.findViewById(R.id.my_maps_item_action_rl);
                item.setVisibility(View.INVISIBLE);
                action.setVisibility(View.VISIBLE);
            }
            vh = view;
            view.setClickable(false);
            final ViewGroup item = (ViewGroup) view.findViewById(R.id.my_maps_item_rl);
            final ViewGroup action = (ViewGroup) view.findViewById(R.id.my_maps_item_action_rl);
            item.setVisibility(View.INVISIBLE);
            action.setVisibility(View.VISIBLE);
            Button remove = (Button) view.findViewById(R.id.my_maps_item_action_remove_btn);
            Button cancel = (Button) view.findViewById(R.id.my_maps_item_action_cancel_btn);
            Button ok = (Button) view.findViewById(R.id.my_maps_item_action_ok_btn);

            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // delete map
                    MyMap mm = mapAdapter.remove(position);  // remove from adapter
                    Variable.getVariable().removeLocalMap(mm);
                    recursiveDelete(new File(mm.getUrl()));
                    resetVH(item, action);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resetVH(item, action);
                }
            });
            ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resetVH(item, action);
                    // load map
                    Variable.getVariable().setPrepareInProgress(true);
                    log(mapAdapter.getItem(position).getMapName());
                    Variable.getVariable().setCountry(mapAdapter.getItem(position).getMapName());
                    startMapActivity();
                }
            });
        }
    }

    /**
     * delete a recursively delete a folder or file
     *
     * @param fileOrDirectory
     */
    public void recursiveDelete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) for (File child : fileOrDirectory.listFiles())
            recursiveDelete(child);
        try {
            fileOrDirectory.delete();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    /**
     * reset: item visible; action invisible; vh = null;
     *
     * @param item
     * @param action
     */
    private void resetVH(ViewGroup item, ViewGroup action) {
        vh = null;
        item.setVisibility(View.VISIBLE);
        action.setVisibility(View.INVISIBLE);
    }

    /**
     * move to download activity
     */
    private void startDownloadActivity() {
        if (isOnline()) {
            Intent intent = new Intent(this, DownloadMapActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Add new Map need internet connection!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * move to map screen
     */
    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * accessing google play services
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient =
                new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                //                got to setting;
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_quit:
                quitApp();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onResume() {
        super.onResume();
        addRecentDownloadedFiles();
        Log.d(tag, "In the onResume() event");
    }

    /**
     * finish all activities ( quit the app )
     */
    private void quitApp() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("ACTION_QUIT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        finish();
        System.exit(0);
    }

    /**
     * @return true is there is a network connection
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * send message to logcat
     *
     * @param str
     */
    private void log(String str) {
        Log.i(this.getClass().getSimpleName(), "---------- main activity ----------" + str);
        logToast(str);
    }

    /**
     * send message to logcat and Toast it on screen
     *
     * @param str: message
     */
    private void logToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public void downloadStart() {
    }

    public void downloadFinished() {
        log("add recent downloaded files called from Main implement download finished!");
        addRecentDownloadedFiles();
    }

    public void progressBarOnupdate() {

    }

    /**
     * add recent downloaded files from Download activity(if any)
     */
    private void addRecentDownloadedFiles() {
        try {
            log("add recent downloaded files in try!");
            for (int i = Variable.getVariable().getRecentDownloadedMaps().size() - 1; i >= 0; i--) {
                MyMap mm = Variable.getVariable().removeRecentDownloadedMap(i);
                mapAdapter.insert(new MyMap(mm.getMapName()));
                Variable.getVariable().addLocalMap(mm);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    String tag = "LifeCycleEvents =================== Main ======= = =";

    public void onStart() {
        super.onStart();
        Log.d(tag, "In the onStart() event");
    }

    public void onRestart() {
        super.onRestart();
        Log.d(tag, "In the onRestart() event");
    }

    //    public void onResume()
    //    {
    //        super.onResume();
    //        Log.d(tag, "In the onResume() event");
    //    }
    public void onPause() {
        super.onPause();
        Log.d(tag, "In the onPause() event");
    }

    public void onStop() {
        super.onStop();
        Log.d(tag, "In the onStop() event");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(tag, "In the onDestroy() event");
    }

}