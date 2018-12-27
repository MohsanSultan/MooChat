package com.example.moo_chat.moochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {

    EditText SignInEmaail , SignInPass;
    Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initField();

    }

    private void initField() {
                SignInEmaail = findViewById(R.id.signin_email);

                SignInPass = findViewById(R.id.signin_pass);

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
