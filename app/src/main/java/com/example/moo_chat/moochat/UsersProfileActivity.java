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
import java.util.Map;

public class UsersProfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView userProfileName , userProfileStatus , userProfileTotalFriends;
    ImageView userProfileImg , userProfileImageThumb;
    Button sendRequestbtn , declineFriendReqBtn;

    String friendStatus;

    ProgressDialog pDialog;

    private DatabaseReference myDatabaseRef , myFriendReqDatabase , myFriendsDatabase;
    private DatabaseReference myNotificationDatabase;
    private DatabaseReference myRootDatabase;
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

        String other_userId = getIntent().getStringExtra("from_user_id");


        myRootDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(other_userId);
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

        // --------------------- Friend Request sent $ received Feature ------------
        // --------------------- put data via Ref DB -------------------

        myFriendReqDatabase.child(myCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(other_userId)){
                    String req_type = dataSnapshot.child(other_userId).child("request_type").getValue().toString();

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
                            if (dataSnapshot.hasChild(other_userId)){

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

// ---------------------------------- ON CLICK -----------------------------------------
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //Send request Button
            case R.id.profile_send_req_btn: {

                // If Friend-State in == Not fRIEND THEN THIS. -----------------
                String otherUserId = getIntent().getStringExtra("from_user_id");
                sendRequestbtn.setEnabled(false);

                if (friendStatus.equals("not_friend")) {

                    sendNewFriendRequest(otherUserId);
                }
                // If Friend-State sent ==  fRIEND THEN THIS CHANCEL ENABLE. -----------------
                if (friendStatus.equals("req_sent")){

                    cancelRequestSent(otherUserId);
                }

                // ------------ Request status ----------
                if (friendStatus.equals("req_received")){

                    acceptDeclineRequest(otherUserId);
                }

                if (friendStatus.equals("friends")){

                    // if already friend , then unfriend onClick
                    unFriendPerson(otherUserId);
                }

            }
            break;
            case R.id.profile_decline_req_btn:
            {
                String otherUserId = getIntent().getStringExtra("from_user_id");
                declineFriendReqBtn.setEnabled(false);
                DeclineFriendRequest(otherUserId);
            }
        }
    }

    // ---------------------------- FUNCTIONS ------------------------------------------
// --------------------- put data via MAPs and HASHMAP -------------------
    private void unFriendPerson(String otherUserId) {

        Map unFriendMap = new HashMap();
        unFriendMap.put("Friends/" + myCurrentUser.getUid() + "/" + otherUserId , null);
        unFriendMap.put("Friends/" + otherUserId + "/" + myCurrentUser.getUid() , null);

        myRootDatabase.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null){
                    friendStatus = "not_friend";
                    sendRequestbtn.setText("Send Friend Request");

                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                    declineFriendReqBtn.setEnabled(false);

                } else {
                    String error = databaseError.getMessage();

                    Toast.makeText(UsersProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                sendRequestbtn.setEnabled(true);
            }
        });
    }

    private void acceptDeclineRequest(String otherUserId) {

        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Map friendsMap = new HashMap();
        friendsMap.put("Friends/" + myCurrentUser.getUid() + "/" + otherUserId + "/date" , currentDate);
        friendsMap.put("Friends/" + otherUserId + "/" + myCurrentUser.getUid() + "/date" , currentDate );

        friendsMap.put("friend_req/" + myCurrentUser.getUid() + "/" + otherUserId , null);
        friendsMap.put("friend_req/" + otherUserId + "/" + myCurrentUser.getUid() , null);

        myRootDatabase.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if (databaseError == null){
                    friendStatus = "friend";
                    sendRequestbtn.setText("Unfriend This Person");

                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                    declineFriendReqBtn.setEnabled(false);
                } else {
                    String error = databaseError.getMessage();
                    Toast.makeText(UsersProfileActivity.this, error , Toast.LENGTH_SHORT).show();
                }
                sendRequestbtn.setEnabled(true);
            }
        });
    }

    private void cancelRequestSent(String otherUserId) {

        Map cancelRequestMap = new HashMap();

        cancelRequestMap.put("friend_req/" + myCurrentUser.getUid() + "/" + otherUserId , null);
        cancelRequestMap.put("friend_req/" + otherUserId + "/" + myCurrentUser.getUid() , null);

        myRootDatabase.updateChildren(cancelRequestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    friendStatus = "not_friend";
                    sendRequestbtn.setText("Send Friend Request");

                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                    declineFriendReqBtn.setEnabled(false);
                }else {
                    String error = databaseError.getMessage();
                    Toast.makeText(UsersProfileActivity.this, error , Toast.LENGTH_SHORT).show();
                }
                sendRequestbtn.setEnabled(true);
            }

        });
    }

    private void sendNewFriendRequest(String otherUserId) {

        DatabaseReference newNotificationref = myRootDatabase.child("notification").child(otherUserId).push();
        String newNotificationId = newNotificationref.getKey();

        // current user request to DB.
        HashMap<String, String> notificationData = new HashMap<>();
        notificationData.put("from", myCurrentUser.getUid());
        notificationData.put("type", "request");

        // other user recieved request to DB.
        Map sendRequestMap = new HashMap();
        sendRequestMap.put("friend_req/" + myCurrentUser.getUid() + "/" + otherUserId + "/request_type", "sent");
        sendRequestMap.put("friend_req/" + otherUserId + "/" + myCurrentUser.getUid() + "/request_type", "received");
        sendRequestMap.put("notification/" + otherUserId + "/" + newNotificationId, notificationData);

        myRootDatabase.updateChildren(sendRequestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                if(databaseError != null){

                    Toast.makeText(UsersProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();
                } else {
                    friendStatus = "req_sent";
                    sendRequestbtn.setText("Cancel Friend Request");
                }
                sendRequestbtn.setEnabled(true);
            }
        });
    }

    private void DeclineFriendRequest(String otherUserId) {
        Map declineRequestMap = new HashMap();

        declineRequestMap.put("friend_req/" + myCurrentUser.getUid() + "/" + otherUserId , null);
        declineRequestMap.put("friend_req/" + otherUserId + "/" + myCurrentUser.getUid() , null);

        myRootDatabase.updateChildren(declineRequestMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null){
                    friendStatus = "not_friend";
                    sendRequestbtn.setText("Send Friend Request");

                    declineFriendReqBtn.setVisibility(View.INVISIBLE);
                    declineFriendReqBtn.setEnabled(false);
                }else {
                    String error = databaseError.getMessage();
                    Toast.makeText(UsersProfileActivity.this, error , Toast.LENGTH_SHORT).show();
                }
                sendRequestbtn.setEnabled(true);
            }

        });
    }

}
