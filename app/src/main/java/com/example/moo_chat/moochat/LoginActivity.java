package com.example.moo_chat.moochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    private Button CreateNewAcBtn , SignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initField();
    }
private void initField() {
        // Register Button
    CreateNewAcBtn = findViewById(R.id.create_new_ac);
    CreateNewAcBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
            startActivity(intent);
        }
    });
    // Sign IN button
    SignInBtn = findViewById(R.id.sign_in_id);
    SignInBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this , SignInActivity.class);
            startActivity(intent);
        }
    });

}

}
