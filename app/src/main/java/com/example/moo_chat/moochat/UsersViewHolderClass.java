package com.example.moo_chat.moochat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class UsersViewHolderClass extends RecyclerView.ViewHolder {

    private static View myView;

    public UsersViewHolderClass(@NonNull View itemView) {
        super(itemView);

        myView = itemView;

    }

    public static void setValues(String name, String status){
        TextView singleViewName;
        TextView singleViewStatus;

        singleViewName = myView.findViewById(R.id.alluser_name);
        singleViewName.setText(name);

        singleViewStatus = myView.findViewById(R.id.alluser_status);
        singleViewStatus.setText(status);

    }
}
