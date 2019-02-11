package com.example.moo_chat.moochat;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class FullImageActivity extends AppCompatActivity {

    String url = "";
    ImageView fullImage;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = findViewById(R.id.full_image_view);
        textView2 = findViewById(R.id.textView2);

        url = getIntent().getStringExtra("image_url");

        Uri myUri = Uri.parse(url);

        fullImage.setImageURI(myUri);
        textView2.setText(url);

    }
}
