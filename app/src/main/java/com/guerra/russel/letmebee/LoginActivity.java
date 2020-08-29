package com.guerra.russel.letmebee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guerra.russel.letmebee.AdminActivities.AdminActivity;
import com.guerra.russel.letmebee.CustomerActivities.CustomerActivity;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        final TextInputEditText et_email, et_password;
        final Button bt_login;
        final TextView tv_forgotpassword;

        tv_forgotpassword = findViewById(R.id.tv_forgotpassword);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        final ProgressBar pb = findViewById(R.id.pb);

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_login.setEnabled(false);
                final String email = et_email.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    new DoToast(getApplicationContext(), "Please fill all fields");
                } else {
                    pb.setVisibility(View.VISIBLE);

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    pb.setVisibility(View.GONE);
                                    bt_login.setEnabled(true);
                                    currentUser = auth.getCurrentUser();
                                    login(currentUser);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    new DoToast(getApplicationContext(), "Incorrect e-mail or password");
                                    pb.setVisibility(View.GONE);
                                    bt_login.setEnabled(true);
                                }
                            });
                }
            }
        });

        tv_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void login(FirebaseUser currentUser) {
        if (currentUser != null) {
            if (!currentUser.getEmail().equals("russelguerra@gmail.com")) {
                startActivity(new Intent(LoginActivity.this,
                        CustomerActivity.class));
                finish();
            } else {
                startActivity(new Intent(LoginActivity.this,
                        AdminActivity.class));
                finish();
            }
        } else {
            new DoToast(getApplicationContext(), "Let me Bee your Honeybee");
        }
    }
}
