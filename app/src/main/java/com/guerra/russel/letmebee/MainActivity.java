package com.guerra.russel.letmebee;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guerra.russel.letmebee.AdminActivities.AdminActivity;
import com.guerra.russel.letmebee.CustomerActivities.CustomerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    TextView tv_title;
    Button bt_login, bt_newaccount;
    Dialog dialog;

    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialise();

        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoToast(getApplicationContext(), "Developed by Russel Guerra");
            }
        });
    }

    private void initialise() {
        bt_login = findViewById(R.id.bt_login);
        bt_newaccount = findViewById(R.id.bt_newaccount);
        tv_title = findViewById(R.id.tv_title);

        bt_login.setOnClickListener(this);
        bt_newaccount.setOnClickListener(this);

        dialog = new Dialog(this);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        login(currentUser);
    }

    private void login(FirebaseUser currentUser) {
        if (currentUser != null) {
            if (!currentUser.getEmail().equals("russelguerra@gmail.com")) {
                startActivity(new Intent(MainActivity.this,
                        CustomerActivity.class));
                finish();
            } else {
                startActivity(new Intent(MainActivity.this,
                        AdminActivity.class));
                finish();
            }
        } else {
            new DoToast(getApplicationContext(), "Let Me Bee Your HoneyBee");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;

            case R.id.bt_newaccount:
                startActivity(new Intent(MainActivity.this, CreateNewAccountActivity.class));
                break;
        }
    }
}