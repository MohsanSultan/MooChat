package com.example.moo_chat.moochat;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersViewHolderClass extends RecyclerView.ViewHolder {

    public static View myView;

    public UsersViewHolderClass(@NonNull View itemView) {
        super(itemView);

        myView = itemView;

    }

    public static void setValues(String name, String status , String thumb_img ){
        TextView singleViewName;
        TextView singleViewStatus;
        CircleImageView singleUserImg;
        CircleImageView singleUserThumbImg;


        singleViewName = myView.findViewById(R.id.alluser_name);
        singleViewName.setText(name);

        singleViewStatus = myView.findViewById(R.id.alluser_status);
        singleViewStatus.setText(status);

        singleUserThumbImg = myView.findViewById(R.id.alluser_img_view);
        Picasso.get().load(thumb_img).placeholder(R.drawable.user_avatar).into(singleUserThumbImg);



    }
}
