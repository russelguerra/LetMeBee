package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Dialog mDialog;
    EditText et_chat;
    ImageButton bt_send;
    RecyclerView rv_chat;

    ChatAdapter chatAdapter;
    List<Chats> mchat;
    long currentTime;

    String sender;
    String receiver, email, firstname, lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extra = getIntent().getExtras();
        email = extra.getString("EMAIL");
        firstname = extra.getString("FIRSTNAME");
        lastname = extra.getString("LASTNAME");

        sender = currentUser.getEmail();
        receiver = email;

        initialise(firstname, lastname);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTime = Calendar.getInstance().getTimeInMillis();
                String message = et_chat.getText().toString().trim();

                if (message.isEmpty()) {
                    new DoToast(getApplicationContext(), "Cannot send empty message");
                } else {
                    sendMessage(sender, receiver, message, currentTime);
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

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Chats")
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            new DoToast(getApplicationContext(), "Error while loading messages");
                        } else {
                            readMessage(sender, receiver);
                        }
                    }
                });
    }

    private void sendMessage(final String sender, final String receiver,
                             final String message, final Long currentTime) {
        Chats newChat = new Chats(sender, receiver, message, currentTime);

        db.collection("Chats/").document(String.valueOf(currentTime))
                .set(newChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.document("Users/" + receiver)
                                .update("read", 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new DoToast(getApplicationContext(), "Failed to send message");
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
        et_chat.setText("");
    }

    private void readMessage(final String myid, final String userid) {
        Log.e(TAG, "readMessage: myid: " + myid + " userid: " + userid);
        mchat = new ArrayList<>();
        db.collection("Chats")
                .orderBy("currentTime", Query.Direction.ASCENDING)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        mchat.clear();
                        for (DocumentChange document : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (document.getType()) {
                                case ADDED:
                                    Chats chat = document.getDocument().toObject(Chats.class);
                                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                                        mchat.add(chat);
                                    }
                                    break;
                            }
                            chatAdapter = new ChatAdapter(mchat, ChatActivity.this);
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
                                                                new DoToast(getApplicationContext(),
                                                                        "Message has been deleted");
                                                                mDialog.dismiss();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pb.setVisibility(View.GONE);
                                                                bt_yes.setEnabled(true);
                                                                bt_no.setEnabled(true);
                                                                new DoToast(getApplicationContext(),
                                                                        "Failed to delete message");
                                                            }
                                                        });
                                            }
                                        });
                                    } else {
                                        new DoToast(getApplicationContext(),
                                                "Not allowed to delete messages\nthat was not sent by you");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void initialise(String firstname, String lastname) {
        getSupportActionBar().setTitle(firstname + " " + lastname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = new Dialog(this);
        et_chat = findViewById(R.id.et_chat);
        bt_send = findViewById(R.id.bt_send);
        rv_chat = findViewById(R.id.rv_chat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv_chat.setLayoutManager(linearLayoutManager);
    }
}
