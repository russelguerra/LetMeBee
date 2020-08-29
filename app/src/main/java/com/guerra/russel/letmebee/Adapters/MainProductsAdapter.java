package com.guerra.russel.letmebee.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.guerra.russel.letmebee.Collection.MainProducts;
import com.guerra.russel.letmebee.R;
import com.squareup.picasso.Picasso;

public class MainProductsAdapter extends FirestoreRecyclerAdapter<MainProducts, MainProductsAdapter.MainProductsHolder> {
    private static final String TAG = "MainProductsAdapter";
    private OnItemClickListener listener;

    public MainProductsAdapter(@NonNull FirestoreRecyclerOptions<MainProducts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainProductsAdapter.MainProductsHolder holder, int position, @NonNull MainProducts model) {
        Log.e(TAG, "onBindViewHolder: Position: " + position);
        holder.tv_name.setText(model.getName());
        Picasso.get().load(model.getImage()).fit().placeholder(R.drawable.ic_honey).into(holder.iv);
    }

    @NonNull
    @Override
    public MainProductsAdapter.MainProductsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_main_products,
                viewGroup, false);
        return new MainProductsHolder(v);
    }

    public class MainProductsHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        ImageView iv;

        public MainProductsHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            iv = itemView.findViewById(R.id.iv);

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
