package com.guerra.russel.letmebee.AdminFragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guerra.russel.letmebee.Adapters.MainProductsAdapter;
import com.guerra.russel.letmebee.Adapters.MainProductsSizesAdapter;
import com.guerra.russel.letmebee.Adapters.OtherProductsAdapter;
import com.guerra.russel.letmebee.Collection.MainProducts;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.Collection.Sizes;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";

    RecyclerView rv_main_products, rv_other_products;

    OtherProductsAdapter mOtherProductsAdapter;
    MainProductsAdapter mMainProductsAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mainProducts, otherProducts;
    Dialog mDialog;

    ImageView iv_add_other_products;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
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
        mOtherProductsAdapter = new OtherProductsAdapter(options);
        rv_other_products.setAdapter(mOtherProductsAdapter);

        mOtherProductsAdapter.setOnItemClickListener(new OtherProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

            }

            @Override
            public void onUpdateClick(DocumentSnapshot documentSnapshot, int position) {
                String id = documentSnapshot.getId();
                OtherProducts currOrder = documentSnapshot.toObject(OtherProducts.class);
                Log.e(TAG, "onUpdateClick: ID: " + id);

                String name = currOrder.getName();
                int stock = currOrder.getStock();
                double price = currOrder.getPrice();

                mDialog.setContentView(R.layout.layout_add_other_item);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();

                final DocumentReference currProduct = db.document("Other Products/" + id);

                final TextInputEditText et_name = mDialog.findViewById(R.id.et_name),
                        et_stock = mDialog.findViewById(R.id.et_stock),
                        et_price = mDialog.findViewById(R.id.et_price);
                final Button bt_add = mDialog.findViewById(R.id.bt_add);
                final ProgressBar pb = mDialog.findViewById(R.id.pb);

                bt_add.setText("Update");
                et_name.setText(name);
                et_stock.setText(String.valueOf(stock));
                et_price.setText(String.valueOf(price));

                bt_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pb.setVisibility(View.VISIBLE);
                        bt_add.setEnabled(false);

                        if (et_name.getText().toString().trim().isEmpty() ||
                                et_stock.getText().toString().trim().isEmpty() ||
                                et_price.getText().toString().trim().isEmpty() ||
                                et_price.getText().toString().trim().equals("0") ||
                                et_stock.getText().toString().trim().equals("0") ||
                                et_stock.getText().toString().trim().equals("0")) {
                            new DoToast(getContext(), "Please fill the form correctly");
                        } else {
                            String name = et_name.getText().toString().trim();
                            int stock = Integer.parseInt(et_stock.getText().toString().trim());
                            double price = Double.parseDouble(et_price.getText().toString().trim());

                            currProduct.update("name", name, "stock", stock, "price", price)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            new DoToast(getContext(), "Products has been updated successfully");
                                            bt_add.setEnabled(true);
                                            pb.setVisibility(View.GONE);
                                            mDialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),
                                                    "Failed to update product",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "onFailure: ", e);
                                        }
                                    });
                        }
                    }
                });
            }

            @Override
            public void onDeleteClick(final DocumentSnapshot documentSnapshot, int position) {
                final OtherProducts thisProduct = documentSnapshot.toObject(OtherProducts.class);
                final String id = documentSnapshot.getId();
                final int orderCounter = thisProduct.getOrder();

                if (orderCounter != 0) {
                    new DoToast(getContext(), "Cannot delete product with existing pending/approved order");
                } else {
                    mDialog.setContentView(R.layout.layout_delete_product);
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mDialog.show();

                    final Button bt_yes = mDialog.findViewById(R.id.bt_yes),
                            bt_no = mDialog.findViewById(R.id.bt_no);
                    final TextView t2 = mDialog.findViewById(R.id.t2);
                    final ProgressBar pb = mDialog.findViewById(R.id.pb);

                    t2.setText("Are you sure you want to delete '" + thisProduct.getName() + "' from the store?");

                    bt_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bt_yes.setEnabled(false);
                            bt_no.setEnabled(false);
                            pb.setEnabled(true);

                            otherProducts.document(id)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            bt_yes.setEnabled(true);
                                            bt_no.setEnabled(true);
                                            pb.setEnabled(true);
                                            mDialog.dismiss();
                                            new DoToast(getContext(), "Product deleted");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            new DoToast(getContext(), "Failed to delete product");
                                            Log.e(TAG, "onFailure: ", e);
                                        }
                                    });
                        }
                    });

                    bt_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
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
                Log.e(TAG, "onItemClick: ID: " + idProduct);
                CollectionReference sizesRef = db.collection("Products/" + idProduct + "/Sizes");

                mDialog.setContentView(R.layout.layout_sizes);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.show();

                final MainProductsSizesAdapter adapter;
                final RecyclerView rv_sizes = mDialog.findViewById(R.id.rv_sizes);
                rv_sizes.setLayoutManager(new LinearLayoutManager(getContext()));

                Query query = sizesRef.orderBy("priority", Query.Direction.ASCENDING);
                final FirestoreRecyclerOptions<Sizes> options = new FirestoreRecyclerOptions.Builder<Sizes>()
                        .setQuery(query, Sizes.class)
                        .build();
                adapter = new MainProductsSizesAdapter(options);
                rv_sizes.setAdapter(adapter);
                adapter.startListening();

                adapter.setOnItemClickListener(new MainProductsSizesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                    }

                    @Override
                    public void onLongClickStock(DocumentSnapshot documentSnapshot, int position) {
                        Log.e(TAG, "onLongClick: PositionL " + position);
                        final String id = documentSnapshot.getId();
                        final Sizes currSize = documentSnapshot.toObject(Sizes.class);

                        mDialog.setContentView(R.layout.layout_update);
                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mDialog.show();

                        final TextInputEditText et_value = mDialog.findViewById(R.id.et_value);
                        final Button bt_update = mDialog.findViewById(R.id.bt_update);
                        final TextInputLayout t1 = mDialog.findViewById(R.id.t1);
                        et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        t1.setHint("New stock");

                        bt_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (et_value.getText().toString().trim().isEmpty() ||
                                        et_value.getText().toString().trim().equals("0")) {
                                    new DoToast(getContext(), "Value is empty");
                                } else {
                                    bt_update.setEnabled(false);
                                    db.document("Products/" + idProduct + "/Sizes/" + id)
                                            .update("stock", Integer.parseInt(et_value.getText().toString().trim()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    new DoToast(getContext(), "Updated successfully");
                                                    bt_update.setEnabled(false);
                                                    mDialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    new DoToast(getContext(), "Failed to update stock");
                                                    bt_update.setEnabled(true);
                                                    Log.e(TAG, "onFailure: ", e);
                                                }
                                            });
                                }
                            }
                        });
                    }

                    @Override
                    public void onLongClickPrice(DocumentSnapshot documentSnapshot, int position) {
                        final String id = documentSnapshot.getId();
                        final Sizes currSize = documentSnapshot.toObject(Sizes.class);

                        mDialog.setContentView(R.layout.layout_update);
                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mDialog.show();

                        final TextInputEditText et_value = mDialog.findViewById(R.id.et_value);
                        final Button bt_update = mDialog.findViewById(R.id.bt_update);
                        final TextInputLayout t1 = mDialog.findViewById(R.id.t1);
                        et_value.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        t1.setHint("New price");

                        bt_update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (et_value.getText().toString().trim().isEmpty() ||
                                        et_value.getText().toString().trim().equals("0")) {
                                    new DoToast(getContext(), "Value is empty");
                                } else {
                                    bt_update.setEnabled(false);
                                    db.document("Products/" + idProduct + "/Sizes/" + id)
                                            .update("price", Integer.parseInt(et_value.getText().toString().trim()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    new DoToast(getContext(), "Updated successfully");
                                                    bt_update.setEnabled(false);
                                                    mDialog.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    new DoToast(getContext(), "Failed to update stock");
                                                    bt_update.setEnabled(true);
                                                    Log.e(TAG, "onFailure: ", e);
                                                }
                                            });
                                }
                            }
                        });
                    }
                });

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

        iv_add_other_products = v.findViewById(R.id.iv_add_other_products);
        iv_add_other_products.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_other_products:
                Log.e(TAG, "onClick: Add Other Products");
                addOtherProducts();
                break;
        }
    }

    private void addOtherProducts() {
        mDialog.setContentView(R.layout.layout_add_other_item);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.show();

        final TextInputEditText et_name = mDialog.findViewById(R.id.et_name),
                et_stock = mDialog.findViewById(R.id.et_stock),
                et_price = mDialog.findViewById(R.id.et_price);
        final Button bt_add = mDialog.findViewById(R.id.bt_add);
        final ProgressBar pb = mDialog.findViewById(R.id.pb);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText().toString().trim().isEmpty() ||
                        et_stock.getText().toString().trim().isEmpty() ||
                        et_price.getText().toString().trim().isEmpty() ||
                        et_price.getText().toString().trim().equals("0") ||
                        et_stock.getText().toString().trim().equals("0") ||
                        et_stock.getText().toString().trim().equals("0")) {
                    new DoToast(getContext(), "Please complete the form");
                } else {
                    pb.setVisibility(View.VISIBLE);
                    bt_add.setEnabled(false);

                    String name = et_name.getText().toString().trim();
                    int stock = Integer.parseInt(et_stock.getText().toString().trim());
                    double price = Double.parseDouble(et_price.getText().toString().trim());
                    DocumentReference newProductID = db.collection("Other Products").document();
                    String newID = newProductID.getId();
                    OtherProducts newProduct = new OtherProducts(name, stock, price, newID, 0);
                    db.collection("Other Products").document(newID)
                            .set(newProduct)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    new DoToast(getContext(), "Products has been added to the store successfully");
                                    bt_add.setEnabled(true);
                                    pb.setVisibility(View.GONE);
                                    mDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    bt_add.setEnabled(true);
                                    pb.setVisibility(View.GONE);
                                    new DoToast(getContext(), "Failed to add product");
                                    Log.e(TAG, "onFailure: ", e);
                                }
                            });
                }
            }
        });
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
