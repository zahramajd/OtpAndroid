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


public class LoginActivity extends Activity {

    private Button btnLogin, btnSignUp;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private TextView errorText;
    public static ArrayList<User> currentUser = null;
    public static User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        //  errorText = (TextView) findViewById(R.id.error_txt);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (inputEmail.getText().toString().length() > 0 && inputPassword.getText().toString().length() > 0) {
                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();

                    user = new User(email, password);
                    // errorText.setText("Logging in ...");
                    Toast.makeText(getApplicationContext(), "Logging in ...", Toast.LENGTH_SHORT).show();
                    myThread();
                }
            }
        });
    }


    public void myThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    user.login();

                    MainActivity.users.add(user);

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // errorText.setText("Logged in :)");
                            Toast.makeText(getApplicationContext(), "Logged in :)", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

                } catch (final Exception e) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //        errorText.setText(e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            //Stay on Login Activity
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

