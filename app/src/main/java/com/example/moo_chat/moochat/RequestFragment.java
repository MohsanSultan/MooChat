package com.example.moo_chat.moochat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RequestFragment extends Fragment {

    private View mMainView;
    private RecyclerView mRequestList;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mFriendsDatabase;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       return inflater.inflate(R.layout.fragment_request, container, false);
//        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
//
//        mRequestList = mMainView.findViewById(R.id.friends_req_list_view);
//        mAuth = FirebaseAuth.getInstance();
//        mCurrent_user_id = mAuth.getCurrentUser().getUid();
//
//        return mMainView;


    }
}
