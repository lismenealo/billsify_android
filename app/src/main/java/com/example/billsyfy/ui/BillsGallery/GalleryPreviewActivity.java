package com.example.billsyfy.ui.BillsGallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.billsyfy.R;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

public class GalleryPreviewActivity extends AppCompatActivity {

    ImageView GalleryPreviewImg;
    String path;
    String description;
    String category;
    String amount;
    String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_gallery_preview);
        Intent intent = getIntent();

        //Retrieve Information stored on intent
        path = intent.getStringExtra("path");
        description = intent.getStringExtra("description");
        category = intent.getStringExtra("category");
        amount = intent.getStringExtra("amount");
        timestamp = intent.getStringExtra("timestamp");

        //Set textView description
        TextView textView = findViewById(R.id.bill_details);
        textView.setText(
                "Category: " + category + "\n" +
                        "Amount: " + amount + "\n" +
                        "Description: " + description + "\n" +
                        "Timestamp: " + timestamp);

        //Load bill image zoomed
        GalleryPreviewImg = (ImageView) findViewById(R.id.GalleryPreviewImg);
        Glide.with(GalleryPreviewActivity.this)
                .load(new File(path)) // Uri of the picture
                .into(GalleryPreviewImg);
    }
}
