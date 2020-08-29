package com.guerra.russel.letmebee.AdminActivities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guerra.russel.letmebee.AdminFragments.ChatFragment;
import com.guerra.russel.letmebee.AdminFragments.HomeFragment;
import com.guerra.russel.letmebee.AdminFragments.OrdersFragment;
import com.guerra.russel.letmebee.AdminFragments.UsersFragment;
import com.guerra.russel.letmebee.MainActivity;
import com.guerra.russel.letmebee.R;

public class AdminActivity extends AppCompatActivity {
    private static final String TAG = "AdminActivity";

    FirebaseAuth auth;
    FirebaseUser currentUser;
    Dialog dialog;

    BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initialise();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                new HomeFragment()).commit();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new HomeFragment()).commit();
                        break;

                    case  R.id.navigation_customers:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new UsersFragment()).commit();
                        break;

                    case R.id.navigation_message:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new ChatFragment()).commit();
                        break;

                    case R.id.navigation_order:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                new OrdersFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }

    private void initialise() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        dialog = new Dialog(this);

        navigation = findViewById(R.id.navigation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                logOut();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logOut() {
        dialog.setContentView(R.layout.layout_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final Button bt_yes = dialog.findViewById(R.id.bt_yes),
                bt_no = dialog.findViewById(R.id.bt_no);

        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                currentUser = null;
                startActivity(new Intent(AdminActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }
        });
    }
}
