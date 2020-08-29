package com.guerra.russel.letmebee;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guerra.russel.letmebee.Collection.Users;

public class CreateNewAccountActivity extends AppCompatActivity {

    TextInputEditText et_email, et_password, et_repassword, et_firstname,
            et_lastname, et_address, et_phone;
    TextInputLayout txt2, txt3;

    Button bt_create;
    ProgressBar pb;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser fbUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference dbUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        initialise();

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = et_email.getText().toString().trim();
                final String password = et_password.getText().toString().trim();
                String repasswrd = et_repassword.getText().toString().trim();
                final String firstname = et_firstname.getText().toString().trim();
                final String lastname = et_lastname.getText().toString().trim();
                final String address = et_address.getText().toString().trim();
                final String phone = et_phone.getText().toString().trim();
                final int read = 0;
                final int pending = 0;
                final int approved = 0;

                if (email.isEmpty() || password.isEmpty() || repasswrd.isEmpty() ||
                        firstname.isEmpty() || lastname.isEmpty() ||
                        address.isEmpty() || phone.isEmpty()) {
                    new DoToast(getApplicationContext(), "All fields are required");
                } else {
                    if (password.length() < 6) {
                        new DoToast(getApplicationContext(), "Password needs to be 6 characters or longer");
                        txt2.setError("Password needs to be 6 characters or longer");
                    } else {
                        if (!password.equals(repasswrd)) {
                            new DoToast(getApplicationContext(), "Passwords do not match");
                            txt2.setError("Passwords do not match");
                            txt3.setError("Passwords do not match");
                        } else {
                            bt_create.setEnabled(false);
                            pb.setVisibility(View.VISIBLE);
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            auth.signInWithEmailAndPassword(email, password)
                                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            fbUser = auth.getCurrentUser();
                                                            String id = fbUser.getUid();
                                                            Users newUser = new Users(id, email, firstname,
                                                                    lastname, address, phone, read, pending, approved);
                                                            dbUsers = db.collection("Users").document(email);
                                                            dbUsers.set(newUser)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            pb.setVisibility(View.GONE);
                                                                            bt_create.setEnabled(true);
                                                                            new DoToast(getApplicationContext(), "Success");
                                                                            auth.signOut();
                                                                            finish();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            fbUser.delete()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            new DoToast(getApplicationContext(),
                                                                                                    "Failed to create account");
                                                                                            pb.setVisibility(View.GONE);
                                                                                            bt_create.setEnabled(true);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            new DoToast(getApplicationContext(), "Failed to create account");
                                                            pb.setVisibility(View.GONE);
                                                            bt_create.setEnabled(true);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            new DoToast(getApplicationContext(), "Failed to create account with e-mail or password");
                                            pb.setVisibility(View.GONE);
                                            bt_create.setEnabled(true);
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialise() {
        getSupportActionBar().setTitle("Create New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_repassword = findViewById(R.id.et_repassword);
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_address = findViewById(R.id.et_address);
        et_phone = findViewById(R.id.et_phone);
        bt_create = findViewById(R.id.bt_create);
        pb = findViewById(R.id.pb);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
    }
}
