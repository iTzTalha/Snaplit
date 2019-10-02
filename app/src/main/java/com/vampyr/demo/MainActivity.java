package com.vampyr.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Fragments.HomeFragment;
import com.vampyr.demo.Fragments.DiscoverFragment;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    boolean doubleBackToExitPressedOnce = false;

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private ProgressDialog loadingBar;

    ImageView nav_image;
    TextView nav_username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        //Drawer Navigation Layout

        drawerLayout = findViewById(R.id.drawer_layout);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headView = navigationView.getHeaderView(0);
        nav_image = headView.findViewById(R.id.nav_image);
        nav_username = headView.findViewById(R.id.nav_username);
        nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        nav_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(nav_image);
                nav_username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Drawer Navigation Ends Here

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Bottom Navigation Layout
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_Home:
                        CloseDrawer();
                        selectedFragment = new HomeFragment();
                        setTitle("Home");
                        break;

                    case R.id.nav_Discover:
                        CloseDrawer();
                        selectedFragment = new DiscoverFragment();
                        setTitle("Discover");
                        break;

                    case R.id.nav_addPosts:
                        CloseDrawer();
                        // selectedFragment = new PostFragment();
                        startActivity(new Intent(MainActivity.this, PostsActivity.class));
                        break;
                    /*
                    case R.id.nav_chat:
                        CloseDrawer();
                        selectedFragment = new ChatsFragment();
                        setTitle("Chats");
                        break;
                     */
                    case R.id.nav_profile:
                        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        CloseDrawer();
                        selectedFragment = new ProfileFragment();
                        setTitle("Me");
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {

        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tap again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            super.onBackPressed();
            return;
        }

        CloseDrawer();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                SendUserToLoginActivity();
        }

        return true;
    }

    public void CloseDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(loginIntent);
    }

}
