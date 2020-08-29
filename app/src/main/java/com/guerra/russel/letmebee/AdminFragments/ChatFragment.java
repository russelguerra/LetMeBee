package com.guerra.russel.letmebee.AdminFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.guerra.russel.letmebee.Adapters.ChatUserAdapter;
import com.guerra.russel.letmebee.AdminActivities.ChatActivity;
import com.guerra.russel.letmebee.Collection.Chats;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

import java.util.List;


public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference userRef = db.collection("Users");
    ChatUserAdapter adapter;
    RecyclerView rv_customers;
    Dialog mDialog;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) {
            ((ViewGroup) v.getParent()).removeView(v);
        } else {
            v = inflater.inflate(R.layout.fragment_chat, container, false);
            getActivity().setTitle("Messages");
            setHasOptionsMenu(true);

            rv_customers = v.findViewById(R.id.rv_customers);
            mDialog = new Dialog(getContext());

            setUpRecyclerView();
        }

        return v;
    }

    private void setUpRecyclerView() {
        Query query = userRef.orderBy("read", Query.Direction.DESCENDING)
                .orderBy("firstname", Query.Direction.ASCENDING);
        final FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();
        adapter = new ChatUserAdapter(options);
        rv_customers.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_customers.setAdapter(adapter);

        adapter.setOnItemClickListener(new ChatUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Users users = documentSnapshot.toObject(Users.class);
                String email = users.getEmail();
                String firstname = users.getFirstname();
                String lastname = users.getLastname();

                Intent chat = new Intent(getContext(), ChatActivity.class);
                chat.putExtra("EMAIL", email);
                chat.putExtra("FIRSTNAME", firstname);
                chat.putExtra("LASTNAME", lastname);
                startActivity(chat);

                DocumentReference userRef = db.collection("Users").document(email);
                userRef.update("read", 0)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new DoToast(getContext(), "Error");
                                Log.e(TAG, "onFailure: ", e);
                            }
                        });
            }

            @Override
            public void onDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                final Users user = documentSnapshot.toObject(Users.class);
                final String userEmail = user.getEmail();
                Log.d(TAG, "onDeleteClick: Receiver: " + userEmail);

                mDialog.setContentView(R.layout.layout_delete_conversation);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();

                final ProgressBar pb = mDialog.findViewById(R.id.pb);
                final Button bt_yes = mDialog.findViewById(R.id.bt_yes),
                        bt_no = mDialog.findViewById(R.id.bt_no);
                final TextView t3 = mDialog.findViewById(R.id.t3);

                t3.setText("Are you sure you want to delete your conversation with '" +
                        user.getFirstname() + " " + user.getLastname() + "'?");

                bt_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });

                bt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt_no.setEnabled(false);
                        bt_yes.setEnabled(false);
                        pb.setVisibility(View.VISIBLE);

                        Query sender = db.collection("Chats")
                                .whereEqualTo("sender", userEmail);
                        Query receiver = db.collection("Chats")
                                .whereEqualTo("receiver", userEmail);

                        Task firstTask = sender.get();
                        Task secondTask = receiver.get();

                        Task<List<QuerySnapshot>> combineTask = Tasks.whenAllSuccess(firstTask, secondTask);
                        combineTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                            @Override
                            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                                WriteBatch batch = db.batch();

                                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                                    for (QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                                        Chats chats = documentSnapshot1.toObject(Chats.class);
                                        Log.e(TAG, "onSuccess: IDs: " + chats.getCurrentTime());

                                        batch.delete(db.document("Chats/" + chats.getCurrentTime()));
                                    }
                                }

                                batch.commit()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DocumentReference userRef = db.collection("Users").document(userEmail);
                                                userRef.update("read", 0)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                bt_no.setEnabled(true);
                                                                bt_yes.setEnabled(true);
                                                                pb.setVisibility(View.GONE);
                                                                mDialog.dismiss();
                                                                new DoToast(getContext(), "Conversation with with '" +
                                                                        user.getFirstname() + " " + user.getLastname() + "' successfully deleted");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                bt_no.setEnabled(true);
                                                                bt_yes.setEnabled(true);
                                                                pb.setVisibility(View.GONE);
                                                                new DoToast(getContext(), "Failed to delete conversation");
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                bt_no.setEnabled(true);
                                                bt_yes.setEnabled(true);
                                                pb.setVisibility(View.GONE);
                                                new DoToast(getContext(), "Failed to delete conversation");
                                            }
                                        });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
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
