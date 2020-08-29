package com.guerra.russel.letmebee.Adapters;

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

public class ProductsAdapter extends FirestoreRecyclerAdapter<Sizes, ProductsAdapter.ProductsHolder> {
    private static final String TAG = "ProductsAdapter";
    private ProductsAdapter.OnItemClickListener listener;

    public ProductsAdapter(@NonNull FirestoreRecyclerOptions<Sizes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductsAdapter.ProductsHolder holder, int position, @NonNull Sizes model) {
        holder.tv_name.setText(model.getSize());
        holder.tv_stock.setText(String.valueOf(model.getStock()));
        holder.tv_price.setText(String.valueOf(model.getPrice()));
    }

    @NonNull
    @Override
    public ProductsAdapter.ProductsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_layout_products,
                viewGroup, false);
        return new ProductsHolder(v);
    }

    class ProductsHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_stock, tv_price;

        ProductsHolder(@NonNull View itemView) {
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

    public void setOnItemClickListener(ProductsAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
