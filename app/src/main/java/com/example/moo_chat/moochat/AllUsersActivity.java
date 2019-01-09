package com.example.moo_chat.moochat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllUsersActivity extends AppCompatActivity {



    private Toolbar mToolbar;
    private RecyclerView myUsersList ;


    private DatabaseReference myDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        mToolbar = findViewById(R.id.users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Moo Users ");

        initFields();
    }

    private void initFields() {

        myUsersList = findViewById(R.id.users_list_view);
        myUsersList.setHasFixedSize(true);
        myUsersList.setLayoutManager(new LinearLayoutManager(this));
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AllUsers , UsersViewHolderClass> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AllUsers, UsersViewHolderClass>(
                AllUsers.class,
                R.layout.single_user_view_layout,
                UsersViewHolderClass.class,
                myDatabaseRef

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolderClass viewHolder, AllUsers model, int position) {

                UsersViewHolderClass.setValues(model.getName() , model.getStatus());
            }
        };

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
