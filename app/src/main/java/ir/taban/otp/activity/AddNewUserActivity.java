package ir.taban.otp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ir.taban.otp.api.User;
import ir.taban.otp.R;


public class AddNewUserActivity extends Activity {

    EditText et1, et2;
    Button button;
    TextView errorText;
    public static User greenUser;
    String account, pw;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_user_layout);

        et1 = (EditText) findViewById(R.id.accountEditor);
        et2 = (EditText) findViewById(R.id.keyEditor);
        button = (Button) findViewById(R.id.btnGo);
        //  errorText = (TextView) findViewById(R.id.errort);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (et1.getText().length() > 0 && et2.getText().length() > 0) {
                    try {
                        invoke_new_user();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void invoke_new_user() throws Exception {

        account = et1.getText().toString();
        pw = et2.getText().toString();
        greenUser = new User(account, pw);

        myThread();
    }


    public void myThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    greenUser.login();
                    MainActivity.users.add(greenUser);
                    AddNewUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // errorText.setText("Logged in :)");
                            Toast.makeText(getApplicationContext(), "User Added :)", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (final Exception e) {
                    AddNewUserActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //errorText.setText(e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            finish();

                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
