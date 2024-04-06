package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ReviewAndRatingActivity extends AppCompatActivity {

    int rating = 0;

    String proposalId = null;
    String professionalUid = null;
    String customerUid = null;

    String oppositeFullName = null;

    String oppositeProfileUrl = null;

    String oppositeUid = null;

    ImageView profileImageview;
    TextView fullNameTextView;
    String ownUid = null;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_and_rating);


        ownUid = FirebaseAuth.getInstance().getUid();

        Intent i = getIntent();
        if (i != null) {
            proposalId = i.getStringExtra("proposalId");
            professionalUid = i.getStringExtra("professionalUid");
            customerUid = i.getStringExtra("customerUid");
            oppositeUid = i.getStringExtra("oppositeUid");
            oppositeFullName = i.getStringExtra("fullname");
            oppositeProfileUrl = i.getStringExtra("profileUrl");

        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.d("ReviewAndRatingActivity", "error in getting intent strings.");
            return;
        }

        ImageView star1 = findViewById(R.id.rating_star_1);
        ImageView star2 = findViewById(R.id.rating_star_2);
        ImageView star3 = findViewById(R.id.rating_star_3);
        ImageView star4 = findViewById(R.id.rating_star_4);
        ImageView star5 = findViewById(R.id.rating_star_5);

        profileImageview = findViewById(R.id.profile_imageview_review_and_rating_activity);
        fullNameTextView = findViewById(R.id.fullname_textview_review_and_rating_activity);

        EditText reviewEditText = findViewById(R.id.review_edittext_review_and_rating_activity);
        Button submitButton = findViewById(R.id.submit_button_review_and_rating_activity);

        progressBar = findViewById(R.id.progressbar_review_and_rating_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_review_and_rating_activity_activity);
        toolbar.setTitle("Rate User");
        setSupportActionBar(toolbar);

        star1.setOnClickListener(v -> {
            rating = 1;
            star1.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star2.setImageResource(R.drawable.ic_star_outline);
            star3.setImageResource(R.drawable.ic_star_outline);
            star4.setImageResource(R.drawable.ic_star_outline);
            star5.setImageResource(R.drawable.ic_star_outline);
        });

        star2.setOnClickListener(v -> {
            rating = 2;
            star1.setImageResource(R.drawable.ic_star_filled);
            star2.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star3.setImageResource(R.drawable.ic_star_outline);
            star4.setImageResource(R.drawable.ic_star_outline);
            star5.setImageResource(R.drawable.ic_star_outline);
        });

        star3.setOnClickListener(v -> {
            rating = 3;
            star1.setImageResource(R.drawable.ic_star_filled);
            star2.setImageResource(R.drawable.ic_star_filled);
            star3.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star4.setImageResource(R.drawable.ic_star_outline);
            star5.setImageResource(R.drawable.ic_star_outline);
        });

        star4.setOnClickListener(v -> {
            rating = 4;
            star1.setImageResource(R.drawable.ic_star_filled);
            star2.setImageResource(R.drawable.ic_star_filled);
            star3.setImageResource(R.drawable.ic_star_filled);
            star4.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star5.setImageResource(R.drawable.ic_star_outline);
        });

        star5.setOnClickListener(v -> {
            rating = 5;
            star1.setImageResource(R.drawable.ic_star_filled);
            star2.setImageResource(R.drawable.ic_star_filled);
            star3.setImageResource(R.drawable.ic_star_filled);
            star4.setImageResource(R.drawable.ic_star_filled);
            star5.setImageResource(R.drawable.ic_star_filled);
        });


        submitButton.setOnClickListener(v -> {
            if (rating == 0){
                Toast.makeText(ReviewAndRatingActivity.this, "rating cannot be zero", Toast.LENGTH_SHORT).show();
            }else {
                submitButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String review = reviewEditText.getText().toString().trim();
                uploadReviewAndRating(rating, review, proposalId, professionalUid, customerUid);
            }

        });

        loadOppositeProfile();
    }

    private void uploadReviewAndRating(int rating, String review, String proposalId, String professionalUid, String customerUid) {

        String localOppositeUid;
        if (ownUid.equals(professionalUid)){
            localOppositeUid = customerUid;
        }else {
            localOppositeUid = professionalUid;
        }

        if (localOppositeUid != null){

            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");

            databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null){
                            String fullName = uploadPersonforSeachIndex.getFullname();

                            UploadUserRating.uploadRatingAndReviewForUser(rating, review, proposalId, professionalUid, customerUid, ownUid,fullName, localOppositeUid, () -> {
                                Toast.makeText(ReviewAndRatingActivity.this, "user rated successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ReviewAndRatingActivity.this,MainActivity.class);
                                startActivity(i);
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ReviewAndRatingActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d("ViewCompletedProposalActivity","unable to find self fullName. error is : "+ error);
                }
            });
        }else {
            Toast.makeText(this, "unable to rate user", Toast.LENGTH_SHORT).show();
            Log.d("ReviewAndRatingActivity","oppositeUid is null");
        }

    }

    private void loadOppositeProfile() {
        Picasso.get().load(oppositeProfileUrl).transform(new RoundImageTransformation()).into(profileImageview);
        fullNameTextView.setText(oppositeFullName);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }
}