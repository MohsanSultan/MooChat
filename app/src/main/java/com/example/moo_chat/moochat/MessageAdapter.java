package com.example.moo_chat.moochat;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;


    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(view);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText , fromMessageText;
        public CircleImageView profileImage , fromProfileImage;
        public ImageView messageImage , fromMessageImage;

        public MessageViewHolder(View view) {
            super(view);

            // ------------------- User Layout -----------------------------------
            messageText = view.findViewById(R.id.message_text_layout);
            messageText.setTextIsSelectable(true);

            profileImage = view.findViewById(R.id.message_profile_layout);
            messageImage = view.findViewById(R.id.message_image_layout);

            // ------------------- Other User Layout -----------------------------
            fromMessageText = view.findViewById(R.id.message_text_user_layout);
            fromMessageText.setTextIsSelectable(true);

            fromProfileImage = view.findViewById(R.id.message_profile_user_layout);
            fromMessageImage = view.findViewById(R.id.message_image_user_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

//        Context myContext = viewHolder.profileImage.getContext();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();
        String mCurrentUserId;

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();

        if (mCurrentUserId.equals(from_user)){
            viewHolder.messageText.setVisibility(View.VISIBLE);
            viewHolder.messageText.setBackgroundResource(R.drawable.custom_chat_view_from);
            viewHolder.profileImage.setVisibility(View.VISIBLE);

            viewHolder.fromMessageText.setVisibility(View.GONE);
            viewHolder.fromProfileImage.setVisibility(View.GONE);

        }else {
            viewHolder.fromMessageText.setVisibility(View.VISIBLE);
            viewHolder.fromMessageText.setBackgroundResource(R.drawable.custom_chat_view_to);
            viewHolder.fromProfileImage.setVisibility(View.VISIBLE);

            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.profileImage.setVisibility(View.GONE);
        }


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("thumb_img").getValue().toString();

                Picasso.get().load(image)
                        .placeholder(R.drawable.user_avatar).into(viewHolder.profileImage);
                Picasso.get().load(image)
                        .placeholder(R.drawable.user_avatar).into(viewHolder.fromProfileImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(message_type.equals("text")) {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.fromMessageText.setText(c.getMessage());

            viewHolder.messageImage.setVisibility(View.GONE);
            viewHolder.fromMessageImage.setVisibility(View.GONE);

        } else {

            viewHolder.messageText.setVisibility(View.GONE);
            viewHolder.fromMessageText.setVisibility(View.GONE);

            viewHolder.messageImage.setVisibility(View.VISIBLE);
            viewHolder.fromMessageImage.setVisibility(View.VISIBLE);

            Picasso.get().load(c.getMessage())
                    .placeholder(R.drawable.user_avatar).into(viewHolder.messageImage);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
}
