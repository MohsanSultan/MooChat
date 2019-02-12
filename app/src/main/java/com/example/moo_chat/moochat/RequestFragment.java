package com.example.moo_chat.moochat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {

    private View mMainView;
    private RecyclerView mRequestList;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private DatabaseReference mReqDatabase;
    private DatabaseReference myRootDatabase;
    private DatabaseReference mUsersDatabase;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_request, container, false);

        mRequestList = mMainView.findViewById(R.id.friends_req_list_view);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req").child(mCurrent_user_id);
        mReqDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        friendsRequestRecyclerAdopter();

        return mMainView;

    }

    private void friendsRequestRecyclerAdopter() {

        FirebaseRecyclerAdapter<FriendRequests, FriendsRequestViewHolder> friendRequestRecyclerViewAdapter = new FirebaseRecyclerAdapter
                <FriendRequests, FriendsRequestViewHolder>(

                FriendRequests.class,
                R.layout.single_user_view_layout,
                FriendsRequestViewHolder.class,
                mReqDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsRequestViewHolder friendsRequestViewHolder, FriendRequests requests, int i) {

                if (requests.getRequest_type().equals("received"))
                {
                final String list_user_id = getRef(i).getKey();

                // -------------------  get user name , img  . -----------------------------------

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        final String userStatus = dataSnapshot.child("status").getValue().toString();
                        String userThumbImg = dataSnapshot.child("thumb_img").getValue().toString();

//                        if (dataSnapshot.hasChild("online")) {
//
//                            String userOnline = dataSnapshot.child("online").getValue().toString();
//                            friendsViewHolder.setUserOnline(userOnline);
//
//                        }

                        friendsRequestViewHolder.setUserName(userName);
                        friendsRequestViewHolder.setUserStatus(userStatus);
                        friendsRequestViewHolder.setUserImg(userThumbImg, getContext());



// -----------------------------------------------------after get and set data---------------------------------------------------------

                        friendsRequestViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                        //Click Event for each item.
                                            Intent profileIntent = new Intent(getContext(), UsersProfileActivity.class);
                                            profileIntent.putExtra("from_user_id", list_user_id);
                                            startActivity(profileIntent);
                                    }
                                });

                            }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                    }
                    else {
                    friendsRequestViewHolder.setUserName("Ops");
                    friendsRequestViewHolder.setUserStatus("There Is No Friend Request Yet!");
                }

            }
        };

        mRequestList.setAdapter(friendRequestRecyclerViewAdapter);

    }



// -------------------------------- Friends Holder Class -----------------------------------------

    public static class FriendsRequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsRequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setUserName(String name){

            TextView userNameView = mView.findViewById(R.id.alluser_name);
            userNameView.setText(name);

        }
        public void setUserStatus(String status){

            TextView userStatusView = mView.findViewById(R.id.alluser_status);
            userStatusView.setText(status);

        }

        public void setUserImg(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.alluser_img_view);
            Picasso.get().load(thumb_image).placeholder(R.drawable.user_avatar).into(userImageView);

        }


    }


}
