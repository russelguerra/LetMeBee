package com.guerra.russel.letmebee.CustomerActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.guerra.russel.letmebee.AdminActivities.HasDeliveredOrderActivity;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class CustomerDeliveredOrderActivity extends AppCompatActivity {
    private static final String TAG = "CustomerDeliveredOrderA";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    OrdersAdapter adapter;
    CollectionReference userRef = db.collection("Orders");

    RecyclerView rv_orders;
    TextView tv_count;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_delivered_order);
        initialise();

        setUpAdapter();
    }

    private void setUpAdapter() {
        Query query = userRef.whereEqualTo("status", 2)
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
                Orders pending = documentSnapshot.toObject(Orders.class);
                String id = adapter.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(CustomerDeliveredOrderActivity.this,
                        HasDeliveredOrderActivity.class);
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

    private void initialise() {
        getSupportActionBar().setTitle("Delivered Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            email = extra.getString("EMAIL");
        }
        rv_orders = findViewById(R.id.rv_orders);
        tv_count = findViewById(R.id.tv_count);
        rv_orders.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        userRef.whereEqualTo("status", 2)
                .whereEqualTo("by", email)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        tv_count.setText("Total: " + queryDocumentSnapshots.size());
                        Log.e(TAG, "onEvent: " + queryDocumentSnapshots.size());
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
