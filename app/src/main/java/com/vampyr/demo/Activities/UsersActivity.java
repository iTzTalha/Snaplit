package com.vampyr.demo.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Adapter.MyPhotoAdapter;
import com.vampyr.demo.Fragments.PostDetailsFragment;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {


    ImageView goBack, btn_share;
    CircleImageView image_profile;
    TextView follwers, following, posts, bio, username,FollowersTextView,FollowingTextVeiw;
    Button btn_profile, chatUser;

    FirebaseUser firebaseUser;
    String profileid;

    RecyclerView recyclerView;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        image_profile = findViewById(R.id.image_profile);
        goBack = findViewById(R.id.goBack);
        follwers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        btn_profile = findViewById(R.id.edit_profile);
        posts = findViewById(R.id.posts);
        bio = findViewById(R.id.bio);
        username = findViewById(R.id.user_name);
        btn_share = findViewById(R.id.share);
        chatUser = findViewById(R.id.chatuser);
        FollowersTextView = findViewById(R.id.followersTextView);
        FollowingTextVeiw = findViewById(R.id.followingTextView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = sharedPreferences.getString("profileid", "none");

        /************
         * recyclerView for Posts
         */
        recyclerView = findViewById(R.id.recyler_view_myPosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getApplicationContext(), postList);
        recyclerView.setAdapter(myPhotoAdapter);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userinfo();
        getFollowers();
        getNrPosts();
        myPhotos();

        if (profileid.equals(firebaseUser.getUid())){

            btn_profile.setVisibility(View.INVISIBLE);
            chatUser.setText("Edit profile");
            chatUser.setTextColor(getResources().getColor(R.color.colorPrimary));
            chatUser.setBackgroundResource(R.drawable.rounded_button_profile);
            chatUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(UsersActivity.this,ProfileActivity.class));
                    finish();
                }
            });

        }else {

            checkFollow();
        }

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = btn_profile.getText().toString();

                if (btn.equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                } else if (btn.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();


                }
            }
        });

        FollowersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });

        FollowingTextVeiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });

        follwers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });

       following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });

    }

    private void userinfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (getApplicationContext() == null) {
                    return;
                }

                Users user = dataSnapshot.getValue(Users.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(profileid).exists()){
                    btn_profile.setText("following");
                    btn_profile.setTextColor(getResources().getColor(R.color.colorPrimary));
                    btn_profile.setBackgroundResource(R.drawable.rounded_button_profile);

                }else {
                    btn_profile.setText("follow");
                    btn_profile.setTextColor(getResources().getColor(R.color.WHITE));
                    btn_profile.setBackgroundResource(R.drawable.rounded_userbutton_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                follwers.setText(""+dataSnapshot.getChildrenCount());
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

    private void getNrPosts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i=0;
                for (DataSnapshot Snapshot : dataSnapshot.getChildren()){

                    Post posts = Snapshot.getValue(Post.class);
                    if (posts.getPublisher().equals(profileid)){
                        i++;
                    }
                }

                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPhotos(){

        DatabaseReference reference =FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }

                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
