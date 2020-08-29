package com.guerra.russel.letmebee.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.R;

public class OrdersAdapter extends FirestoreRecyclerAdapter<Orders, OrdersAdapter.PendingOrderHolder> {
    private static final String TAG = "OrdersAdapter";
    private OrdersAdapter.OnItemClickListener listener;
    private int count;

    public OrdersAdapter(@NonNull FirestoreRecyclerOptions<Orders> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PendingOrderHolder holder, int position, @NonNull Orders model) {
        holder.tv_date.setText(model.getDateOrdered());
        Log.e(TAG, "onBindViewHolder: " + position);
    }

    @NonNull
    @Override
    public PendingOrderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_layout_pendingorders,
                viewGroup, false);
        return new PendingOrderHolder(v);
    }

    class PendingOrderHolder extends RecyclerView.ViewHolder {
        TextView tv_date;

        PendingOrderHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);

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

    public void setOnItemClickListener(OrdersAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
