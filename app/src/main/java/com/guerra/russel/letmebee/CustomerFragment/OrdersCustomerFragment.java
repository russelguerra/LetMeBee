package com.guerra.russel.letmebee.CustomerFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.guerra.russel.letmebee.CustomerActivities.CustomerApprovedActivity;
import com.guerra.russel.letmebee.CustomerActivities.CustomerDeclinedOrderActivity;
import com.guerra.russel.letmebee.CustomerActivities.CustomerDeliveredOrderActivity;
import com.guerra.russel.letmebee.CustomerActivities.CustomerPendingOrderActivity;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class OrdersCustomerFragment extends Fragment {
    private static final String TAG = "OrdersCustomerFragment";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    CollectionReference orderRef;

    Button bt_delivered, bt_declined;
    TextView tv_pendingorder, tv_approvedorder;

    RecyclerView rv_pendingorder, rv_approvedorder, rv_deliveredorder;
    OrdersAdapter adapter, adapter2, adapter3;

    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders_customer, container, false);
        initialise(v);
        setUpPending();
        setUpApproved();
        setUpDelivered();

        bt_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerDeliveredOrderActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }
        });

        bt_declined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerDeclinedOrderActivity.class);
                intent.putExtra("EMAIL", email);
                startActivity(intent);
            }
        });

        return v;
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
                Intent product = new Intent(getActivity(), CustomerPendingOrderActivity.class);
                product.putExtra("ID", id);
                product.putExtra("DATE", order.getDateOrdered());
                product.putExtra("NAME", order.getName());
                product.putExtra("SIZE", order.getSize());
                product.putExtra("QUANTITY", order.getQuantity());
                product.putExtra("PRICE", order.getPrice());
                product.putExtra("EMAIL", email);
                startActivity(product);
            }
        });
    }

    private void setUpApproved() {
        CollectionReference userRef = db.collection("Users/" + email + "/Orders");
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
                Orders order = documentSnapshot.toObject(Orders.class);
                String id = adapter2.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(getActivity(), CustomerApprovedActivity.class);
                product.putExtra("ID", id);
                startActivity(product);
            }
        });
    }

    private void setUpDelivered() {
        Query query = orderRef.whereEqualTo("status", 2)
                .whereEqualTo("by", email)
                .orderBy("dateDelivered", Query.Direction.DESCENDING)
                .limit(3);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter3 = new OrdersAdapter(options);
        rv_deliveredorder.setAdapter(adapter3);

        adapter3.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Orders pending = documentSnapshot.toObject(Orders.class);
                String id = adapter3.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(getActivity(),
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

    private void initialise(View v) {
        getActivity().setTitle("My Orders");
        setHasOptionsMenu(true);

        email = currUser.getEmail();

        rv_pendingorder = v.findViewById(R.id.rv_pendingorder);
        rv_approvedorder = v.findViewById(R.id.rv_approvedorder);
        rv_deliveredorder = v.findViewById(R.id.rv_deliveredorder);
        bt_delivered = v.findViewById(R.id.bt_delivered);
        bt_declined = v.findViewById(R.id.bt_declined);
        tv_pendingorder = v.findViewById(R.id.tv_pendingorder);
        tv_approvedorder = v.findViewById(R.id.tv_approvedorder);

        rv_pendingorder.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_approvedorder.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_deliveredorder.setLayoutManager(new LinearLayoutManager(getContext()));

        orderRef = db.collection("Orders");
        Log.e(TAG, "initialise: Email: " + email);
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
        adapter3.startListening();

        orderRef.whereEqualTo("status", 0)
                .whereEqualTo("by", email)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            tv_pendingorder.setText("Pending Order: " + queryDocumentSnapshots.size());
                        }
                    }
                });

        orderRef.whereEqualTo("status", 1)
                .whereEqualTo("by", email)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            tv_approvedorder.setText("Approved Order: " + queryDocumentSnapshots.size());
                        }
                    }
                });

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        adapter2.stopListening();
        adapter3.stopListening();
    }
}
