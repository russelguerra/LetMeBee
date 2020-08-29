package com.guerra.russel.letmebee.AdminFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guerra.russel.letmebee.Adapters.UsersAdapter;
import com.guerra.russel.letmebee.AdminActivities.ProfileActivity;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.R;

public class UsersFragment extends Fragment {
    private static final String TAG = "UsersFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UsersAdapter adapter;
    RecyclerView rv_users;
    FirestoreRecyclerOptions<Users> options;
    Dialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_users, container, false);
        getActivity().setTitle("Customers");
        setHasOptionsMenu(true);
        rv_users = v.findViewById(R.id.rv_users);
        mDialog = new Dialog(getContext());
        setUpRecyclerView();

        return v;
    }

    private void setUpRecyclerView() {
        final CollectionReference userRef = db.collection("Users");
        Query query = userRef
                .orderBy("pending", Query.Direction.DESCENDING)
                .orderBy("approved", Query.Direction.DESCENDING)
                .orderBy("firstname", Query.Direction.ASCENDING);
        options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();
        adapter = new UsersAdapter(options);
        rv_users.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_users.setAdapter(adapter);

        adapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Users user = documentSnapshot.toObject(Users.class);
                String email = user.getEmail();
                String firstname = user.getFirstname();
                String lastname = user.getLastname();
                String address = user.getAddress();
                String phone = user.getPhone();
                int approved = user.getApproved();
                int pending = user.getPending();

                Intent profile = new Intent(getContext(), ProfileActivity.class);
                profile.putExtra("EMAIL", email);
                profile.putExtra("NAME", firstname + " " + lastname);
                profile.putExtra("ADDRESS", address);
                profile.putExtra("PHONE", phone);
                profile.putExtra("APPROVED", approved);
                profile.putExtra("PENDING", pending);
                startActivity(profile);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
