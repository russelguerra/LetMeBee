package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.Collection.Sizes;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PendingOrderActivity extends AppCompatActivity {
    private static final String TAG = "PendingOrderActivity";

    TextView tv_id, tv_name, tv_quantity, tv_size, tv_price, tv_date, t3;
    Button bt_decline, bt_approve;
    ProgressBar pb, pb2;

    Dialog dialog;

    String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String id, dateOrdered, productName, productSize, productID;
    int quantity, counter;
    double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order);
        initialise();

        bt_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decline();
            }
        });

        bt_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approve();
            }
        });
    }

    private void approve() {
        dialog.setContentView(R.layout.layout_approve_order);
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
                bt_no.setEnabled(false);
                bt_yes.setEnabled(false);
                pb.setVisibility(View.VISIBLE);

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                String date = format.format(calendar.getTime());

                db.document("Orders/" + id)
                        .update("status", 1, "dateApproved", date)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.document("Users/" + email)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Users user = documentSnapshot.toObject(Users.class);
                                                int pending = user.getPending();
                                                int approved = user.getApproved();
                                                pending--;
                                                approved++;
                                                db.document("Users/" + email)
                                                        .update("approved", approved, "pending", pending)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                new DoToast(getApplicationContext(), "Order has been approved");
                                                                dialog.dismiss();
                                                                bt_no.setEnabled(true);
                                                                bt_yes.setEnabled(true);
                                                                pb.setVisibility(View.GONE);
                                                                finish();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new DoToast(getApplicationContext(), "Failed to approve order");
                                bt_no.setEnabled(true);
                                bt_yes.setEnabled(true);
                                pb.setVisibility(View.GONE);

                                Log.e(TAG, "onFailure: ", e);
                            }
                        });
            }
        });
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
                                                                                        int pending = user.getPending() - 1;
                                                                                        db.document("Users/" + email)
                                                                                                .update("pending", pending)
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
                                                                        new DoToast(getApplicationContext(),
                                                                                "Order has been declined");
                                                                        db.document("Users/" + email)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        Users user = documentSnapshot.toObject(Users.class);
                                                                                        int pending = user.getPending() - 1;
                                                                                        db.document("Users/" + email)
                                                                                                .update("pending", pending)
                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                        dialog.dismiss();
                                                                                                        bt_no.setEnabled(true);
                                                                                                        bt_yes.setEnabled(true);
                                                                                                        pb.setVisibility(View.GONE);
                                                                                                        finish();
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
        getSupportActionBar().setTitle("Order Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        id = extra.getString("ID");

        dialog = new Dialog(this);

        t3 = findViewById(R.id.t3);
        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_size = findViewById(R.id.tv_size);
        tv_price = findViewById(R.id.tv_price);
        tv_date = findViewById(R.id.tv_date);
        bt_decline = findViewById(R.id.bt_decline);
        bt_approve = findViewById(R.id.bt_approve);
        pb = findViewById(R.id.pb);
        pb2 = findViewById(R.id.pb2);

        db.document("Orders/" + id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Orders currOrder = documentSnapshot.toObject(Orders.class);
                        dateOrdered = currOrder.getDateOrdered();
                        productName = currOrder.getName();
                        productSize = currOrder.getSize();
                        quantity = currOrder.getQuantity();
                        totalPrice = currOrder.getPrice();
                        productID = currOrder.getId();
                        email = currOrder.getBy();

                        Log.e(TAG, "onSuccess: ID: " + id + " Date: " + dateOrdered + " Name: " + productName + " Size: " + productSize +
                                " Quantity: " + quantity + " Price: " + totalPrice + " ProductID: " + productID);

                        tv_id.setText(id);
                        tv_name.setText(productName);
                        tv_quantity.setText(String.valueOf(quantity));
                        tv_size.setText(productSize);
                        tv_date.setText(dateOrdered);
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
