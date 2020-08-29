package com.guerra.russel.letmebee.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.R;

public class ChatUserAdapter extends FirestoreRecyclerAdapter<Users, ChatUserAdapter.ChatUserHolder> {
    private static final String TAG = "ChatUserAdapter";
    private OnItemClickListener listener;

    public ChatUserAdapter(@NonNull FirestoreRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatUserAdapter.ChatUserHolder holder, int position, @NonNull Users model) {
        holder.tv_name.setText(model.getFirstname() + " " + model.getLastname());
        if (model.getRead() == 1) {
            holder.iv_chat.setVisibility(View.VISIBLE);
        } else {
            holder.iv_chat.setVisibility(View.GONE);
        }
        Log.d(TAG, "onBindViewHolder: " + position);
    }

    @NonNull
    @Override
    public ChatUserAdapter.ChatUserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_layout_customers,
                viewGroup, false);
        return new ChatUserHolder(v);
    }

    class ChatUserHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_chat;
        RelativeLayout parent_layout;

        ChatUserHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_chat = itemView.findViewById(R.id.iv_chat);
            parent_layout = itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete Conversation");

                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION && listener != null) {
                                listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                            }
                            return true;
                        }
                    });
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
