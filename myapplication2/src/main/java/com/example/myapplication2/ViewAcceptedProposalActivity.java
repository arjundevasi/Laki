package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ViewAcceptedProposalActivity extends AppCompatActivity {

    TextView titleTextview;
    TextView detailsTextview;
    TextView budgetTextview;
    ImageView profileImageView;
    TextView fullNameTextView;

    Button markAsDoneButton;

    String ownUid;

    String senderUid;

    String proposalId;

    String receiverUid;

    String title;

    String oppositeUid;

    TextView markedAsCompletedTextview;


    String details;
    String budget;
    String acceptedBy;

    String professionalId;

    String fullName = "";
    String profileUrl = "";

    String ownFullname;

    String fcmToken;

    TextView startDateTextview;

    ProgressBar progressBar;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accepted_proposal);

        ownUid = FirebaseAuth.getInstance().getUid();

        titleTextview = findViewById(R.id.title_textview_view_accepted_proposal_activity);
        detailsTextview = findViewById(R.id.details_textview_view_accepted_proposal_activity);
        budgetTextview = findViewById(R.id.budget_textview_view_accepted_proposal_activity);
        startDateTextview = findViewById(R.id.start_date_textview_view_accepted_proposal_activity);

        fullNameTextView = findViewById(R.id.fullname_textview_view_accepted_proposal_activity);
        profileImageView = findViewById(R.id.profile_imageview_view_accepted_proposal_activity);

        markedAsCompletedTextview = findViewById(R.id.you_marked_as_completed_textview_view_accepted_proposal_activity);

        markAsDoneButton = findViewById(R.id.mark_as_done_button_view_accepted_proposal_activity);

        progressBar = findViewById(R.id.progressbar_view_accepted_proposal_activity);

        layout = findViewById(R.id.linear_layout_view_accepted_proposal_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_accepted_proposal_activity);
        toolbar.setTitle("Proposal Accepted");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i != null) {
            String ongoingProposalId = i.getStringExtra("ongoing_proposal_id");
            fetchOngoingProposal(ongoingProposalId);
        }

        markAsDoneButton.setOnClickListener(v -> {
            showConfirmationDialog();
        });
    }

    private void fetchOngoingProposal(String ongoingProposalId) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("ongoing_Proposal");
        databaseReference.child(ownUid).child(ongoingProposalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AcceptProposalObject acceptProposalObject = snapshot.getValue(AcceptProposalObject.class);

                    if (acceptProposalObject != null){
                        title = acceptProposalObject.getTitle();
                        details = acceptProposalObject.getDetails();
                        budget = acceptProposalObject.getBudget();
                        acceptedBy = acceptProposalObject.getAcceptedBy();

                        proposalId = acceptProposalObject.getProposalId();

                        senderUid = acceptProposalObject.getSenderUid();
                        receiverUid = acceptProposalObject.getReceiverUid();


                        professionalId = acceptProposalObject.getProfessionalId();

                        String date = acceptProposalObject.getStartDate();
                        String professionalMarkedAsCompleted = acceptProposalObject.getProfessionalMarkedAsCompleted();


                        if (ownUid.equals(senderUid)) {
                            oppositeUid = receiverUid;
                        } else {
                            oppositeUid = senderUid;
                        }

                        checkFcmTokenIsGenerated(oppositeUid);

                        setupViews(title, details, budget, professionalMarkedAsCompleted,date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewAcceptedProposalActivity", "error fetching ongoing proposal. error is: " + error);
            }
        });
    }

    private void setupViews(String title, String details, String budget, String professionalMarkedAsCompleted,String date) {

        progressBar.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");
        databaseReference.child(oppositeUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        fullName = uploadPersonforSeachIndex.getFullname();
                        profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                    }

                }
                String finalFullName = fullName;
                String finalProfileUrl = profileUrl;

                fullNameTextView.setText(finalFullName);
                Picasso.get().load(finalProfileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageView);


                titleTextview.setText(title);
                detailsTextview.setText(details);
                String budgetText = "₹ " + budget;
                budgetTextview.setText(budgetText);
                startDateTextview.setText(date);
                checkAcceptStatus(professionalMarkedAsCompleted);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewAcceptedProposalActivity", "error in finding fullName and profile url. error is : " + error);
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        builder.setView(view);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                markAsDoneButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                markProposalAsCompleted();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private void checkAcceptStatus(String professionalMarkedAsCompleted) {

        if (ownUid.equals(professionalId) && !professionalMarkedAsCompleted.equals("Yes")) {
            markAsDoneButton.setVisibility(View.VISIBLE);
        } else {
            markAsDoneButton.setVisibility(View.GONE);
        }

        if (professionalMarkedAsCompleted.equals("Yes")){
            markedAsCompletedTextview.setVisibility(View.VISIBLE);
        }
    }

    private void markProposalAsCompleted() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String notificationId = "completed_" + proposalId + timestamp;
        NotificationsObject notificationsObject = new NotificationsObject(ownUid, receiverUid, proposalId, title, timestamp, "No", notificationId, null, null, null, proposalId,null,null);

        Map<String, Object> updates = new HashMap<>();

        updates.put("professionalMarkedAsCompleted", "Yes");
        updates.put("professionalMarkedCompleteTimestamp", timestamp);

        databaseReference.child("ongoing_Proposal").child(senderUid).child(proposalId).updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                databaseReference.child("ongoing_Proposal").child(receiverUid).child(proposalId).updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                        if (ownUid.equals(senderUid)) {
                            databaseReference.child("notifications").child(receiverUid).child(notificationId).setValue(notificationsObject);
                        } else {
                            databaseReference.child("notifications").child(senderUid).child(notificationId).setValue(notificationsObject);
                        }

                        // move to completed proposal collection
                        requestAdminToCalculatePayments(proposalId);

                    }
                });
            }
        });

    }

    private void moveToCompletedProposal() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        // we get the latest data from ongoing collection and put that by updating some values like professional marked competed timestamp.
        // and we put that info into completed collection.

        databaseReference.child("ongoing_Proposal").child(senderUid).child(proposalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AcceptProposalObject acceptProposalObject = snapshot.getValue(AcceptProposalObject.class);
                    if (acceptProposalObject != null){
                        // String senderUid, String receiverUid, String proposalId, String title, String details, String budget, String ongoingTimestamp, String hasAccepted,
                        // String acceptedBy, String professionalId, String customerId, String professionalMarkedAsCompleted, String professionalMarkedCompleteTimestamp
                        String completedSenderUid = acceptProposalObject.getSenderUid();
                        String completedReceiverUid = acceptProposalObject.getReceiverUid();
                        String completedProposalId = acceptProposalObject.getProposalId();
                        String completedTitle = acceptProposalObject.getTitle();
                        String completedDetails = acceptProposalObject.getDetails();
                        String completedBudget = acceptProposalObject.getBudget();
                        String completedOngoingTimestamp = acceptProposalObject.getOngoingTimestamp();
                        String completedHasAccepted = acceptProposalObject.getHasAccepted();
                        String completedAcceptedBy = acceptProposalObject.getAcceptedBy();
                        String completedProfessionalId = acceptProposalObject.getProfessionalId();
                        String completedCustomerId = acceptProposalObject.getCustomerId();
                        String professionalMarkedAsCompleted = acceptProposalObject.getProfessionalMarkedAsCompleted();
                        String timestamp = acceptProposalObject.getProfessionalMarkedCompleteTimestamp();
                        String startDate = acceptProposalObject.getStartDate();

                        AcceptProposalObject proposalObject = new AcceptProposalObject(completedSenderUid, completedReceiverUid, completedProposalId, completedTitle, completedDetails, completedBudget, completedOngoingTimestamp, completedHasAccepted, completedAcceptedBy, completedProfessionalId, completedCustomerId, professionalMarkedAsCompleted, timestamp,startDate);

                        databaseReference.child("completed_Proposal").child(senderUid).child(proposalId).setValue(proposalObject);
                        databaseReference.child("completed_Proposal").child(receiverUid).child(proposalId).setValue(proposalObject);

                        // since we have moved the data to completed proposal collection we are updating the data from ongoing collection.
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("professionalMarkedAsCompleted", "Yes");

                        databaseReference.child("ongoing_Proposal").child(receiverUid).child(proposalId).updateChildren(updates);
                        databaseReference.child("ongoing_Proposal").child(senderUid).child(proposalId).updateChildren(updates);

                        // send notification to user
                        findOwnFullnameAndSendNotification(proposalId,completedTitle);

                        // add ratings and contract data
                        setRatingsAndContract(completedProfessionalId, completedCustomerId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewAcceptedProposalActivity", "error fetching ongoing proposal. error is " + error);
            }
        });
    }

    private void setRatingsAndContract(String professionalUid, String customerUid) {
        // in this method we will create contract and ratings for new user.  if there is existing user than we would update the ratings and contracts.

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        ContractObject contractObject = new ContractObject(proposalId, professionalUid, customerUid);
        // adding data to contract collection
        databaseReference.child("contracts").child(professionalUid).child(proposalId).setValue(contractObject, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                // adding contract data for another user
                databaseReference.child("contracts").child(customerUid).child(proposalId).setValue(contractObject, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        // now adding data for ratings

                        RatingManagerObject ratingManagerObject = new RatingManagerObject(proposalId, professionalUid, customerUid, "No", "No");

                        databaseReference.child("rating_Manager").child(professionalUid).child(proposalId).setValue(ratingManagerObject, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                // adding ratings for another user
                                databaseReference.child("rating_Manager").child(customerUid).child(proposalId).setValue(ratingManagerObject, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        // now we will direct user to rating activity which will prompt user to give ratings.
                                        Intent i = new Intent(ViewAcceptedProposalActivity.this, ReviewAndRatingActivity.class);
                                        i.putExtra("proposalId", proposalId);
                                        i.putExtra("professionalUid", professionalUid);
                                        i.putExtra("customerUid", customerUid);
                                        i.putExtra("oppositeUid", oppositeUid);
                                        i.putExtra("fullname", fullName);
                                        i.putExtra("profileUrl", profileUrl);
                                        startActivity(i);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    private void findOwnFullnameAndSendNotification(String notificationProposalId, String notificationTitle) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String sharedFullname = sharedPreferences.getString("fullname", null);



        if (sharedFullname == null) {
            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(ownUid);

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            ownFullname = uploadPersonforSeachIndex.getFullname();
                            if (fcmToken != null){
                                try {
                                    SendPushNotification.sendCompletedProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ViewAcceptedProposalActivity.this);
                                } catch (JSONException e) {
                                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                                }
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("CreatePostActivity", "unable to find username and photo");
                }
            });
        } else {
            // we have full name in shared preferences.
            ownFullname = sharedFullname;
            if (fcmToken != null){
                try {
                    SendPushNotification.sendCompletedProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ViewAcceptedProposalActivity.this);
                } catch (JSONException e) {
                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                }
            }
        }
    }

    private void checkFcmTokenIsGenerated(String receiverUid){
        fcmToken = null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        databaseReference.child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        fcmToken = uploadPersonforSeachIndex.getFcmToken();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void requestAdminToCalculatePayments(String proposalId){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        long timestamp = System.currentTimeMillis();
        RequestAdminCalculatePaymentsObject requestAdminCalculatePaymentsObject = new RequestAdminCalculatePaymentsObject(proposalId,ownUid,timestamp);
        databaseReference.child("userRequestToCalculatePayments").child(proposalId).setValue(requestAdminCalculatePaymentsObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                moveToCompletedProposal();
            }
        });
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