package com.guerra.russel.letmebee;

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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().setTitle("Reset Password");

        final TextView tv_forgotpassword = findViewById(R.id.tv_forgotpassword);
        final TextInputEditText et_email = findViewById(R.id.et_email);
        final Button bt_reset = findViewById(R.id.bt_reset);
        final ProgressBar pb = findViewById(R.id.pb);

        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_reset.setEnabled(false);
                final String email = et_email.getText().toString().trim();
                if (email.isEmpty() || !email.contains("@")) {
                    new DoToast(getApplicationContext(), "Please input an proper e-mail");
                } else {
                    pb.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pb.setVisibility(View.GONE);
                                    bt_reset.setEnabled(true);
                                    new DoToast(getApplicationContext(), "Reset password has been send to your e-mail");
                                    tv_forgotpassword.setText("E-mail sent. Please check your e-mail " +
                                            "to reset your password");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    new DoToast(getApplicationContext(), "Failed to send to e-mail");
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
