package com.vampyr.demo.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vampyr.demo.PostsActivity;
import com.vampyr.demo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private ImageView addImage;

    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_post, container, false);

        addImage = view.findViewById(R.id.add);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostsActivity.class));
            }
        });

        return view;
    }

}
