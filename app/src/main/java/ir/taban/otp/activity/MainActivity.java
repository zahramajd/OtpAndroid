package ir.taban.otp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import ir.taban.otp.api.User;
import ir.taban.otp.R;
import ir.taban.otp.ui.DividerItemDecoration;
import ir.taban.otp.ui.MainRecycleViewAdapter;

public class MainActivity extends Activity {

    private static MainActivity instance;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Timer timer;
    private ArrayList<User> users = new ArrayList<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static long dif=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        instance = this;

        // Disable to rotate screen
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // Init Adapters
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mAdapter = new MainRecycleViewAdapter(users);
        mRecyclerView.setAdapter(mAdapter);

        // Load Saved Users
        loadUsers();

        if (users.size() == 0) {
            // If no users are defined, show a login activity
            // go to LoginActivity
            Intent i = new Intent(this, LoginActivity.class);
            startActivityForResult(i, 0);
        }

        // Initialize UI
        build_list();
        //connect();
        setClickHandlers();
        setSyncClickHandlers();
        initUpdater();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        storeUsers();
    }

    public void addUser(User user) {
        if (user == null)
            return;

        // Check if user already exists
        for (User u : users)
            if (u.getEmail().equals(user.getEmail()))
                return;

        // Add User
        users.add(user);

        // Notify Recycler view
        mAdapter.notifyDataSetChanged();

        // Store users
        storeUsers();

    }

    private void setClickHandlers() {
        //  LinearLayout add_layer = (LinearLayout) findViewById(R.id.addCircleView);
        Button add_btn = (Button) findViewById(R.id.add_user_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_connect();
            }
        });
    }

    private void setSyncClickHandlers() {
        Button sync_btn = (Button) findViewById(R.id.sync_btn);
        sync_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sync_connect();
            }
        });
    }

    public void add_connect() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        boolean isWifi = manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        Log.v("", is3g + " ConnectivityManager Test " + isWifi);
        if (!is3g && !isWifi) {
            Toast.makeText(getApplicationContext(),
                    "Please make sure, your network connection is ON ",
                    Toast.LENGTH_LONG).show();
        } else {
            Intent i = new Intent(MainActivity.this, AddNewUserActivity.class);
            startActivityForResult(i, 0);
        }
    }


    public void sync_connect() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean is3g = manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        boolean isWifi = manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();

        Log.v("", is3g + " ConnectivityManager Test " + isWifi);
        if (!is3g && !isWifi) {
            Toast.makeText(getApplicationContext(),
                    "Please make sure, your network connection is ON ",
                    Toast.LENGTH_LONG).show();
        } else {
            syncServer();
        }
    }

    public void syncServer() {

        long server_time = getServerTime();
        long client_time = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis() / 1000;

        dif = client_time - server_time;

        Toast.makeText(getApplicationContext(),
                "App synced",
                Toast.LENGTH_LONG).show();
        return;

    }

    public long getServerTime(){

        // TODO : get time from server
        return 1472295374;
    }

    private void initUpdater() {
        // update timer
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }, 0, 1000);
    }

    private void build_list() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    private void storeUsers() {
        ArrayList<User.Data> data = new ArrayList<>();
        for (User u : users)
            data.add(u.toData());
        SharedPreferences appSharedPrefs;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        String json = new Gson().toJson(data);
        prefsEditor.putString("MyUsers", json);
        prefsEditor.apply();
    }


    private void loadUsers() {
        Gson gson = new Gson();
        String json;
        SharedPreferences appSharedPrefs;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        json = appSharedPrefs.getString("MyUsers", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<User.Data>>() {
            }.getType();
            List<User.Data> data = gson.fromJson(json, type);
            if (data.size() > 0) {
                users.clear();
                for (User.Data d : data) {
                    users.add(new User(d));
                }
                // We manually updated users array, so call notify
                mAdapter.notifyDataSetChanged();
            }
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://ir.taban.otp.activity/http/host/path")
//        );
////        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app URL is correct.
//                Uri.parse("android-app://ir.taban.otp.activity/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
//    }
}

