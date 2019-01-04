package com.example.moo_chat.moochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import org.w3c.dom.Text;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText regName, regEmail, regPassword;
    Button RegAcBtn;
    ProgressDialog pDialog;

    private Toolbar mToolbar;

    //Firebase
    private FirebaseAuth mAuth;


    // ---

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register New User");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


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
        }
    }

    private void  registerAccountBtn(){

        String displayName =  regName.getText().toString();
        String displayEmail = regEmail.getText().toString();
        String displayPassword = regPassword.getText().toString();

        if ((displayName.isEmpty()) || (displayEmail.isEmpty()) || (displayPassword.isEmpty()))
        {
            Toast.makeText(this, "Kindly Fill All Fields ! ", Toast.LENGTH_LONG).show();
        }else {
            pDialog = new ProgressDialog(this);
            pDialog.setTitle("Registering New User !");
            pDialog.setMessage("Loading...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            registerUser(displayName , displayEmail , displayPassword);
        }
    }

    private void registerUser(String displayName, String displayEmail, String displayPassword) {

        mAuth.createUserWithEmailAndPassword(displayEmail, displayPassword).addOnCompleteListener(task -> {

            if (task.isComplete()){
                Intent regIntent = new Intent(RegisterActivity.this , MainActivity.class);
                regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(regIntent);
                pDialog.dismiss();
                finish();
            } else {
                pDialog.hide();
                Toast.makeText(RegisterActivity.this, "You are having some error.", Toast.LENGTH_SHORT).show();
            }

        });


    }
}
