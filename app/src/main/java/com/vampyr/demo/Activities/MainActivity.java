package com.vampyr.demo.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
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
import com.vampyr.demo.Fragments.ChatFragment;
import com.vampyr.demo.Fragments.HomeFragment;
import com.vampyr.demo.Fragments.DiscoverFragment;
import com.vampyr.demo.Fragments.PostDetailsFragment;
import com.vampyr.demo.Fragments.PostFragment;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

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

    CircleImageView nav_image;
    TextView nav_username,followers,followersTextView,following,followingTextView;

    SharedPreferences.Editor editor;

    String profileid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /***********
         * check internet Connection and load the appp
         */

//        if (!isConnected(MainActivity.this)){
//            buildDialog(MainActivity.this).show();
//        }else {
//
//        }

        /***********
         * check internet Connection and load the appp
         */

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        //Drawer Navigation Layout

        drawerLayout = findViewById(R.id.drawer_layout);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        View headView = navigationView.getHeaderView(0);
        nav_image = headView.findViewById(R.id.nav_image);
        nav_username = headView.findViewById(R.id.nav_username);
        followers = headView.findViewById(R.id.followers);
        followersTextView = headView.findViewById(R.id.followersTextView);
        following = headView.findViewById(R.id.following);
        followingTextView = headView.findViewById(R.id.followingTextView);

   /*     nav_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
                editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.apply();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        nav_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
                editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.apply();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });

        followersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });

        followingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });
    */
        getFollowers();

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

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherID");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
            editor.putString("profileid",publisher);
            editor.apply();

           // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            startActivity(new Intent(MainActivity.this,UsersActivity.class));
            finish();
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_Home:
                        selectedFragment = new HomeFragment();
                        setTitle("Home");
                        break;

                    case R.id.nav_Discover:
                        selectedFragment = new DiscoverFragment();
                        setTitle("Discover");
                        break;

                    case R.id.nav_addPosts:
                        selectedFragment = new PostFragment();
                        break;

                    case R.id.nav_chat:
                        selectedFragment = new ChatFragment();
                        setTitle("Chats");
                        break;

                    case R.id.nav_profile:
                        editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();

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

        }else {
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
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }


    public boolean isConnected(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            android.net.NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())){
                return true;
            }else {
                return false;
            }

        }else {
            return false;
        }
    }

    public AlertDialog.Builder buildDialog(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No network connection");
        builder.setMessage("No internet connection. Connect to the internet and try again");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder;
    }

    private void getFollowers(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = sharedPreferences.getString("profileid", "none");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference referencel = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("following");
        referencel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
