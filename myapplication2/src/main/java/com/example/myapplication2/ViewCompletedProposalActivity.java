package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Locale;
import java.util.Objects;

public class ViewCompletedProposalActivity extends AppCompatActivity {

    ImageView profileImageView;
    TextView fullnameTextview;

    TextView budgetTextview;

    TextView titleTextview;

    TextView detailsTextview;

    TextView completedByTextview;

    String ownUid;

    ImageView star1_imageview;

    ImageView star2_imageview;

    ImageView star3_imageview;

    ImageView star4_imageview;

    ImageView star5_imageview;

    EditText review_edittext;

    Button submitRatingButton;

    LinearLayout reviewLayout;

    int rating = 0;

    String proposalId;

    String professionalUid;
    String customerUid;

    TextView startDateTextview;

    CardView youRatedCardview;

    TextView youRatedIntTextview;

    TextView youRatedReviewTextview;

    LinearLayout activityLayout;

    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_completed_proposal);

        profileImageView = findViewById(R.id.profile_imageview_view_completed_proposal_activity);
        fullnameTextview = findViewById(R.id.fullname_textview_view_completed_proposal_activity);
        budgetTextview = findViewById(R.id.budget_textview_view_completed_proposal_activity);
        titleTextview = findViewById(R.id.title_textview_view_completed_proposal_activity);
        detailsTextview = findViewById(R.id.details_textview_view_completed_proposal_activity);
        completedByTextview = findViewById(R.id.completed_by_textview_view_completed_proposal_activity);
        startDateTextview = findViewById(R.id.start_date_textview_view_completed_proposal_activity);


        star1_imageview = findViewById(R.id.rating_star_1_completed_proposal_activity);
        star2_imageview = findViewById(R.id.rating_star_2_completed_proposal_activity);
        star3_imageview = findViewById(R.id.rating_star_3_completed_proposal_activity);
        star4_imageview = findViewById(R.id.rating_star_4_completed_proposal_activity);
        star5_imageview = findViewById(R.id.rating_star_5_completed_proposal_activity);

        reviewLayout = findViewById(R.id.ratings_layout_completed_proposal_activity);

        review_edittext = findViewById(R.id.review_edittext_completed_proposal_activity);
        submitRatingButton = findViewById(R.id.submit_rating_button_completed_proposal_activity);

        ownUid = FirebaseAuth.getInstance().getUid();

        youRatedCardview = findViewById(R.id.you_rated_cardview_view_completed_proposal_activity);
        youRatedIntTextview = findViewById(R.id.you_rated_int_textview_view_completed_proposal_activity);
        youRatedReviewTextview = findViewById(R.id.you_rated_review_textview_view_completed_proposal_activity);

        activityLayout = findViewById(R.id.activity_layout_view_completed_proposal_activity);
        progressBar = findViewById(R.id.progressbar_view_completed_proposal_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_view_completed_proposal_activity);
        toolbar.setTitle("Completed proposal");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();

        if (i != null) {
            String completedProposalId = i.getStringExtra("completedProposalId");

            proposalId = completedProposalId;

            fetchProposal(completedProposalId);
        }

        //ratings images
        setRatingStars();

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating == 0) {
                    Toast.makeText(ViewCompletedProposalActivity.this, "rating cannot be zero", Toast.LENGTH_SHORT).show();
                } else {
                    submitRatingButton.setEnabled(false);
                    String review = review_edittext.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
                    uploadReviewAndRating(rating, review, proposalId, professionalUid, customerUid);
                }

            }
        });
    }

    private void fetchProposal(String completedProposalId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("completed_Proposal");
        databaseReference.child(ownUid).child(completedProposalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AcceptProposalObject proposal = snapshot.getValue(AcceptProposalObject.class);
                    if (proposal != null){
                        String title = proposal.getTitle();
                        String budget = proposal.getBudget();
                        String details = proposal.getDetails();
                        String timestamp = proposal.getProfessionalMarkedCompleteTimestamp();
                        String professionalId = proposal.getProfessionalId();
                        String customerId = proposal.getCustomerId();
                        String date = proposal.getStartDate();

                        if (ownUid.equals(professionalId)) {
                            fetchOppositeProfile(customerId, title, budget, details, timestamp, professionalId,date);
                        } else {
                            fetchOppositeProfile(professionalId, title, budget, details, timestamp, professionalId,date);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewCompletedProposalActivity", "error fetching completed proposal" + error);
                Toast.makeText(ViewCompletedProposalActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchOppositeProfile(String oppositeUid, String title, String budget, String details, String timestamp, String professionalUid,String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");
        databaseReference.child(oppositeUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String fullName = uploadPersonforSeachIndex.getFullname();
                        String profileUrl = uploadPersonforSeachIndex.getProfileUrl();

                        fullnameTextview.setText(fullName);
                        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageView);

                        titleTextview.setText(title);
                        budgetTextview.setText(budget);
                        detailsTextview.setText(details);
                        startDateTextview.setText(date);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        String time = dateFormat.format(new Date(Long.parseLong(timestamp)));

                        if (ownUid.equals(professionalUid)) {
                            String youCompletedText = "You completed this on " + time;
                            completedByTextview.setText(youCompletedText);
                        } else {
                            String fullNameYouCompletedText = fullName + " completed this on " + time;
                            completedByTextview.setText(fullNameYouCompletedText);
                        }

                        checkRatings();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewCompletedProposalActivity", "error in finding fullName and profile url. error is : " + error);
            }
        });
    }

    private void checkRatings() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        databaseReference.child("rating_Manager").child(ownUid).child(proposalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RatingManagerObject ratings = snapshot.getValue(RatingManagerObject.class);
                    if (ratings != null){
                        professionalUid = ratings.getProfessionalUid();
                        customerUid = ratings.getCustomerUid();
                        String professionalRatedCustomer = ratings.getProfessionalHasRatedCustomer();
                        String customerRatedProfessional = ratings.getCustomerHasRatedProfessional();

                        setupRatingsViews(professionalUid, customerUid, proposalId, professionalRatedCustomer, customerRatedProfessional);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewCompletedProposalActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("ViewCompletedProposalActivity", "error fetching ratings. error: " + error);
            }
        });
    }

    private void setupRatingsViews(String professionalUid, String customerUid, String proposalId, String professionalRatedCustomer, String customerRatedProfessional) {
        if (ownUid.equals(customerUid)) {
            if (!customerRatedProfessional.equals("Yes")) {
                reviewLayout.setVisibility(View.VISIBLE);

                // we have loaded all the view now make activity visible
                activityLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                reviewLayout.setVisibility(View.GONE);
                getRatings(proposalId,professionalUid,customerUid);
            }
        } else {
            if (!professionalRatedCustomer.equals("Yes")) {
                reviewLayout.setVisibility(View.VISIBLE);
                // we have loaded all the view now make activity visible
                activityLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                reviewLayout.setVisibility(View.GONE);
                getRatings(proposalId,professionalUid,customerUid);
            }
        }
    }

    private void uploadReviewAndRating(int rating, String review, String proposalId, String professionalUid, String customerUid) {

        String oppositeUid = null;

        if (ownUid.equals(customerUid)) {
            oppositeUid = professionalUid;
        } else {
            oppositeUid = customerUid;
        }

        if (oppositeUid != null) {

            // we find name of the person who is rating...
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");
            String finalOppositeUid = oppositeUid;
            databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null){
                            String fullname = uploadPersonforSeachIndex.getFullname();
                            requestToUploadRating(rating, review, proposalId, professionalUid, customerUid, ownUid,fullname, finalOppositeUid);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewCompletedProposalActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d("ViewCompletedProposalActivity","unable to find self fullname. error is : "+ error);
                }
            });

        } else {
            Toast.makeText(this, "unable to rate", Toast.LENGTH_SHORT).show();
            Log.d("ViewCompletedProposalActivity", "oppositeUid is null");
        }

    }

    private void requestToUploadRating(int rating, String review, String proposalId, String professionalUid,String customerUid,String ratedByUid,String ratedByName, String oppositeUid){
        UploadUserRating.uploadRatingAndReviewForUser(rating, review, proposalId, professionalUid, customerUid, ratedByUid,ratedByName, oppositeUid, new UploadUserRating.CompletionCallback() {
            @Override
            public void onCompletion() {
                Toast.makeText(ViewCompletedProposalActivity.this, "user rated successfully", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ViewCompletedProposalActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void setRatingStars(){
        star1_imageview.setOnClickListener(v -> {
            rating = 1;
            star1_imageview.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star2_imageview.setImageResource(R.drawable.ic_star_outline);
            star3_imageview.setImageResource(R.drawable.ic_star_outline);
            star4_imageview.setImageResource(R.drawable.ic_star_outline);
            star5_imageview.setImageResource(R.drawable.ic_star_outline);
        });

        star2_imageview.setOnClickListener(v -> {
            rating = 2;
            star1_imageview.setImageResource(R.drawable.ic_star_filled);
            star2_imageview.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star3_imageview.setImageResource(R.drawable.ic_star_outline);
            star4_imageview.setImageResource(R.drawable.ic_star_outline);
            star5_imageview.setImageResource(R.drawable.ic_star_outline);
        });

        star3_imageview.setOnClickListener(v -> {
            rating = 3;
            star1_imageview.setImageResource(R.drawable.ic_star_filled);
            star2_imageview.setImageResource(R.drawable.ic_star_filled);
            star3_imageview.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star4_imageview.setImageResource(R.drawable.ic_star_outline);
            star5_imageview.setImageResource(R.drawable.ic_star_outline);
        });

        star4_imageview.setOnClickListener(v -> {
            rating = 4;
            star1_imageview.setImageResource(R.drawable.ic_star_filled);
            star2_imageview.setImageResource(R.drawable.ic_star_filled);
            star3_imageview.setImageResource(R.drawable.ic_star_filled);
            star4_imageview.setImageResource(R.drawable.ic_star_filled);

            //make other star outlined
            star5_imageview.setImageResource(R.drawable.ic_star_outline);
        });

        star5_imageview.setOnClickListener(v -> {
            rating = 5;
            star1_imageview.setImageResource(R.drawable.ic_star_filled);
            star2_imageview.setImageResource(R.drawable.ic_star_filled);
            star3_imageview.setImageResource(R.drawable.ic_star_filled);
            star4_imageview.setImageResource(R.drawable.ic_star_filled);
            star5_imageview.setImageResource(R.drawable.ic_star_filled);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void getRatings(String proposalId,String professionalUid,String customerUid){

        if (ownUid.equals(customerUid)){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("ratingAndReview");
            databaseReference.child(professionalUid).child(proposalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        RatingAndReviewObject ratingAndReviewObject = snapshot.getValue(RatingAndReviewObject.class);
                        if (ratingAndReviewObject != null){
                            String ratedBy = ratingAndReviewObject.getRatedByUid();

                            if (ratedBy.equals(ownUid)){
                                String ratingNumber = String.valueOf(ratingAndReviewObject.getRating());
                                String review = ratingAndReviewObject.getReview();
                                youRatedCardview.setVisibility(View.VISIBLE);
                                youRatedIntTextview.setText(ratingNumber);
                                youRatedReviewTextview.setText(review);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if (ownUid.equals(professionalUid)){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("ratingAndReview");
            databaseReference.child(customerUid).child(proposalId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        RatingAndReviewObject ratingAndReviewObject = snapshot.getValue(RatingAndReviewObject.class);
                        if (ratingAndReviewObject != null){
                            String ratedBy = ratingAndReviewObject.getRatedByUid();

                            if (ratedBy.equals(ownUid)){
                                String ratingNumber = String.valueOf(ratingAndReviewObject.getRating());
                                String review = ratingAndReviewObject.getReview();
                                youRatedCardview.setVisibility(View.VISIBLE);
                                youRatedIntTextview.setText(ratingNumber);
                                youRatedReviewTextview.setText(review);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        // we have loaded all the view now make activity visible
        activityLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isTaskRoot()){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }else {
            finish();
        }
    }

}