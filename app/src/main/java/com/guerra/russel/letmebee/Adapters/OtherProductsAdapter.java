package com.guerra.russel.letmebee.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.R;

public class OtherProductsAdapter extends FirestoreRecyclerAdapter<OtherProducts, OtherProductsAdapter.OtherProductsHolder> {
    private static final String TAG = "OtherProductsAdapter";

    private OnItemClickListener listener;

    public OtherProductsAdapter(@NonNull FirestoreRecyclerOptions<OtherProducts> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OtherProductsHolder holder, int position, @NonNull OtherProducts model) {
        holder.tv_name.setText(model.getName());
        holder.tv_stock.setText(String.valueOf(model.getStock()));
        holder.tv_price.setText(String.valueOf(model.getPrice()));
    }

    @NonNull
    @Override
    public OtherProductsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_other_products,
                viewGroup, false);
        return new OtherProductsHolder(v);
    }

    class OtherProductsHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_stock, tv_price;

        OtherProductsHolder(@NonNull View itemView) {
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

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle("Select Action");
                    MenuItem update = contextMenu.add(Menu.NONE, 1, 1, "Update");
                    MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

                    update.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int position = getAdapterPosition();
                            listener.onUpdateClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        }
                    });

                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int position = getAdapterPosition();
                            listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        }
                    });
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void onUpdateClick(DocumentSnapshot documentSnapshot, int position);

        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
