package com.guerra.russel.letmebee.CustomerActivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

public class CustomerProfileUpdateActivity extends AppCompatActivity {
    private static final String TAG = "CustomerProfileUpdateAc";

    TextInputEditText et_firstname, et_lastname, et_address, et_phone;
    Button bt_update;
    ProgressBar pb;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef;

    String email, firstname, lastname, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_update);
        initialise();

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_firstname.getText().toString().trim().isEmpty() ||
                        et_lastname.getText().toString().trim().isEmpty() ||
                        et_address.getText().toString().trim().isEmpty() ||
                        et_phone.getText().toString().trim().isEmpty()) {
                    new DoToast(getApplicationContext(),
                            "Please make sure all fields are not empty");
                } else {
                    bt_update.setEnabled(false);
                    pb.setVisibility(View.VISIBLE);

                    firstname = et_firstname.getText().toString().trim();
                    lastname = et_lastname.getText().toString().trim();
                    address = et_address.getText().toString().trim();
                    phone = et_phone.getText().toString().trim();

                    userRef.update("firstname", firstname,
                            "lastname", lastname,
                            "address", address,
                            "phone", phone)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pb.setVisibility(View.VISIBLE);
                                    bt_update.setEnabled(true);

                                    new DoToast(getApplicationContext(),
                                            "Information successfully updated");

                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pb.setVisibility(View.VISIBLE);
                                    bt_update.setEnabled(true);
                                    new DoToast(getApplicationContext(),
                                            "Failed to update information");
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }

            }
        });
    }

    private void initialise() {
        getSupportActionBar().setTitle("Update Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_address = findViewById(R.id.et_address);
        et_phone = findViewById(R.id.et_phone);
        bt_update = findViewById(R.id.bt_update);
        pb = findViewById(R.id.pb);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            email = extra.getString("EMAIL");
            firstname = extra.getString("FIRSTNAME");
            lastname = extra.getString("LASTNAME");
            address = extra.getString("ADDRESS");
            phone = extra.getString("PHONE");
        }

        et_firstname.setText(firstname);
        et_lastname.setText(lastname);
        et_address.setText(address);
        et_phone.setText(phone);

        userRef = db.document("Users/" + email);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
