package com.example.moo_chat.moochat;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettingActivity extends AppCompatActivity {

    CircleImageView profileDPImg;
    TextView profileName , profileStatus;
    Button change_dp_btn , change_status_btn , change_name_btn;
    String userName , status , dpImg , thumb_img;

    private DatabaseReference myDbRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        initFields();
    }

    private void initFields() {

        profileDPImg = findViewById(R.id.user_dp);
        profileName = findViewById(R.id.user_setting_name);
        profileStatus = findViewById(R.id.user_setting_status);

        RetriveDbData();

        change_dp_btn = findViewById(R.id.setting_dp_btn);
        change_dp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkCmraPicture();
            }
        });

        change_status_btn = findViewById(R.id.setting_status_btn);
        change_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialogBuilder = new AlertDialog.Builder(UserSettingActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_layout, null);

                final EditText new_status = dialogView.findViewById(R.id.new_status_editText);
                Button status_save_btn = dialogView.findViewById(R.id.new_status_save_btn);
                Button status_cancel_btn = dialogView.findViewById(R.id.new_status_cancel_btn);

                status_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });
                status_save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if((new_status.getText().toString().isEmpty()))
                            Toast.makeText(UserSettingActivity.this, "Type your new Status please! ", Toast.LENGTH_SHORT).show();
                         else {
                             String new_status_confirm = new_status.getText().toString();
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String currentUserID = currentUser.getUid();

                            myDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                            myDbRef.child("status").setValue(new_status_confirm);
                            dialogBuilder.dismiss();

                        }
                    }
                });
                dialogBuilder.setView(dialogView);
                dialogBuilder.show();
            }
        });

        change_name_btn = findViewById(R.id.setting_username_btn);
        change_name_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "Sorry! You are not allowed to Chnage your name..", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void RetriveDbData() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();

        myDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        myDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                dpImg = dataSnapshot.child("image").getValue().toString();
                thumb_img = dataSnapshot.child("thumb_img").getValue().toString();

                profileName.setText(userName);
                profileStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
