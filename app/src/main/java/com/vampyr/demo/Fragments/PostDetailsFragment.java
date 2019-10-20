package com.vampyr.demo.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Adapter.CommentAdapter;
import com.vampyr.demo.Adapter.PostAdapter;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostDetailsFragment extends Fragment {

    ImageView btn_back;

    String postID;
    RecyclerView recyclerView;
    PostAdapter postAdapter;
    List<Post> postList;


    public PostDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);

        btn_back = view.findViewById(R.id.btn_photoback);

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postID = preferences.getString("postID", "none");

        recyclerView = view.findViewById(R.id.recyler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPost();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        return view;
    }

    

    private void readPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();

                Post post = dataSnapshot.getValue(Post.class);

                postList.add(post);

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
