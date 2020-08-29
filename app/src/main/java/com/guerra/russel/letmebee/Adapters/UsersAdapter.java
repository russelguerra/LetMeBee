package com.guerra.russel.letmebee.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

public class UsersAdapter extends FirestoreRecyclerAdapter<Users, UsersAdapter.UsersHolder> {
    private static final String TAG = "UsersAdapter";

    private OnItemClickListener listener;

    public UsersAdapter(@NonNull FirestoreRecyclerOptions<Users> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull UsersHolder holder, int position, @NonNull Users model) {
        if (model.getEmail().equals("russelguerra@gmail.com")) {
            holder.parent_layout.setVisibility(View.GONE);
        } else {
            if (model.getPending() <= 0) {
                holder.iv_pending.setVisibility(View.GONE);
            } else {
                holder.iv_pending.setVisibility(View.VISIBLE);
            }

            if (model.getApproved() <= 0) {
                holder.iv_approved.setVisibility(View.GONE);
            } else {
                holder.iv_approved.setVisibility(View.VISIBLE);
            }

            holder.tv_name.setText(model.getFirstname() + " " + model.getLastname());
        }
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_layout_users,
                viewGroup, false);
        return new UsersHolder(v);
    }

    class UsersHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv_pending, iv_approved;
        RelativeLayout parent_layout;

        UsersHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            iv_pending = itemView.findViewById(R.id.iv_pending);
            iv_approved = itemView.findViewById(R.id.iv_approved);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
