package com.example.moo_chat.moochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AllUsersActivity extends AppCompatActivity {



    private Toolbar mToolbar;
    private RecyclerView myUsersList ;

    private FirebaseAuth mAuth;
    private DatabaseReference myDatabaseRef;
    private DatabaseReference myOnlineRef;
    FirebaseUser FirebaseCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#830ddb'>ALL USERS</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initFields();
    }

    private void initFields() {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            myOnlineRef = FirebaseDatabase.getInstance().getReference().child("UsersOnlineStatus").child(mAuth.getCurrentUser().getUid());

        }
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myUsersList = findViewById(R.id.users_list_view);
        myUsersList.setHasFixedSize(true);
        myUsersList.setLayoutManager(new LinearLayoutManager(this));

        myRecyclerAdopter();
    }

    private void myRecyclerAdopter() {

        FirebaseRecyclerAdapter<AllUsers , UsersViewHolderClass> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, UsersViewHolderClass>(
                AllUsers.class,
                R.layout.all_users_view_layout,
                UsersViewHolderClass.class,
                myDatabaseRef
        ) {
            @Override
            protected void populateViewHolder(UsersViewHolderClass viewHolder, AllUsers model, int position) {

                Log.e( "mohsinid",model.getId() );

                String current_user_id = mAuth.getCurrentUser().getUid();

                Log.e( "mohsinUserId",model.getId());

                UsersViewHolderClass.setValues(model.getName() , model.getStatus() , model.getThumb_img() );
                final  String userId = getRef(position).getKey();

                UsersViewHolderClass.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent userProfileIntent = new Intent(AllUsersActivity.this , UsersProfileActivity.class);
                        userProfileIntent.putExtra("from_user_id" , userId);
                        startActivity(userProfileIntent);
                    }
                });
                        // Hide Current User -------------------------------------
                        if (model.getId().equals(current_user_id) || model.getAccountStatus().equals("deActive")){
                            viewHolder.itemView.setVisibility(View.GONE);
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)viewHolder.itemView.getLayoutParams();
                            layoutParams.height = 0;
                            viewHolder.itemView.setLayoutParams(layoutParams);
                        }
                        // ------------------------------------------------------------------------



            }
        };
        myUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseCurrentUser = mAuth.getCurrentUser();

        if (FirebaseCurrentUser == null)
        {
            Toast.makeText(this, "You Are Not Logged In", Toast.LENGTH_SHORT).show();
        }else {
            myOnlineRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myOnlineRef.child("online").setValue("true");
    }
    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            myOnlineRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

//    public static class UsersViewHolderClass extends RecyclerView.ViewHolder {
//
//        static View myView;
//
//        public UsersViewHolderClass(@NonNull View itemView) {
//            super(itemView);
//
//            myView = itemView;
//        }
//        public static void setValues(String name, String status){
//            TextView singleViewName;
//            TextView singleViewStatus;
//
//
//            singleViewName = myView.findViewById(R.id.alluser_name);
//            singleViewName.setText(name);
//
//            singleViewStatus = myView.findViewById(R.id.alluser_status);
//            singleViewStatus.setText(status);
//
//        }
//    }
}
