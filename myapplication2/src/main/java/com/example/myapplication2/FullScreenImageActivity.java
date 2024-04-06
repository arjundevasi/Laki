package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FullScreenImageActivity extends AppCompatActivity {

    SubsamplingScaleImageView photoview;

    ImageButton cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        photoview = findViewById(R.id.photo_view_full_screen_image_activity);
        cancelButton = findViewById(R.id.cancel_button_full_screen_image_activity);

        Intent i = getIntent();

        if (i != null){
            String imageUrl = i.getStringExtra("imageUrl");
            Picasso.get().load(imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    photoview.setImage(ImageSource.bitmap(bitmap));
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        cancelButton.setOnClickListener(v -> {
            finish();
        });
        
    }
}