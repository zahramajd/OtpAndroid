package ir.taban.otp.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import ir.taban.otp.api.User;
import ir.taban.otp.R;


public class FirstTimePage extends Activity {

    private Button btnGo;
    private EditText secretEditText;
    public static ArrayList<User> endUser;
    public static ArrayList<User> redUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page_layout);


        btnGo = (Button) findViewById(R.id.btnGo);
        secretEditText = (EditText) findViewById(R.id.secret);

        btnGo.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                FirstTimePage.redUser = endUser;
                Intent i = new Intent(FirstTimePage.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}


