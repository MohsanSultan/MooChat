package com.example.moo_chat.moochat;

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


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    private SectionPagerAdapter myPagerAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        InitFields();

    }

    private void InitFields() {

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Moo Chat");

        myViewPager = findViewById(R.id.tab_pager);

        myPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myPagerAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setTabTextColors(Color.GRAY,Color.MAGENTA);
        myTabLayout.setupWithViewPager(myViewPager);
        

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
          GoToStart();
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
             FirebaseAuth.getInstance().signOut();
             GoToStart();
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
            deleteAccount();
        }
        return true;
    }
    

    private void deleteAccount() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Your account has been deleted ..!", Toast.LENGTH_LONG).show();
                    GoToStart();
                } else {
                    Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_LONG).show();
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
