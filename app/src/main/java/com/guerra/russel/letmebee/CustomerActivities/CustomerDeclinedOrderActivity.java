package com.guerra.russel.letmebee.CustomerActivities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.guerra.russel.letmebee.Adapters.OrdersAdapter;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class CustomerDeclinedOrderActivity extends AppCompatActivity {
    private static final String TAG = "CustomerDeclinedOrderAc";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    OrdersAdapter adapter;
    CollectionReference orderRef = db.collection("Orders");

    RecyclerView rv_orders;
    TextView tv_count;

    String email;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_declined_order);
        initialise();

        setUpAdapter();
    }

    private void setUpAdapter() {
        Query query = orderRef.whereEqualTo("status", 4)
                .whereEqualTo("by", email)
                .orderBy("dateDelivered", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter = new OrdersAdapter(options);
        rv_orders.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
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

    private void initialise() {
        getSupportActionBar().setTitle("Declined/Canceled Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            email = extra.getString("EMAIL");
        }
        rv_orders = findViewById(R.id.rv_orders);
        tv_count = findViewById(R.id.tv_count);
        rv_orders.setLayoutManager(new LinearLayoutManager(this));
        dialog = new Dialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        orderRef.whereEqualTo("status", 4)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_count.setText("Total: " + queryDocumentSnapshots.size());
                        Log.e(TAG, "onEvent: "+ queryDocumentSnapshots.size());
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
