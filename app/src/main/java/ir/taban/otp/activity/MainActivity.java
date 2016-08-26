package ir.taban.otp.activity;

import android.annotation.TargetApi;
import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import ir.taban.otp.api.User;
import ir.taban.otp.R;
import ir.taban.otp.ui.DividerItemDecoration;
import ir.taban.otp.ui.MainRecycleViewAdapter;

public class MainActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static ArrayList<User> allUsers = new ArrayList<>();
    public ArrayList<User> Dataset = new ArrayList<>();
    public static Timer timer;
    public SharedPreferences appSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        allUsers = LoginActivity.userArrayList;
        if (allUsers == null)
            allUsers = DecidingClassActivity.cUsers;
        Dataset = allUsers;

        storeUsers();

        mAdapter = new MainRecycleViewAdapter(Dataset);
        mRecyclerView.setAdapter(mAdapter);

        build_list();
        setClickHandlers();
        initUpdater();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void setClickHandlers() {
        LinearLayout add_layer = (LinearLayout) findViewById(R.id.addCircleView);
        add_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewUserActivity.class));
                finish();
            }
        });
    }

    public void initUpdater() {
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

    public void storeUsers() {
        ArrayList<User.Data> data = new ArrayList<>();
        for (User u : allUsers)
            data.add(u.toData());

        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        String json = new Gson().toJson(data);
        prefsEditor.putString("MyUsers", json);
        prefsEditor.apply();
    }

}

