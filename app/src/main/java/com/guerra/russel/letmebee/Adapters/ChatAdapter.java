package com.guerra.russel.letmebee.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guerra.russel.letmebee.Collection.Chats;
import com.guerra.russel.letmebee.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final String TAG = "ChatAdapter";

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private OnItemClickListener listener;
    private List<Chats> chat;
    private Context context;

    public ChatAdapter(List<Chats> chat, Context context) {
        this.chat = chat;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_RIGHT) {
            View v = LayoutInflater.from(context).inflate(R.layout.chat_item_right, viewGroup, false);
            return new ChatAdapter.ViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.chat_item_left, viewGroup, false);
            return new ChatAdapter.ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder viewHolder, final int i) {
        final Chats chat = this.chat.get(i);
        final String chatID = String.valueOf(chat.getCurrentTime());
        final String sender = chat.getSender();
        viewHolder.show_message.setText(chat.getMessage());

        viewHolder.show_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(chatID, sender, i);
                }
            }
        });

        viewHolder.show_message.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");

                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (i != RecyclerView.NO_POSITION && listener != null) {
                            listener.onDeleteClick(chatID, sender, i);
                        }
                        return true;
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView show_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);

/*            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String messageID = String.valueOf(chat.get(position).getCurrentTime());
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(messageID, position);
                    }
                }
            });

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Delete");

                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int position = getAdapterPosition();
                            String messageID = String.valueOf(chat.get(position).getCurrentTime());
                            if (position != RecyclerView.NO_POSITION && listener != null) {
                                listener.onDeleteClick(messageID, position);
                            }
                            return true;
                        }
                    });
                }
            });*/
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String id, String sender, int position);

        void onDeleteClick(String id, String sender, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currecntUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chat.get(position).getSender().equals(currecntUser.getEmail())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
