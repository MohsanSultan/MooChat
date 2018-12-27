package com.example.moo_chat.moochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText regName, regEmail, regPassword;
    Button RegAcBtn;

    //Firebase
    private FirebaseAuth mAuth;

    // ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        // Check Internet connection here. ---- code latter.
        initField();
    }

    private void initField() {
        regName = findViewById(R.id.reg_name);

        regEmail = findViewById(R.id.reg_email);

        regPassword = findViewById(R.id.reg_pass);

        RegAcBtn = findViewById(R.id.reg_btn);
        RegAcBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.reg_btn:
                registerAccountBtn();
                break;
        }
    }

    private void  registerAccountBtn(){

        String displayName =  regName.getText().toString();
        String displayEmail = regEmail.getText().toString();
        String displayPassword = regPassword.getText().toString();

        registerUser(displayName , displayEmail , displayPassword);
    }

    private void registerUser(String displayName, String displayEmail, String displayPassword) {

        mAuth.createUserWithEmailAndPassword(displayEmail, displayPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isComplete()){
                    Intent regIntent = new Intent(RegisterActivity.this , MainActivity.class);
                    startActivity(regIntent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "You are having some error.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
