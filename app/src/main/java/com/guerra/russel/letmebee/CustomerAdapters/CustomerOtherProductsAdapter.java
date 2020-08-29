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
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.R;

public class CustomerOtherProductsAdapter extends FirestoreRecyclerAdapter<OtherProducts, CustomerOtherProductsAdapter.OtherProductsHolder> {
    private static final String TAG = "CustomerOtherProductsAdapter";

    private CustomerOtherProductsAdapter.OnItemClickListener listener;

    public CustomerOtherProductsAdapter(@NonNull FirestoreRecyclerOptions<OtherProducts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomerOtherProductsAdapter.OtherProductsHolder holder, int position, @NonNull OtherProducts model) {
        holder.tv_name.setText(model.getName());
        holder.tv_stock.setText(String.valueOf(model.getStock()));
        holder.tv_price.setText(String.valueOf(model.getPrice()));
    }

    @NonNull
    @Override
    public CustomerOtherProductsAdapter.OtherProductsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_other_products,
                viewGroup, false);
        return new CustomerOtherProductsAdapter.OtherProductsHolder(v);
    }

    public class OtherProductsHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_stock, tv_price;

        public OtherProductsHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
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

    public void setOnItemClickListener(CustomerOtherProductsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}