package com.guerra.russel.letmebee.CustomerFragment;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guerra.russel.letmebee.Adapters.MainProductsAdapter;
import com.guerra.russel.letmebee.Collection.MainProducts;
import com.guerra.russel.letmebee.Collection.Orders;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.Collection.Sizes;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.CustomerAdapters.CustomerMainProductsAdapter;
import com.guerra.russel.letmebee.CustomerAdapters.CustomerOtherProductsAdapter;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeCustomerFragment extends Fragment {

    RecyclerView rv_main_products, rv_other_products;

    CustomerOtherProductsAdapter mOtherProductsAdapter;
    MainProductsAdapter mMainProductsAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mainProducts, otherProducts;
    Dialog mDialog;

    double totalprice;

    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_customer, container, false);

        initialise(v);

        setUpMainProductsAdapter();
        setUpOtherProductsAdapter();

        return v;
    }

    private void setUpOtherProductsAdapter() {
        Query query = otherProducts.orderBy("name", Query.Direction.ASCENDING);
        final FirestoreRecyclerOptions<OtherProducts> options = new FirestoreRecyclerOptions.Builder<OtherProducts>()
                .setQuery(query, OtherProducts.class)
                .build();
        mOtherProductsAdapter = new CustomerOtherProductsAdapter(options);
        rv_other_products.setAdapter(mOtherProductsAdapter);

        mOtherProductsAdapter.setOnItemClickListener(new CustomerOtherProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final OtherProducts size = documentSnapshot.toObject(OtherProducts.class);
                final String id = documentSnapshot.getId();
                final String name = size.getName();
                final double price = size.getPrice();
                final int stock = size.getStock();
                final String productID = size.getId();
                final int orderCounter = size.getOrder();

                if (stock == 0) {
                    new DoToast(getContext(), "Currently no stock in the store. Please wait for the owner to restock");
                } else {
                    mDialog.setContentView(R.layout.layout_order);
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialog.show();

                    final TextView tv_product = mDialog.findViewById(R.id.tv_product),
                            tv_size = mDialog.findViewById(R.id.tv_size),
                            tv_stock = mDialog.findViewById(R.id.tv_stock),
                            tv_price = mDialog.findViewById(R.id.tv_price),
                            tv_totalprice = mDialog.findViewById(R.id.tv_totalprice);
                    final ProgressBar pb = mDialog.findViewById(R.id.pb);
                    final EditText et_quantity = mDialog.findViewById(R.id.et_quantity);
                    final Button bt_order = mDialog.findViewById(R.id.bt_order);

                    tv_product.setText(name);
                    tv_size.setVisibility(View.GONE);
                    tv_stock.setText("Stock: " + stock);
                    tv_price.setText("Price: " + price);
                    bt_order.setEnabled(false);

                    et_quantity.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (et_quantity.getText().toString().trim().isEmpty() ||
                                    et_quantity.getText().toString().trim().equals("0")) {
                                tv_totalprice.setText("Total Price: ");
                                bt_order.setEnabled(false);
                            } else {
                                final int quantity = Integer.parseInt(et_quantity.getText().toString().trim());
                                if (quantity > stock) {
                                    tv_totalprice.setText("Total Price: ");
                                    new DoToast(getContext(), "Cannot order amount\ngreater than the current stock");
                                    bt_order.setEnabled(false);
                                } else {
                                    totalprice = quantity * price;
                                    tv_totalprice.setText("Total Price: " + totalprice);
                                    bt_order.setEnabled(true);

                                    bt_order.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            pb.setVisibility(View.VISIBLE);
                                            bt_order.setEnabled(false);

                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                            String date = format.format(calendar.getTime());

                                            orders(email, date, name, null
                                                    , quantity, totalprice, 0,
                                                    null, null, null, productID);
                                            int newStock = stock - quantity;
                                            int newOrderCounter = orderCounter + 1;
                                            db.document("Other Products/" + id)
                                                    .update("stock", newStock, "order", newOrderCounter)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mDialog.dismiss();
                                                            pb.setVisibility(View.GONE);
                                                            bt_order.setEnabled(true);
                                                            new DoToast(getContext(), "Order has been placed");

                                                            db.document("Users/" + email)
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            Users currUser = documentSnapshot.toObject(Users.class);
                                                                            int pendingOrders = currUser.getPending() + 1;
                                                                            db.document("Users/" + email)
                                                                                    .update("pending", pendingOrders);
                                                                        }
                                                                    });

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            pb.setVisibility(View.GONE);
                                                            bt_order.setEnabled(true);
                                                            new DoToast(getContext(), "Failed to place order");
                                                        }
                                                    });
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void setUpMainProductsAdapter() {
        Query query = mainProducts.orderBy("name", Query.Direction.ASCENDING);
        final FirestoreRecyclerOptions<MainProducts> options = new FirestoreRecyclerOptions.Builder<MainProducts>()
                .setQuery(query, MainProducts.class)
                .build();
        mMainProductsAdapter = new MainProductsAdapter(options);
        rv_main_products.setAdapter(mMainProductsAdapter);

        mMainProductsAdapter.setOnItemClickListener(new MainProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                final String idProduct = documentSnapshot.getId();
                CollectionReference sizesRef = db.collection("Products/" + idProduct + "/Sizes");

                mDialog.setContentView(R.layout.layout_sizes);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();

                final CustomerMainProductsAdapter adapter;
                final RecyclerView rv_sizes = mDialog.findViewById(R.id.rv_sizes);
                rv_sizes.setLayoutManager(new LinearLayoutManager(getContext()));

                Query query = sizesRef.orderBy("priority", Query.Direction.ASCENDING);
                final FirestoreRecyclerOptions<Sizes> options = new FirestoreRecyclerOptions.Builder<Sizes>()
                        .setQuery(query, Sizes.class)
                        .build();
                adapter = new CustomerMainProductsAdapter(options);
                rv_sizes.setAdapter(adapter);
                adapter.startListening();

                adapter.setOnItemClickListener(new CustomerMainProductsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                        final Sizes size = documentSnapshot.toObject(Sizes.class);
                        final String id = documentSnapshot.getId();
                        final double price = size.getPrice();
                        final int stock = size.getStock();
                        final String productID = size.getId();

                        if (stock == 0) {
                            new DoToast(getContext(), "Currently no stock in the store. Please wait for the owner to restock");
                        } else {
                            mDialog.setContentView(R.layout.layout_order);
                            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            mDialog.show();

                            final TextView tv_product = mDialog.findViewById(R.id.tv_product),
                                    tv_size = mDialog.findViewById(R.id.tv_size),
                                    tv_stock = mDialog.findViewById(R.id.tv_stock),
                                    tv_price = mDialog.findViewById(R.id.tv_price),
                                    tv_totalprice = mDialog.findViewById(R.id.tv_totalprice);
                            final ProgressBar pb = mDialog.findViewById(R.id.pb);
                            final EditText et_quantity = mDialog.findViewById(R.id.et_quantity);
                            final Button bt_order = mDialog.findViewById(R.id.bt_order);

                            tv_product.setText(idProduct);
                            tv_size.setText("Size: " + size.getSize());
                            tv_stock.setText("Stock: " + stock);
                            tv_price.setText("Price: " + price);
                            bt_order.setEnabled(false);

                            et_quantity.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                    if (et_quantity.getText().toString().trim().isEmpty() ||
                                            et_quantity.getText().toString().trim().equals("0")) {
                                        tv_totalprice.setText("Total Price: ");
                                        bt_order.setEnabled(false);
                                    } else {
                                        final int quantity = Integer.parseInt(et_quantity.getText().toString().trim());
                                        if (quantity > stock) {
                                            tv_totalprice.setText("Total Price: ");
                                            new DoToast(getContext(), "Cannot order amount greater than the current stock");
                                            bt_order.setEnabled(false);
                                        } else {
                                            totalprice = quantity * price;
                                            tv_totalprice.setText("Total Price: " + totalprice);
                                            bt_order.setEnabled(true);

                                            bt_order.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    pb.setVisibility(View.VISIBLE);
                                                    bt_order.setEnabled(false);

                                                    Calendar calendar = Calendar.getInstance();
                                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                                    String date = format.format(calendar.getTime());
                                                    orders(email, date, idProduct, size.getSize()
                                                            , quantity, totalprice, 0,
                                                            null, null, null, productID);
                                                    int newStock = stock - quantity;
                                                    db.document("Products/" + idProduct + "/Sizes/" + size.getSize())
                                                            .update("stock", newStock)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mDialog.dismiss();
                                                                    pb.setVisibility(View.GONE);
                                                                    bt_order.setEnabled(true);
                                                                    Toast.makeText(getContext(),
                                                                            "Order has been placed",
                                                                            Toast.LENGTH_SHORT).show();

                                                                    db.document("Users/" + email)
                                                                            .get()
                                                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                    Users currUser = documentSnapshot.toObject(Users.class);
                                                                                    int pendingOrders = currUser.getPending() + 1;
                                                                                    db.document("Users/" + email)
                                                                                            .update("pending", pendingOrders);
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    pb.setVisibility(View.GONE);
                                                                    bt_order.setEnabled(true);
                                                                    new DoToast(getContext(), "Failed to place order");
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    private void orders(String by, String dateOrdered, String name, String size, int quantity,
                        double price, int status, String dateApproved, String dateDelivered, String signature, String id) {
        Orders order = new Orders(by, dateOrdered, name, size, quantity, price, status, dateApproved, dateDelivered, signature, id);

        db.collection("Orders")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new DoToast(getContext(), "Failed to place order");
                    }
                });
    }

    private void initialise(View v) {
        getActivity().setTitle("Store");
        setHasOptionsMenu(true);

        mainProducts = db.collection("Products");
        otherProducts = db.collection("Other Products");

        mDialog = new Dialog(getActivity());

        rv_main_products = v.findViewById(R.id.rv_main_products);
        rv_main_products.setLayoutManager(new LinearLayoutManager(getContext()));

        rv_other_products = v.findViewById(R.id.rv_other_products);
        rv_other_products.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        mMainProductsAdapter.startListening();
        mOtherProductsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMainProductsAdapter.stopListening();
        mOtherProductsAdapter.stopListening();
    }
}
