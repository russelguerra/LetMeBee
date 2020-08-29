package com.guerra.russel.letmebee.CustomerActivities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;

public class CustomerApprovedActivity extends AppCompatActivity {
    private static final String TAG = "CustomerApprovedActivit";

    TextView tv_id, tv_date, tv_approvedDate, tv_name, tv_size, tv_quantity, tv_price, t3;

    Dialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef, orderRef;

    String id, dateOrdered, productName, productSize, productID, dateApproved, email;
    int quantity, counter;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_approved);
        initialise();
    }

    private void initialise() {
        getSupportActionBar().setTitle("Approved Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dialog = new Dialog(this);
        tv_id = findViewById(R.id.tv_id);
        tv_date = findViewById(R.id.tv_date);
        tv_approvedDate = findViewById(R.id.tv_approvedDate);
        tv_name = findViewById(R.id.tv_name);
        tv_size = findViewById(R.id.tv_size);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_price = findViewById(R.id.tv_price);
        t3 = findViewById(R.id.t3);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("ID");
            userRef = db.document("Users/" + email);
            orderRef = db.document("Orders/" + id);

            orderRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Orders currOrder = documentSnapshot.toObject(Orders.class);
                            dateOrdered = currOrder.getDateOrdered();
                            dateApproved = currOrder.getDateApproved();
                            productName = currOrder.getName();
                            productSize = currOrder.getSize();
                            quantity = currOrder.getQuantity();
                            totalPrice = currOrder.getPrice();
                            productID = currOrder.getId();
                            email = currOrder.getBy();

                            Log.e(TAG, "onSuccess: ID: " + id + " Date: " + dateOrdered + " Name: "
                                    + productName + " Size: " + productSize +
                                    " Quantity: " + quantity + " Price: " + totalPrice
                                    + " ProductID: " + productID);

                            tv_id.setText(id);
                            tv_name.setText(productName);
                            tv_quantity.setText(String.valueOf(quantity));
                            tv_size.setText(productSize);
                            tv_date.setText(dateOrdered);
                            tv_approvedDate.setText(dateApproved);
                            tv_price.setText(dateOrdered.valueOf(totalPrice));

                            if (productSize == null) {
                                tv_size.setVisibility(View.GONE);
                                t3.setVisibility(View.GONE);
                                counter = 0;
                            } else {
                                tv_size.setVisibility(View.VISIBLE);
                                t3.setVisibility(View.VISIBLE);
                                counter = 1;
                            }
                        }
                    });
        }
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
