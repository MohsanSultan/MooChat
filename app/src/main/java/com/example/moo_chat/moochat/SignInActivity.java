package com.example.moo_chat.moochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    EditText SignInEmaail , SignInPass;
    FloatingActionButton signInBtn , backBtn;
    ProgressDialog pDialog;
    private Toolbar mToolbar;

    private DatabaseReference myDbRef;

    private FirebaseAuth mAuth;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initField();
    }


    private void initField() {
        pDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        pDialog.setTitle("Entering User - Please Wait !");
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);


        myDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

                SignInEmaail = findViewById(R.id.signin_email);
                SignInPass = findViewById(R.id.signin_pass);

        mAuth = FirebaseAuth.getInstance();

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.setOnClickListener(v -> {
            String Email = SignInEmaail.getText().toString();
            String Password = SignInPass.getText().toString();

            if ((!Email.isEmpty()) || (!Password.isEmpty())){
                    signInUser(Email , Password);
            } else Toast.makeText(this, "Kindly Fill All Fields ! ", Toast.LENGTH_LONG).show();

        });

        backBtn = findViewById(R.id.signin_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(SignInActivity.this , LoginActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }

    private void signInUser(String email, String password) {

        pDialog.show();



            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    pDialog.dismiss();

                    if (mAuth.getCurrentUser() != null) {
                        String current_user_id = mAuth.getCurrentUser().getUid();

                        String device_token = FirebaseInstanceId.getInstance().getToken();

                        myDbRef.child(current_user_id).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    }
                } else {
                    pDialog.hide();
                    Toast.makeText(SignInActivity.this, "User Not Exist OR wrong Password...! Please Check Again", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


