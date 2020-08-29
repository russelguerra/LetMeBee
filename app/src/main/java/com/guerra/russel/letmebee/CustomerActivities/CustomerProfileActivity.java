package com.guerra.russel.letmebee.CustomerActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class CustomerProfileActivity extends AppCompatActivity {
    private static final String TAG = "CustomerProfileActivity";

    TextView tv_email, tv_name, tv_address, tv_phone;
    Button bt_update;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef;

    String email, firstname, lastname, address, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        initialise();

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent update = new Intent(CustomerProfileActivity.this, CustomerProfileUpdateActivity.class);
                update.putExtra("EMAIL", email);
                update.putExtra("FIRSTNAME", firstname);
                update.putExtra("LASTNAME", lastname);
                update.putExtra("ADDRESS", address);
                update.putExtra("PHONE", phone);
                startActivity(update);
            }
        });
    }

    private void initialise() {
        getSupportActionBar().setTitle("My Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_email = findViewById(R.id.tv_email);
        tv_name = findViewById(R.id.tv_name);
        tv_address = findViewById(R.id.tv_address);
        tv_phone = findViewById(R.id.tv_phone);
        bt_update = findViewById(R.id.bt_update);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            email = extra.getString("EMAIL");

        }

        userRef = db.document("Users/" + email);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Users user = documentSnapshot.toObject(Users.class);
                firstname = user.getFirstname();
                lastname = user.getLastname();
                address = user.getAddress();
                phone = user.getPhone();

                tv_email.setText(email);
                tv_name.setText(firstname + " " + lastname);
                tv_address.setText(address);
                tv_phone.setText(phone);
            }
        });
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
