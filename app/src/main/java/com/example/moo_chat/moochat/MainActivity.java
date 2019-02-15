package com.example.moo_chat.moochat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser FirebaseCurrentUser;
    private DatabaseReference myDatabaseRef;
    private DatabaseReference myCurrentUserRef;
    private AlertDialog alertDialog;

    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    private SectionPagerAdapter myPagerAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitFields();

    }

    private void InitFields() {

        mAuth = FirebaseAuth.getInstance();
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        if (mAuth.getCurrentUser() != null) {

            myCurrentUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MyMooChat");

        myViewPager = findViewById(R.id.tab_pager);
        myPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myPagerAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setTabTextColors(Color.GRAY,Color.WHITE);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseCurrentUser = mAuth.getCurrentUser();

        if (FirebaseCurrentUser == null)
        {
            GoToStart();
        }else {

            myCurrentUserRef.child("online").setValue("true");
//            myCurrentUserRef.child("offline").setValue("false");

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentUserRef.child("online").setValue("true");
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            myCurrentUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.main_logout_btn) {
             confirmLogout();
         }
         if (item.getItemId() == R.id.account_Bar_btn)
         {
             Intent settingInt = new Intent(MainActivity.this , UserSettingActivity.class);
             startActivity(settingInt);
         }
        if (item.getItemId() == R.id.all_users_bar_btn)
        {
            Intent settingInt = new Intent(MainActivity.this , AllUsersActivity.class);
            startActivity(settingInt);
        }
        if (item.getItemId() == R.id.main_del_ac_btn)
        {
            confirmDeleteAccount();
        }
        return true;
    }

    private void confirmDeleteAccount() {
        alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(R.drawable.main_logo)
                .setTitle("Delete Account !")
                .setMessage("Your Account Will Be Deleted Permanently , You Agree ? ...")
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                })
                //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        Toast.makeText(getApplicationContext(), "Hurey ! Let's Chat.........", Toast.LENGTH_LONG).show();
                        alertDialog.cancel();
                    }
                })
                .show();
    }

    private void confirmLogout() {
            alertDialog = new AlertDialog.Builder(this)
                    //set icon
                 .setIcon(R.drawable.main_logo)
                    .setTitle("LogOut !")
                    .setMessage("Do You Want To Logout ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            myCurrentUserRef.child("online").setValue(ServerValue.TIMESTAMP);
                            FirebaseAuth.getInstance().signOut();
                            GoToStart();
                        }
                    })
                    //set negative button
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what should happen when negative button is clicked
                            Toast.makeText(getApplicationContext(), "Hurey ! Let's Chat.........", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        }
                    })
                    .show();
//
//            alertDialog.show();
        }


    private void deleteAccount() {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    myDatabaseRef.child(currentUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Your account has been deleted ..!", Toast.LENGTH_LONG).show();
                                GoToStart();
                            }else
                                Toast.makeText(MainActivity.this, "SomeThing Is wrong. please SignIn again ,thank you....", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(MainActivity.this, "You should log-out and SignIn again to Delete Your Account (for security purpose.).... Thank You.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void GoToStart() {
        Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
