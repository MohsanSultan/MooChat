package com.example.moo_chat.moochat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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
        return true;
    }

    private void GoToStart() {
        Intent loginIntent = new Intent(MainActivity.this , LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
