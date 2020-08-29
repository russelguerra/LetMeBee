package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.guerra.russel.letmebee.Adapters.OrdersAdapter;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    TextView tv_name, tv_email, tv_address, tv_phone, tv_declinedorder, tv_approvedorder, tv_pendingorder, tv_deliveredorder;
    CardView c2;
    RecyclerView rv_pendingorder, rv_approvedorder, rv_recentorder, rv_declinedorder;
    Button bt_order;

    String firstname, lastname, email, name, address, phone;
    int counter;

    Dialog dialog;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderRef = db.collection("Orders");

    OrdersAdapter adapter, adapter2, adapter3, adapter4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initialise();

        setUpPending();
        setUpApproved();
        setUpDelivered();
        setUpDeclined();

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });
    }

    private void setUpDeclined() {
        Query query = orderRef.whereEqualTo("status", 4)
                .whereEqualTo("by", email)
                .orderBy("dateOrdered", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter4 = new OrdersAdapter(options);
        rv_declinedorder.setAdapter(adapter4);

        adapter4.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String name, size, dateOrdered, dateApproved, id;
                int quantity;
                double price;
                Orders order = documentSnapshot.toObject(Orders.class);
                id = order.getId();
                name = order.getName();
                size = order.getSize();
                dateOrdered = order.getDateOrdered();
                dateApproved = order.getDateApproved();

                if (dateApproved == null) {
                    dateApproved = "Not yet approved";
                }

                quantity = order.getQuantity();
                price = order.getPrice();

                dialog.setContentView(R.layout.layout_canceled_order);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                TextView tv_id, tv_dateOrdered, tv_dateApproved, tv_name, tv_size, tv_quantity, tv_price, t3;

                tv_dateApproved = dialog.findViewById(R.id.tv_dateApproved);
                tv_id = dialog.findViewById(R.id.tv_id);
                tv_dateOrdered = dialog.findViewById(R.id.tv_dateOrdered);
                tv_name = dialog.findViewById(R.id.tv_name);
                tv_size = dialog.findViewById(R.id.tv_size);
                tv_quantity = dialog.findViewById(R.id.tv_quantity);
                tv_price = dialog.findViewById(R.id.tv_price);
                t3 = dialog.findViewById(R.id.t4);

                if (size == null) {
                    tv_size.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                } else {
                    t3.setVisibility(View.VISIBLE);
                    tv_size.setVisibility(View.VISIBLE);
                    tv_size.setText(size);
                }

                tv_dateApproved.setText(dateApproved);
                tv_dateOrdered.setText(dateOrdered);
                tv_id.setText(id);
                tv_name.setText(name);
                tv_price.setText(String.valueOf(price));
                tv_quantity.setText(String.valueOf(quantity));
            }
        });


    }

    private void setUpDelivered() {
        Query query = orderRef.whereEqualTo("status", 2)
                .whereEqualTo("by", email)
                .orderBy("dateDelivered", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter3 = new OrdersAdapter(options);
        rv_recentorder.setAdapter(adapter3);

        adapter3.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Orders pending = documentSnapshot.toObject(Orders.class);
                String id = adapter3.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(ProfileActivity.this, HasDeliveredOrderActivity.class);
                product.putExtra("ID", id);
                product.putExtra("DATE", pending.getDateOrdered());
                product.putExtra("NAME", pending.getName());
                product.putExtra("SIZE", pending.getSize());
                product.putExtra("QUANTITY", pending.getQuantity());
                product.putExtra("PRICE", pending.getPrice());
                product.putExtra("DELIVERED", pending.getDateDelivered());
                product.putExtra("APPROVED", pending.getDateApproved());
                product.putExtra("SIGNATURE", pending.getSignature());
                product.putExtra("EMAIL", email);
                startActivity(product);
            }
        });
    }

    private void setUpApproved() {
        Query query = orderRef.whereEqualTo("status", 1)
                .whereEqualTo("by", email)
                .orderBy("dateApproved", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter2 = new OrdersAdapter(options);
        rv_approvedorder.setAdapter(adapter2);

        adapter2.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot != null) {
                    String id = adapter2.getSnapshots().getSnapshot(position).getId();
                    Intent product = new Intent(ProfileActivity.this, ApprovedOrderActivity.class);
                    product.putExtra("ID", id);
                    product.putExtra("EMAIL", email);
                    startActivity(product);
                }
            }
        });
    }

    private void setUpPending() {
        Query query = orderRef.whereEqualTo("status", 0)
                .whereEqualTo("by", email)
                .orderBy("dateOrdered", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter = new OrdersAdapter(options);
        rv_pendingorder.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Orders order = documentSnapshot.toObject(Orders.class);
                String id = adapter.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(ProfileActivity.this, PendingOrderActivity.class);
                product.putExtra("ID", id);
                product.putExtra("EMAIL", email);
                startActivity(product);
            }
        });
    }

    private void initialise() {
        getSupportActionBar().setTitle("Customer Information");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            email = extra.getString("EMAIL");
            name = extra.getString("NAME");
            address = extra.getString("ADDRESS");
            phone = extra.getString("PHONE");
            counter = extra.getInt("COUNTER");
        }

        dialog = new Dialog(this);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_address = findViewById(R.id.tv_address);
        tv_phone = findViewById(R.id.tv_phone);
        c2 = findViewById(R.id.c2);

        tv_pendingorder = findViewById(R.id.tv_pendingorder);
        tv_approvedorder = findViewById(R.id.tv_approvedorder);
        tv_deliveredorder = findViewById(R.id.tv_deliveredorder);
        tv_declinedorder = findViewById(R.id.tv_declinedorder);

        rv_pendingorder = findViewById(R.id.rv_pendingorder);
        rv_approvedorder = findViewById(R.id.rv_approvedorder);
        rv_recentorder = findViewById(R.id.rv_recentorder);
        rv_declinedorder = findViewById(R.id.rv_declinedorder);
        bt_order = findViewById(R.id.bt_order);

        tv_name.setText(name);
        tv_address.setText(address);
        tv_email.setText(email);
        tv_phone.setText(phone);

        rv_pendingorder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_declinedorder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_approvedorder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_recentorder.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (counter == 10) {
            c2.setVisibility(View.GONE);
        }
    }

    private void messageUser() {
        db.document("Users/" + email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Users user = documentSnapshot.toObject(Users.class);
                        firstname = user.getFirstname();
                        lastname = user.getLastname();
                        Intent message = new Intent(ProfileActivity.this, ChatActivity.class);
                        message.putExtra("EMAIL", email)
                                .putExtra("FIRSTNAME", firstname)
                                .putExtra("LASTNAME", lastname);
                        startActivity(message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.message:
                messageUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_send_message, menu);
        return true;
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

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
        adapter2.startListening();
        adapter3.startListening();
        adapter4.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
        adapter3.startListening();
        adapter4.startListening();

        orderRef.whereEqualTo("status", 0)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_pendingorder.setText("Pending Order: " + queryDocumentSnapshots.size());
                    }
                });

        orderRef.whereEqualTo("status", 1)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_approvedorder.setText("Approved Order: " + queryDocumentSnapshots.size());
                    }
                });

        orderRef.whereEqualTo("status", 2)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_deliveredorder.setText("Delivered Order: " + queryDocumentSnapshots.size());
                    }
                });

        orderRef.whereEqualTo("status", 4)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_declinedorder.setText("Declined/Canceled Order: " + queryDocumentSnapshots.size());
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
        adapter3.stopListening();
        adapter4.stopListening();
    }
}
