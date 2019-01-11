package com.example.moo_chat.moochat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class UserSettingActivity extends AppCompatActivity {

    CircleImageView profileDPImg;
    TextView profileName , profileStatus;
    Button change_dp_btn , change_status_btn , change_name_btn;
    String userName , status , dpImg , thumb_img, currentUserID;

    ProgressDialog pDialog;
    Bitmap myThumbBitmap;

    private static final int  GALLERY_PICK_CODE = 100;

    private DatabaseReference myDbRef;
    private FirebaseUser currentUser;
    private StorageReference myProfileImgStorage;

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

        myProfileImgStorage = FirebaseStorage.getInstance().getReference();

        RetriveDbData();

        change_dp_btn = findViewById(R.id.setting_dp_btn);
        change_dp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                checkCmraPicture();
                pickImageGallery();
            }
        });

        change_status_btn = findViewById(R.id.setting_status_btn);
        change_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
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

    private void changeStatus() {
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
                    currentUserID = currentUser.getUid();

                    myDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
                    myDbRef.child("status").setValue(new_status_confirm);
                    dialogBuilder.dismiss();

                }
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void pickImageGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent , "Select Image ! "), GALLERY_PICK_CODE);

    }

    private void RetriveDbData() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = currentUser.getUid();

        myDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        myDbRef.keepSynced(true);

        myDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("name").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                dpImg = dataSnapshot.child("image").getValue().toString();
                thumb_img = dataSnapshot.child("thumb_img").getValue().toString();

                if (!dpImg.equals("default")) {

//                    Picasso.get().load(dpImg).into(profileDPImg);
                    Picasso.get().load(dpImg).networkPolicy(NetworkPolicy.OFFLINE).into(profileDPImg, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(dpImg).into(profileDPImg);
                        }
                    });
                }

                profileName.setText(userName);
                profileStatus.setText(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK_CODE){

            try {
                Uri image_uri = data.getData();

                CropImage.activity(image_uri)
                        .setAspectRatio(1,1)
                        .start(this);
            } catch(Exception e) {
                Toast.makeText(this, "You did not pick any Picture !", Toast.LENGTH_LONG).show();
            }


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                pDialog = new ProgressDialog(this);
                pDialog.setTitle("Saving Display Pictuer !");
                pDialog.setMessage("Loading...");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();

                Uri resultUri = result.getUri();

                File myThumbFilePath = new File(resultUri.getPath());

                currentUserID = currentUser.getUid();

                try {
                    myThumbBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(myThumbFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                myThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] myThumbByte = baos.toByteArray();


                StorageReference ImgstoragePath = myProfileImgStorage.child("profile_images").child(currentUserID+ ".jpg");
                StorageReference thumb_filePath = myProfileImgStorage.child("profile_images").child("thumbs").child(currentUserID + ".jpg");

                // get uri from gallery and store to firebase DB
                ImgstoragePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            String downloadImgUrl = task.getResult().getDownloadUrl().toString();

                            //download img uri and upload this in thumbs , as a bitmap bytes (compress form)
                            UploadTask uploadTask = thumb_filePath.putBytes(myThumbByte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task_thumb) {
                                    String thumb_downloadUrl = task_thumb.getResult().getDownloadUrl().toString();

                                    if (task_thumb.isSuccessful())
                                    {
                                        Map update_HashMap = new HashMap<>();
                                        update_HashMap.put("image" , downloadImgUrl);
                                        update_HashMap.put("thumb_img" , thumb_downloadUrl);

                                        // get uri from DB and set to our imgView and thumb both.
                                        myDbRef.updateChildren(update_HashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(UserSettingActivity.this, "Display Picture Added Successfully .!", Toast.LENGTH_SHORT).show();
                                                    pDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                            Toast.makeText(UserSettingActivity.this, "WORKING..............", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(UserSettingActivity.this, "There is some error in uploading Picture, please Try Again. ! ", Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(40);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
