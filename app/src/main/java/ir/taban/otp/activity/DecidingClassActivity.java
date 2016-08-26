package ir.taban.otp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ir.taban.otp.api.User;
import ir.taban.otp.R;


public class DecidingClassActivity extends Activity {

    // Shared Preferences
    SharedPreferences appSharedPrefs;

    //
    public ArrayList<User> users = new ArrayList();

    // this users
    public static ArrayList<User> cUsers = null;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.deciding_layout);

        loadUsers();

        if (users.size() > 0) {
            // go to MainActivity
            DecidingClassActivity.cUsers = users;
            Intent i = new Intent(DecidingClassActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            // go to LoginActivity
            Intent i = new Intent(DecidingClassActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void loadUsers() {
        users.clear();
        Gson gson = new Gson();
        String json;
        if (appSharedPrefs != null) {
            json = appSharedPrefs.getString("MyUsers", "");

            Type type = new TypeToken<ArrayList<User.Data>>() {}.getType();
            users = gson.fromJson(json, type);
        }
    }
}