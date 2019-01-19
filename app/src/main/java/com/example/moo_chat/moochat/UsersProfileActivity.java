package com.example.moo_chat.moochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class UsersProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView userProfileName , userProfileStatus , userProfileTotalFriends;
    ImageView userProfileImg , userProfileImageThumb;
    Button sendRequestbtn , declineFriendReqBtn;

    String friendStatus;

    ProgressDialog pDialog;

    private DatabaseReference myDatabaseRef , myFriendReqDatabase , myFriendsDatabase;
    private DatabaseReference myNotificationDatabase;
    private FirebaseUser myCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        initFields();
    }

    private void initFields() {

        friendStatus = "not_friend";

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Loading User !");
        pDialog.setMessage("Please Wait...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        String userId = getIntent().getStringExtra("user_data");

        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        myFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("friend_req");
        myCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        myFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        myNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notification");

        userProfileName = findViewById(R.id.user_profile_name);
        userProfileStatus = findViewById(R.id.user_profile_status);
        userProfileTotalFriends = findViewById(R.id.user_profile_total_friends);

        userProfileImageThumb = findViewById(R.id.user_profile_img_thumb);
        userProfileImageThumb.setOnClickListener(this);


        sendRequestbtn = findViewById(R.id.profile_send_req_btn);
        sendRequestbtn.setOnClickListener(this);

        declineFriendReqBtn = findViewById(R.id.profile_decline_req_btn);
        declineFriendReqBtn.setOnClickListener(this);

        declineFriendReqBtn.setVisibility(View.INVISIBLE);
        declineFriendReqBtn.setEnabled(false);

        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child("name").getValue().toString();
                String userStatus = dataSnapshot.child("status").getValue().toString();
                String userImage = dataSnapshot.child("image").getValue().toString();
                String userImageThumb = dataSnapshot.child("thumb_img").getValue().toString();

                userProfileName.setText(userName);
                userProfileStatus.setText(userStatus);
                Picasso.get().load(userImageThumb).placeholder(R.drawable.user_avatar).into(userProfileImageThumb);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // ---- Friend Request sent $ received Feature ------------

        myFriendReqDatabase.child(myCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)){
                    String req_type = dataSnapshot.child(userId).child("request_type").getValue().toString();

                    if (req_type.equals("received")){
                        friendStatus = "req_received";
                        sendRequestbtn.setText("Accept Friend Request");

                        declineFriendReqBtn.setVisibility(View.VISIBLE);
                        declineFriendReqBtn.setEnabled(true);

                    } else if (req_type.equals("sent")){

                        friendStatus = "req_sent";
                        sendRequestbtn.setText("Cancel Friend Request");

                        declineFriendReqBtn.setVisibility(View.INVISIBLE);
                        declineFriendReqBtn.setEnabled(false);
                    }
                    pDialog.dismiss();
                }else {
                    myFriendsDatabase.child(myCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(userId)){

                                friendStatus = "friends";
                                sendRequestbtn.setText("Unfriend This Person");

                                declineFriendReqBtn.setVisibility(View.INVISIBLE);
                                declineFriendReqBtn.setEnabled(false);

                                pDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            pDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        pDialog.dismiss();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //Send request Button
            case R.id.profile_send_req_btn: {

                // If Friend-State in == Not fRIEND THEN THIS. -----------------
                String otherUserId = getIntent().getStringExtra("user_data");
                sendRequestbtn.setEnabled(false);


                if (friendStatus.equals("not_friend")) {

                    myFriendReqDatabase.child(myCurrentUser.getUid()).child(otherUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                myFriendReqDatabase.child(otherUserId).child(myCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String , String> notificationDataMap = new HashMap<>();
                                        notificationDataMap.put("from" , myCurrentUser.getUid());
                                        notificationDataMap.put("type" , "request");

                                        myNotificationDatabase.child(otherUserId).push().setValue(notificationDataMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        friendStatus = "req_sent";
                                                        sendRequestbtn.setText("Cancel Friend Request");

                                                        declineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                        declineFriendReqBtn.setEnabled(false);

                                                        Toast.makeText(UsersProfileActivity.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    }
                                });
                            } else {
                                Toast.makeText(UsersProfileActivity.this, "Some Errror", Toast.LENGTH_SHORT).show();

                            }
                            sendRequestbtn.setEnabled(true);
                        }
                    });
                }

                // If Friend-State in ==  fRIEND THEN THIS CHANCEL ENABLE. -----------------
                if (friendStatus.equals("req_sent")){
                    myFriendReqDatabase.child(myCurrentUser.getUid()).child(otherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            myFriendReqDatabase.child(otherUserId).child(myCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendRequestbtn.setEnabled(true);
                                    friendStatus = "not_friend";
                                    sendRequestbtn.setText("Send Friend Request");

                                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                                    declineFriendReqBtn.setEnabled(false);
                                }
                            });
                        }
                    });

                }

                // ------------ Request status ----------
                if (friendStatus.equals("req_received")){
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    myFriendsDatabase.child(myCurrentUser.getUid()).child(otherUserId).setValue(currentDate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    myFriendsDatabase.child(otherUserId).child(myCurrentUser.getUid()).setValue(currentDate)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    myFriendReqDatabase.child(myCurrentUser.getUid()).child(otherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            myFriendReqDatabase.child(otherUserId).child(myCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    sendRequestbtn.setEnabled(true);
                                                                    friendStatus = "friend";
                                                                    sendRequestbtn.setText("Unfriend This Person");

                                                                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                                                                    declineFriendReqBtn.setEnabled(false);
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                }
                            });

                }

                if (friendStatus.equals("friends")){
                    myFriendsDatabase.child(myCurrentUser.getUid()).child(otherUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            myFriendsDatabase.child(otherUserId).child(myCurrentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendRequestbtn.setEnabled(true);
                                    friendStatus = "not_friend";
                                    sendRequestbtn.setText("Send Friend Request");
                                }
                            });
                        }
                    });

                }

            }
            break;
            case R.id.profile_decline_req_btn:
            {


            }

        }

    }

}
