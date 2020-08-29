package com.guerra.russel.letmebee.CustomerAdapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.guerra.russel.letmebee.Collection.Sizes;
import com.guerra.russel.letmebee.R;

public class CustomerMainProductsAdapter extends FirestoreRecyclerAdapter<Sizes, CustomerMainProductsAdapter.MainProductsHolder> {

    private CustomerMainProductsAdapter.OnItemClickListener listener;

    public CustomerMainProductsAdapter(@NonNull FirestoreRecyclerOptions<Sizes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainProductsHolder holder, int position, @NonNull Sizes model) {
        holder.tv_size.setText(model.getSize());
        holder.tv_price.setText(String.valueOf(model.getPrice()));
        holder.tv_stock.setText(String.valueOf(model.getStock()));
    }

    @NonNull
    @Override
    public MainProductsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_layout_sizes,
                viewGroup, false);
        return new CustomerMainProductsAdapter.MainProductsHolder(v);
    }

    public class MainProductsHolder extends RecyclerView.ViewHolder {
        TextView tv_size, tv_stock, tv_price;

        public MainProductsHolder(@NonNull View itemView) {
            super(itemView);

            tv_size = itemView.findViewById(R.id.tv_size);
            tv_stock = itemView.findViewById(R.id.tv_stock);
            tv_price = itemView.findViewById(R.id.tv_price);

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

    public void setOnItemClickListener(CustomerMainProductsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
