package com.guerra.russel.letmebee.CustomerFragment;


import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.guerra.russel.letmebee.Adapters.ChatAdapter;
import com.guerra.russel.letmebee.Collection.Chats;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ListenerRegistration readMessageListener;
    ListenerRegistration readMessageMethod;

    EditText et_chat;
    ImageButton bt_send;
    RecyclerView rv_chat;
    Dialog mDialog;

    ChatAdapter chatAdapter;
    List<Chats> mchat;
    long currentTime;

    private String sender;
    private String receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat2, container, false);
        initialise(v);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTime = Calendar.getInstance().getTimeInMillis();
                String message = et_chat.getText().toString().trim();
                if (message.isEmpty()) {
                    new DoToast(getContext(), "Cannot send empty message");
                } else {
                    sendMessage(sender, receiver, message, currentTime);
                }
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        readMessageMethod = db.collection("Chats")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            if (e != null) {
                                new DoToast(getActivity(), "Error while loading messages");
                                Log.e(TAG, "onEvent: ", e);
                            } else {
                                readMessage(sender, receiver);
                            }
                        }
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        readMessageMethod.remove();
    }

    private void readMessage(final String sender, final String receiver) {
        mchat = new ArrayList<>();
        readMessageListener = db.collection("Chats")
                .orderBy("currentTime", Query.Direction.ASCENDING)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        mchat.clear();
                        if (e != null) {
                            Log.e(TAG, "onEvent: ", e);
                            return;
                        }

                        if (queryDocumentSnapshots.getDocuments() != null) {
                            for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                                switch (document.getType()) {
                                    case ADDED:
                                        Chats chat = document.getDocument().toObject(Chats.class);
                                        if (chat.getReceiver().equals(sender) && chat.getSender().equals(receiver) ||
                                                chat.getReceiver().equals(receiver) && chat.getSender().equals(sender)) {
                                            mchat.add(chat);
                                        }
                                        break;
                                }
                                chatAdapter = new ChatAdapter(mchat, getActivity());
                                rv_chat.setAdapter(chatAdapter);
                                chatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(String id, String sender, int position) {
                                    }

                                    @Override
                                    public void onDeleteClick(final String id, String sender1, int position) {
                                        if (sender1.equals(sender)) {
                                            mDialog.setContentView(R.layout.layout_delete_message);
                                            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            mDialog.show();

                                            final Button bt_yes = mDialog.findViewById(R.id.bt_yes),
                                                    bt_no = mDialog.findViewById(R.id.bt_no);
                                            final ProgressBar pb = mDialog.findViewById(R.id.pb);

                                            bt_no.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    mDialog.dismiss();
                                                }
                                            });

                                            bt_yes.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    pb.setVisibility(View.VISIBLE);
                                                    bt_yes.setEnabled(false);
                                                    bt_no.setEnabled(false);
                                                    db.document("Chats/" + id)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    pb.setVisibility(View.GONE);
                                                                    bt_yes.setEnabled(true);
                                                                    bt_no.setEnabled(true);
                                                                    new DoToast(getContext(), "Message has been deleted");
                                                                    mDialog.dismiss();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    pb.setVisibility(View.GONE);
                                                                    bt_yes.setEnabled(true);
                                                                    bt_no.setEnabled(true);
                                                                    new DoToast(getContext(), "Failed to delete message");
                                                                }
                                                            });
                                                }
                                            });
                                        } else {
                                            new DoToast(getContext(), "Not allowed to delete messages that was not sent by you");
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void sendMessage(final String sender, String receiver,
                             String message, long currentTime) {
        Chats newChat = new Chats(sender, receiver, message, currentTime);
        DocumentReference newMessageID = db.collection("Users/" + sender + "/Chats")
                .document(String.valueOf(currentTime));
        db.collection("Chats/").document(String.valueOf(currentTime))
                .set(newChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("Users").document(sender)
                                .update("read", 1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),
                                "Failed to send message",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
        et_chat.setText("");
    }

    private void initialise(View v) {
        getActivity().setTitle("Message Us");
        setHasOptionsMenu(true);

        mDialog = new Dialog(getContext());
        et_chat = v.findViewById(R.id.et_chat);
        bt_send = v.findViewById(R.id.bt_send);
        rv_chat = v.findViewById(R.id.rv_chat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_chat.setLayoutManager(linearLayoutManager);

        sender = currentUser.getEmail();
        receiver = "russelguerra@gmail.com";
    }

}
