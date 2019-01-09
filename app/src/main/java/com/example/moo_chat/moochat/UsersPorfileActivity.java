package com.example.moo_chat.moochat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UsersPorfileActivity extends AppCompatActivity implements View.OnClickListener{

    TextView userProfileName , userProfileStatus , userProfileTotalFriends;
    ImageView userProfileImg;
    Button sendRequestbtn;

    String friendStatus;

    ProgressDialog pDialog;

    private DatabaseReference myDatabaseRef , myFriendReqDatabase;
    private FirebaseUser myCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_porfile);

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

        userProfileName = findViewById(R.id.user_profile_name);
        userProfileStatus = findViewById(R.id.user_profile_status);
        userProfileTotalFriends = findViewById(R.id.user_profile_total_friends);
        userProfileImg = findViewById(R.id.user_profile_img);

        sendRequestbtn = findViewById(R.id.profile_send_req_btn);
        sendRequestbtn.setOnClickListener(this);

        myDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String userName = dataSnapshot.child("name").getValue().toString();
                String userStatus = dataSnapshot.child("status").getValue().toString();
                String userImage = dataSnapshot.child("image").getValue().toString();

                userProfileName.setText(userName);
                userProfileStatus.setText(userStatus);
                Picasso.get().load(userImage).placeholder(R.drawable.user_avatar).into(userProfileImg);
                pDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            //Send request Button
            case R.id.profile_send_req_btn:
                String userId = getIntent().getStringExtra("user_data");

                if (friendStatus.equals("not_friend")){

                    myFriendReqDatabase.child(myCurrentUser.getUid()).child(userId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                myFriendReqDatabase.child(userId).child(myCurrentUser.getUid()).child("request_type")
                                .setValue("recieved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(UsersPorfileActivity.this, "Sent", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }else {
                                Toast.makeText(UsersPorfileActivity.this, "Some Errror", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }

                break;
        }
    }
}
