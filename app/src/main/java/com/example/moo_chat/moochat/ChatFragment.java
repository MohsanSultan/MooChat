package com.example.moo_chat.moochat;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabase;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference myRootDatabase;
    private DatabaseReference myOnlineRef;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;
    FloatingActionButton addUsersBtn;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chat, container, false);

        mConvList = mMainView.findViewById(R.id.conv_list);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            myRootDatabase = FirebaseDatabase.getInstance().getReference();
            myOnlineRef = FirebaseDatabase.getInstance().getReference().child("UsersOnlineStatus");
        }
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mConvDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrent_user_id);
        mConvDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrent_user_id);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        addUsersBtn = mMainView.findViewById(R.id.add_chat_users_btn);
        addUsersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent allUsersIntent = new Intent(getActivity() , AllUsersActivity.class);
                startActivity(allUsersIntent);
            }
        });

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = mConvDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.chat_single_user_layout,
                ConvViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {

                final String list_user_id = getRef(i).getKey();

                Query lastMessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        convViewHolder.setMessage(data, conv.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_img").getValue().toString();

                        myOnlineRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("online")) {

                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    convViewHolder.setUserOnline(userOnline , getContext());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        convViewHolder.setName(userName);
                        convViewHolder.setUserImage(userThumb, getContext());

                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
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

                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);

                                chatIntent.putExtra("from_user_id", list_user_id);
                                chatIntent.putExtra("user_name", userName);

                                startActivity(chatIntent);
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

        mConvList.setAdapter(firebaseConvAdapter);
    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = mView.findViewById(R.id.alluser_status);
            userStatusView.setText(message);

            ImageView newMegIcon = mView.findViewById(R.id.new_msg_icon);

            if(!isSeen){
                userStatusView.setTextColor(Color.parseColor("#9600ff"));
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
                newMegIcon.setVisibility(View.VISIBLE);

            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
                newMegIcon.setVisibility(View.GONE);
            }

        }

        public void setName(String name){

            TextView userNameView = mView.findViewById(R.id.chat_alluser_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView =  mView.findViewById(R.id.alluser_img_view);
            Picasso.get().load(thumb_image).placeholder(R.drawable.user_avatar).into(userImageView);

        }

        public void setUserOnline(String online_status , Context ctx) {

            ImageView userOnlineView = mView.findViewById(R.id.user_online_icon);
            TextView userTimeView = mView.findViewById(R.id.txtTime);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);
                userTimeView.setVisibility(View.INVISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);
                userTimeView.setVisibility(View.VISIBLE);
                GetTimeAgo getTimeAgo = new GetTimeAgo();

                long lastTime = Long.parseLong(online_status);

                String lastSeenTime = getTimeAgo.getTimeAgo(lastTime, ctx);

                userTimeView.setText(lastSeenTime);

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
