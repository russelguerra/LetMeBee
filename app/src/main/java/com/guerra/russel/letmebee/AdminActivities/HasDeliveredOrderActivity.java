package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

public class HasDeliveredOrderActivity extends AppCompatActivity {
    private static final String TAG = "HasDeliveredOrderActivi";

    TextView tv_id, tv_date, tv_deliveredDate, tv_approvedDate, tv_name, tv_size, tv_quantity, tv_price, t3;
    Button bt_signature;

    Dialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef, orderRef, productRef;

    String id, dateOrdered, productName, productSize, productID, dateApproved, email, dateDelivered;
    int quantity, counter;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_has_delivered_order);
        initialise();

        bt_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.layout_signature);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                final ImageView iv = dialog.findViewById(R.id.iv);

                orderRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        Log.e(TAG, "onEvent: ID: " + id);
                        if (documentSnapshot != null && e == null) {
                            Orders order = documentSnapshot.toObject(Orders.class);
                            Picasso.get()
                                    .load(order.getSignature())
                                    .placeholder(R.drawable.ic_honey)
                                    .into(iv);
                        }
                    }
                });
            }
        });
    }

    private void initialise() {
        getSupportActionBar().setTitle("Delivered Order");
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
        tv_deliveredDate = findViewById(R.id.tv_deliveredDate);
        bt_signature = findViewById(R.id.bt_signature);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("ID");
            email = extra.getString("EMAIL");
            userRef = db.document("Users/" + email);
            orderRef = db.document("Orders/" + id);

            orderRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Orders currOrder = documentSnapshot.toObject(Orders.class);
                            dateOrdered = currOrder.getDateOrdered();
                            dateApproved = currOrder.getDateApproved();
                            dateDelivered = currOrder.getDateDelivered();
                            productName = currOrder.getName();
                            productSize = currOrder.getSize();
                            quantity = currOrder.getQuantity();
                            totalPrice = currOrder.getPrice();
                            productID = currOrder.getId();

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
                            tv_deliveredDate.setText(dateDelivered);
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
