package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.Collection.Sizes;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

public class ApprovedOrderActivity extends AppCompatActivity {
    private static final String TAG = "ApprovedOrderActivity";

    TextView tv_id, tv_date, tv_approvedDate, tv_name, tv_size, tv_quantity, tv_price, t3;
    Button bt_decline, bt_deliver;

    Dialog dialog;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef, orderRef;

    String id, dateOrdered, productName, productSize, productID, dateApproved, email;
    int quantity, counter;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_order);
        initialise();

        bt_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decline();
            }
        });

        bt_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliver();
            }
        });
    }

    private void deliver() {
        Intent deliver = new Intent(ApprovedOrderActivity.this, DeliverOrderActivity.class);
        deliver.putExtra("ID", id)
                .putExtra("EMAIL", email)
                .putExtra("COUNTER", counter)
                .putExtra("PRODUCTID", productID);
        startActivity(deliver);
    }

    private void decline() {
        dialog.setContentView(R.layout.layout_decline_order);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final Button bt_yes = dialog.findViewById(R.id.bt_yes),
                bt_no = dialog.findViewById(R.id.bt_no);
        final ProgressBar pb = dialog.findViewById(R.id.pb);

        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: Counter: " + counter);
                bt_no.setEnabled(false);
                bt_yes.setEnabled(false);
                pb.setVisibility(View.VISIBLE);

                db.document("Orders/" + id)
                        .update("status", 4)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (counter == 1) {
                                    db.document("Products/" + productName + "/Sizes/" + productSize)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot != null) {
                                                        Sizes s = documentSnapshot.toObject(Sizes.class);
                                                        int currentStock = s.getStock();
                                                        db.document("Products/" + productName + "/Sizes/" + productSize)
                                                                .update("stock", currentStock + quantity)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        db.document("Users/" + email)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        Users user = documentSnapshot.toObject(Users.class);
                                                                                        int pending = user.getApproved() - 1;
                                                                                        db.document("Users/" + email)
                                                                                                .update("approved", pending)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        dialog.dismiss();
                                                                                                        bt_no.setEnabled(true);
                                                                                                        bt_yes.setEnabled(true);
                                                                                                        pb.setVisibility(View.GONE);
                                                                                                        finish();
                                                                                                        new DoToast(getApplicationContext(),
                                                                                                                "Order has been declined");
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        new DoToast(getApplicationContext(), "Failed to decline order");
                                                                        Log.e(TAG, "onFailure: ", e);
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    new DoToast(getApplicationContext(), "Failed to do operation");
                                                    Log.e(TAG, "onFailure: ", e);
                                                }
                                            });
                                } else if (counter == 0) {
                                    db.document("Other Products/" + productID)
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot != null) {
                                                        OtherProducts s = documentSnapshot.toObject(OtherProducts.class);
                                                        int currentStock = s.getStock();
                                                        int orderCounter = s.getOrder() - 1;
                                                        db.document("Other Products/" + productID)
                                                                .update("stock", currentStock + quantity,
                                                                        "order", orderCounter)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        db.document("Users/" + email)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        Users user = documentSnapshot.toObject(Users.class);
                                                                                        int pending = user.getApproved() - 1;
                                                                                        db.document("Users/" + email)
                                                                                                .update("approved", pending)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        dialog.dismiss();
                                                                                                        bt_no.setEnabled(true);
                                                                                                        bt_yes.setEnabled(true);
                                                                                                        pb.setVisibility(View.GONE);
                                                                                                        finish();
                                                                                                        new DoToast(getApplicationContext(),
                                                                                                                "Order has been declined");
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        new DoToast(getApplicationContext(), "Failed to decline order");
                                                                        Log.e(TAG, "onFailure: ", e);
                                                                    }
                                                                });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    new DoToast(getApplicationContext(), "Failed to do operation");
                                                    Log.e(TAG, "onFailure: ", e);
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: ", e);
                            }
                        });
            }
        });
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
        bt_decline = findViewById(R.id.bt_decline);
        bt_deliver = findViewById(R.id.bt_deliver);
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
