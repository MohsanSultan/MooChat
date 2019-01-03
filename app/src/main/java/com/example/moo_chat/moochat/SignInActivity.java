package com.example.moo_chat.moochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    EditText SignInEmaail , SignInPass;
    Button signInBtn;
    ProgressDialog pDialog;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("LogIn My-MooChat");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        initField();

    }

    private void initField() {
                SignInEmaail = findViewById(R.id.signin_email);
                SignInPass = findViewById(R.id.signin_pass);

        mAuth = FirebaseAuth.getInstance();

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(v -> {
            String Email = SignInEmaail.getText().toString();
            String Password = SignInPass.getText().toString();

            if ((!Email.isEmpty()) || (!Password.isEmpty())){
                    checkUser(Email , Password);
            } else Toast.makeText(this, "Kindly Fill All Fields ! ", Toast.LENGTH_LONG).show();

        });
    }

    private void checkUser(String email, String password) {

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Entering User !");
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                pDialog.dismiss();
                Intent mainIntent = new Intent(SignInActivity.this , MainActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                pDialog.hide();
                Toast.makeText(SignInActivity.this, "User Not Exist ! Check Again Please", Toast.LENGTH_LONG).show();
            }

        });

    }


}
