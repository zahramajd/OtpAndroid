package ir.taban.otp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        instance=this;

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
            startActivityForResult(i,0);
        }

        // Initialize UI
        build_list();
        setClickHandlers();
        initUpdater();
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
        if(user==null)
            return;

        // Check if user already exists
        for(User u:users)
            if(u.getEmail().equals(user.getEmail()))
                return;

        // Add User
        users.add(user);

        // Notify Recycler view
        mAdapter.notifyDataSetChanged();

        // Store users
        storeUsers();

    }

    private void setClickHandlers() {
        LinearLayout add_layer = (LinearLayout) findViewById(R.id.addCircleView);
        add_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, AddNewUserActivity.class);
                startActivityForResult(i,0);
            }
        });
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
        json = appSharedPrefs.getString("MyUsers",null);

        if(json!=null) {
            Type type = new TypeToken<ArrayList<User.Data>>() {
            }.getType();
            List<User.Data> data=gson.fromJson(json,type);
            if(data.size()>0){
                users.clear();
                for (User.Data d:data){
                    users.add(new User(d));
                }
                // We manually updated users array, so call notify
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}

