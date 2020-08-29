package com.guerra.russel.letmebee.AdminActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.guerra.russel.letmebee.Collection.OtherProducts;
import com.guerra.russel.letmebee.Collection.Users;
import com.guerra.russel.letmebee.DoToast;
import com.guerra.russel.letmebee.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DeliverOrderActivity extends AppCompatActivity {
    private static final String TAG = "DeliverOrderActivity";

    private StorageReference signatureRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DocumentReference userRef;
    private DocumentReference orderRef;

    Uri signatureUri;
    File file;

    String id, email, productID;
    int approved, counter, orderCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaintView paint = new PaintView(getApplicationContext());
        setContentView(paint);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            id = extra.getString("ID");
            email = extra.getString("EMAIL");
            counter = extra.getInt("COUNTER");
            productID = extra.getString("PRODUCTID");

            orderRef = db.document("Orders/" + id);
            signatureRef = storage.getReference("signatures");

            initialise();

            Log.e(TAG, "initialise: Counter: " + counter + " ProductID: " + productID);
        }
    }

    private void initialise() {
        getSupportActionBar().setTitle("Customer Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deliver:
                deliver();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void deliver() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(DeliverOrderActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        } else {
            View rootView = getWindow().getDecorView().getRootView();
            Bitmap bm = getScreenShot(rootView);
            final String fileName = System.currentTimeMillis() + ".jpg";
            store(bm, fileName);

            final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/Screenshots/" + fileName;
            final Uri imageUri = Uri.fromFile(file);

            Log.e(TAG, "deliver: URI: " + imageUri);

            signatureRef.child(fileName)
                    .putFile(imageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            signatureRef.child(fileName).getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            signatureUri = uri;
                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                            String date = format.format(calendar.getTime());
                                            orderRef.update("status", 2,
                                                    "dateDelivered", date,
                                                    "signature", signatureUri.toString())
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.document("Users/" + email)
                                                                    .get()
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                            Users user = documentSnapshot.toObject(Users.class);
                                                                            approved = user.getApproved() - 1;
                                                                            db.document("Users/" + email)
                                                                                    .update("approved", approved)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            if (counter == 0) {
                                                                                                db.document("Other Products/" + productID)
                                                                                                        .get()
                                                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                                                OtherProducts currProduct = documentSnapshot.toObject(OtherProducts.class);
                                                                                                                orderCounter = currProduct.getOrder();
                                                                                                                db.document("Other Products/" + productID)
                                                                                                                        .update("order", orderCounter - 1)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                Log.e(TAG, "onSuccess: Got order");
                                                                                                                                new DoToast(getApplicationContext(),
                                                                                                                                        "Delivery has been successfully verified");
                                                                                                                                startActivity(new Intent(DeliverOrderActivity.this,
                                                                                                                                        AdminActivity.class));
                                                                                                                                finish();
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Log.e(TAG, "onFailure: ", e);
                                                                                                            }
                                                                                                        });
                                                                                                Log.e(TAG, "onSuccess: Counter is 0");
                                                                                            } else {
                                                                                                new DoToast(getApplicationContext(), "Delivery has been successfully verified");
                                                                                                startActivity(new Intent(DeliverOrderActivity.this,
                                                                                                        AdminActivity.class));
                                                                                                finish();
                                                                                                Log.e(TAG, "onSuccess: Counter is not 0");
                                                                                            }
                                                                                        }
                                                                                    })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            new DoToast(getApplicationContext(), "Failed to deliver");
                                                                                            Log.e(TAG, "onFailure: ", e);
                                                                                        }
                                                                                    });
                                                                        }
                                                                    });


                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            new DoToast(getApplicationContext(), "Failed to verify");
                                                        }
                                                    });
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: URI: " + imageUri.toString(), e);
                        }
                    });
        }
    }

    public void store(Bitmap bm, String fileName) {
        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        file = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_deliver, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
