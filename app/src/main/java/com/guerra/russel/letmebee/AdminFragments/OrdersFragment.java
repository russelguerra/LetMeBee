package com.guerra.russel.letmebee.AdminFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.guerra.russel.letmebee.Adapters.OrdersAdapter;
import com.guerra.russel.letmebee.AdminActivities.ApprovedOrder2Activity;
import com.guerra.russel.letmebee.AdminActivities.PendingOrder2Activity;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;

import javax.annotation.Nullable;

public class OrdersFragment extends Fragment {
    private static final String TAG = "OrdersFragment";
    TextView t1, t2;
    RecyclerView rv_pendingorders, rv_approvedorders;
    OrdersAdapter adapter, adapter2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderRef = db.collection("Orders");

    ListenerRegistration listener, listener2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_orders, container, false);
        initialise(v);

        setUpPending();
        setUpApproved();

        return v;
    }

    private void initialise(View v) {
        getActivity().setTitle("Orders");
        t1 = v.findViewById(R.id.t1);
        t2 = v.findViewById(R.id.t2);
        rv_pendingorders = v.findViewById(R.id.rv_pendingorders);
        rv_approvedorders = v.findViewById(R.id.rv_approvedorders);

        rv_pendingorders.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_approvedorders.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpApproved() {
        Log.e(TAG, "setUpApproved: ");
        Query query = orderRef.whereEqualTo("status", 1)
                .orderBy("dateApproved", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter2 = new OrdersAdapter(options);
        rv_approvedorders.setAdapter(adapter2);

        adapter2.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (documentSnapshot != null) {
                    String id = adapter2.getSnapshots().getSnapshot(position).getId();
                    Intent product = new Intent(getActivity(), ApprovedOrder2Activity.class);
                    product.putExtra("ID", id);
                    startActivity(product);
                }
            }
        });

        listener = query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    t2.setText("Approved Orders: " + queryDocumentSnapshots.size());
                }
            }
        });
    }

    private void setUpPending() {
        Log.e(TAG, "setUpPending: ");
        final Query query = orderRef.whereEqualTo("status", 0)
                .orderBy("dateOrdered", Query.Direction.DESCENDING);
        final FirestoreRecyclerOptions<Orders> options = new FirestoreRecyclerOptions.Builder<Orders>()
                .setQuery(query, Orders.class)
                .build();
        adapter = new OrdersAdapter(options);
        rv_pendingorders.setAdapter(adapter);

        adapter.setOnItemClickListener(new OrdersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Orders order = documentSnapshot.toObject(Orders.class);
                String id = adapter.getSnapshots().getSnapshot(position).getId();
                Intent product = new Intent(getActivity(), PendingOrder2Activity.class);
                product.putExtra("ID", id);
                startActivity(product);
            }
        });

        listener2 = query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                t1.setText("Pending Orders: " + queryDocumentSnapshots.size());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        adapter2.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter2.stopListening();
        adapter.stopListening();

        listener.remove();
        listener2.remove();
    }
}
