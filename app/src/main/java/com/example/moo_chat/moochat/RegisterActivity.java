package com.example.moo_chat.moochat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements  View.OnClickListener{

    EditText regName, regEmail, regPassword, regConfirmPass;
    FloatingActionButton RegAcBtn ;
    FloatingActionButton backBtn;
    ProgressDialog pDialog;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference myDbRef , myDevTokenRef;


    // ---

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Check Internet connection here. ---- code latter.
        initField();
    }

    private void initField() {

        pDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        pDialog.setTitle("Registering New User - Please Wait !");
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);

        regName = findViewById(R.id.reg_name);

        regEmail = findViewById(R.id.reg_email);

        regPassword = findViewById(R.id.reg_pass);
        regConfirmPass = findViewById(R.id.reg_confirm_pass);


        backBtn = findViewById(R.id.reg_back_btn);
        backBtn.setOnClickListener(this);

        RegAcBtn = findViewById(R.id.reg_btn);
        RegAcBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.reg_btn:
                registerAccountBtn();
                break;

            case R.id.reg_back_btn: {
                onBackPressed();
                break;
            }

        }
    }

    private void  registerAccountBtn(){

        String displayName =  regName.getText().toString();
        String displayEmail = regEmail.getText().toString();
        String displayPassword = regPassword.getText().toString();
        String displayConfirmPassword = regConfirmPass.getText().toString();

        if ((displayName.isEmpty()) && (displayEmail.isEmpty()) && (displayPassword.isEmpty()))
        {
            Toast.makeText(this, "Kindly Fill All Fields ! ", Toast.LENGTH_LONG).show();
        }else if(doStringsMatch(displayPassword, displayConfirmPassword)){
            pDialog.show();
            registerUser(displayName , displayEmail , displayPassword);
        }
        else
            Toast.makeText(this, "Password Not Matched!.....", Toast.LENGTH_SHORT).show();
    }

    private boolean doStringsMatch(String s1, String s2) {
        return s1.equals(s2);
    }

    private void registerUser(String displayName, String displayEmail, String displayPassword) {

        mAuth.fetchProvidersForEmail(displayEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> taskEmailCheck) {

                boolean checkEmail = taskEmailCheck.getResult().getProviders().isEmpty();

                if (!checkEmail){
                    Toast.makeText(RegisterActivity.this, "This Email is already is use. thank you..!", Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }else {
                    mAuth.createUserWithEmailAndPassword(displayEmail, displayPassword).addOnCompleteListener(task -> {

                        if (task.isComplete()){

//                            sendVerificationEmail();
                            createNewUser(displayName);

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You are having some error.", Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            }
        });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Kindly Check Your Email Inbox to verify....", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Couldn't send email", Toast.LENGTH_SHORT).show();
                                pDialog.dismiss();
                            }
                        }
                    });
        }

    }

    private void createNewUser(String displayName) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserID = currentUser.getUid();

        String current_user_id = mAuth.getCurrentUser().getUid();
        String device_token = FirebaseInstanceId.getInstance().getToken();

        myDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UserID);

        HashMap<String , String> userMap = new HashMap<>();
        userMap.put("name" , displayName);
        userMap.put("status" , "Hi, i'm using MyMooChat !");
        userMap.put("image" , "default");
        userMap.put("online" , "false");
        userMap.put("id",current_user_id);
        userMap.put("thumb_img" , "default");

        myDbRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    myDevTokenRef = FirebaseDatabase.getInstance().getReference().child("Users");

                    myDevTokenRef.child(current_user_id).child("device_token").setValue(device_token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Intent regIntent = new Intent(RegisterActivity.this , SignInActivity.class);
                            regIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(regIntent);
                            pDialog.dismiss();
                            finish();

                        }
                    });
                }
            }
        });
    }



    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You did not Registered YourSelf...", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
