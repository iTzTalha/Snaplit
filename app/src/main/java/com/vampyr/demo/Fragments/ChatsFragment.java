package com.vampyr.demo.Fragments;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.fragment.app.Fragment;

import com.vampyr.demo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chats, container, false);
        return view;
    }
}
