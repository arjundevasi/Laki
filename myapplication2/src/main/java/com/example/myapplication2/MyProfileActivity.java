package com.example.myapplication2;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;

import android.os.Bundle;


import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MyProfileActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    ProgressBar progressBar;

    FirebaseUser mUser;

    TextView fullnameTextview;
    TextView categoryTextview;
    TextView locationTextview;
    TextView servicesTextview;
    TextView yearTextView;
    TextView ratingTextview;
    TextView contractTextview;

    ImageView profileImageview;

    String oppositeUid = null;

    String fullnameExport = null;

    String profileUrlExport = null;

    Button editProfileButton;

    LinearLayout proposalMessageLayout;

    String profileImageUri;

    Toolbar toolbar;

    Button hireButton;
    Button messageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        linearLayout = findViewById(R.id.myprofile_linear_layout);
        progressBar = findViewById(R.id.myprofile_progressbar);

        editProfileButton = findViewById(R.id.edit_profile_button_my_profile_activity);
        proposalMessageLayout =findViewById(R.id.proposal_message_button_layout);

        fullnameTextview = findViewById(R.id.myprofile_fullname_textview);
        categoryTextview = findViewById(R.id.myprofile_category_textview);
        locationTextview = findViewById(R.id.myprofile_location_textview);
        servicesTextview = findViewById(R.id.myprofile_services_textview);
        yearTextView = findViewById(R.id.myprofile_year_textview);
        ratingTextview = findViewById(R.id.myprofile_rating_textview);
        contractTextview = findViewById(R.id.myprofile_contract_textview);
        profileImageview = findViewById(R.id.myprofile_activity_profile_imageview);

        hireButton = findViewById(R.id.hire_button_my_profile_activty);
        messageButton = findViewById(R.id.message_button_my_profile_activity);

        //we make message layout disappear and then check whether to make it visible or not.
        messageButton.setVisibility(View.GONE);

        toolbar = findViewById(R.id.myprofile_activity_toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null){
            toolbar.setTitle("Laki");
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        if (i != null) {

            String uidFromIntent = i.getStringExtra("uid");

            if (uidFromIntent != null) {

                if (uidFromIntent.equals(mUser.getUid())){
                    editProfileButton.setVisibility(View.VISIBLE);
                    proposalMessageLayout.setVisibility(View.GONE);
                }else {
                    proposalMessageLayout.setVisibility(View.VISIBLE);
                    editProfileButton.setVisibility(View.GONE);
                }

                oppositeUid = uidFromIntent;

                loadProfile(uidFromIntent);

                CheckMessageButtonVisibilityStatus();
            } else {
                loadProfile(mUser.getUid());
                editProfileButton.setVisibility(View.VISIBLE);
                proposalMessageLayout.setVisibility(View.GONE);
            }

        }


        hireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, ProposalActivity.class);
                i.putExtra("fullName", fullnameExport);
                i.putExtra("profileUrl", profileUrlExport);
                i.putExtra("uid", oppositeUid);
                i.putExtra("isEditing", "No");
                startActivity(i);
            }
        });


        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyProfileActivity.this, MessageActivity.class);
                i.putExtra("uid", oppositeUid);
                startActivity(i);
            }
        });


        editProfileButton.setOnClickListener(v -> {
            Intent j = new Intent(MyProfileActivity.this, RegisterUsersActivity.class);
            j.putExtra("isEditing","Yes");
            startActivity(j);
        });

        profileImageview.setOnClickListener(v -> {
            if (profileImageUri != null){
                Intent k = new Intent(MyProfileActivity.this,FullScreenImageActivity.class);
                k.putExtra("imageUrl",profileImageUri);
                startActivity(k);
            }

        });
    }

    private void loadProfile(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){

                        String fullname = uploadPersonforSeachIndex.getFullname();
                        String location = uploadPersonforSeachIndex.getLocation();
                        String category = uploadPersonforSeachIndex.getCategory();
                        String services = uploadPersonforSeachIndex.getServices();
                        String year = uploadPersonforSeachIndex.getYear();
                        String profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        String username = uploadPersonforSeachIndex.getUsername();

                        profileImageUri = profileUrl;

                        double rating = uploadPersonforSeachIndex.getAverageRating();
                        long contract = uploadPersonforSeachIndex.getTotalContracts();
                        long ratingCount = uploadPersonforSeachIndex.getTotalRatingCount();

                        fullnameExport = fullname;
                        profileUrlExport = profileUrl;


                        setProfileDetails(fullname, location, category, services, year, rating, contract, profileUrl,ratingCount,username);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No User Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void setProfileDetails(String fullname, String location, String category, String services, String year, double rating, long contract, String profileUrl,long ratingCount,String username) {
        String capitalName = UtilityMethods.capitalizeFullName(fullname);
        fullnameTextview.setText(capitalName);
        locationTextview.setText(location);
        categoryTextview.setText(category);
        servicesTextview.setText(services);

        String formattedYear = "Joined " + year;
        yearTextView.setText(formattedYear);

        String toolbarUsername = "@" + username;

        if (toolbar != null){
            toolbar.setTitle(toolbarUsername);
        }

        if (ratingCount < 1){
            ratingTextview.setText(String.valueOf(rating));
        }else{
            String text = rating + " (" + ratingCount + ")";
            ratingTextview.setText(text);
        }
        contractTextview.setText(String.valueOf(contract));
        Picasso.get().load(profileUrl).resize(600, 600).centerCrop().into(profileImageview);
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void CheckMessageButtonVisibilityStatus(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("ongoing_Proposal").child(mUser.getUid());
        reference.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        AcceptProposalObject acceptProposalObject = dataSnapshot.getValue(AcceptProposalObject.class);
                        if (acceptProposalObject != null){
                            String professionalUid = acceptProposalObject.getProfessionalId();
                            String customerUid = acceptProposalObject.getCustomerId();
                            String hasCompleted = acceptProposalObject.getProfessionalMarkedAsCompleted();
                            if (!hasCompleted.equals("Yes")){
                                if (oppositeUid.equals(professionalUid) || oppositeUid.equals(customerUid)){
                                    messageButton.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}