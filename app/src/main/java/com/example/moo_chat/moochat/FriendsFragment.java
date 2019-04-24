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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference myRootDatabase;
    private DatabaseReference myOnlineRef;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = mMainView.findViewById(R.id.friends_list_view);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            myRootDatabase = FirebaseDatabase.getInstance().getReference();
            mCurrent_user_id = mAuth.getCurrentUser().getUid();
            myOnlineRef = FirebaseDatabase.getInstance().getReference().child("UsersOnlineStatus");
        }
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        friendsRecyclerAdopter();

        return mMainView;
    }

    // -------------------------------- Friends list Adopter ----------------------------------------
    private void friendsRecyclerAdopter() {

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.friends_list_view_layout,
                FriendsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {

                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();

                // -------------------  get user name , online status , img , date . -----------------------------------

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumbImg = dataSnapshot.child("thumb_img").getValue().toString();

                        myOnlineRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("online")) {

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    friendsViewHolder.setUserOnline(userOnline);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumbImg, getContext());

// -----------------------------------------------------after get and set data---------------------------------------------------------

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String accountStatus = dataSnapshot.child("accountStatus").getValue().toString();

                                if (accountStatus.equals("deActive")){
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setTitle("User DeActivated")
                                            .setMessage("User Deleted this Account, Please UnFriend !")

                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    unFriendPerson(list_user_id);
                                                }
                                            })
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .show();

                                }else {

                                    CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                    builder.setTitle("Select Options");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            //Click Event for each item.
                                            if(i == 0){

                                                Intent profileIntent = new Intent(getContext(), UsersProfileActivity.class);
                                                profileIntent.putExtra("from_user_id", list_user_id);
                                                startActivity(profileIntent);

                                            }

                                            if(i == 1){

                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                chatIntent.putExtra("from_user_id", list_user_id);
                                                chatIntent.putExtra("user_name", userName);
                                                startActivity(chatIntent);

                                            }

                                        }
                                    });

                                    builder.show();
                                }

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mFriendsList.setAdapter(friendsRecyclerViewAdapter);

    }



// -------------------------------- Friends Holder Class -----------------------------------------

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = mView.findViewById(R.id.alluser_status);
            userStatusView.setText(date);

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.alluser_name);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = mView.findViewById(R.id.alluser_img_view);
            Picasso.get().load(thumb_image).placeholder(R.drawable.user_avatar).into(userImageView);
        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = mView.findViewById(R.id.user_online_icon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void  unFriendPerson(String otherUserId) {

        Map unFriendMap = new HashMap();
        unFriendMap.put("Friends/" + mCurrent_user_id + "/" + otherUserId , null);
        unFriendMap.put("Friends/" + otherUserId + "/" + mCurrent_user_id , null);
        unFriendMap.put("messages/" + mCurrent_user_id + "/" + otherUserId , null);
        unFriendMap.put("messages/" + otherUserId + "/" + mCurrent_user_id , null);
        unFriendMap.put("Chat/" + mCurrent_user_id + "/" + otherUserId , null);
        unFriendMap.put("Chat/" + otherUserId + "/" + mCurrent_user_id , null);

        myRootDatabase.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null){
                    Toast.makeText(getContext(), "UnFriend Successfully ...", Toast.LENGTH_SHORT).show();

                } else {
                    String error = databaseError.getMessage();

                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
