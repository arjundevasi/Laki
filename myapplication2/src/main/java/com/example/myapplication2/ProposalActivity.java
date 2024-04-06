package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProposalActivity extends AppCompatActivity {

    String fullName;
    String profileUrl;

    EditText titleEdittext;
    EditText detailsEdittext;
    EditText budgetEdittext;

    String ownUid;

    String hasAccepted = "No";

    String oppositeUid;

    // here editing means the values come from view proposal activity when edit button is clicked.
    String editingProposalId;

    String editingCounterProposalId;
    String editingTitle;
    String editingDetails;
    String editingBudget;

    String isEditing = "No";


    String oppositeProfileUrl;
    String oppositeFullName;

    ProgressBar progressBar;


    String professionalId;
    String customerId;
    Button submitButton;

    String ownFullname;

    String fcmToken = null;

    Button selectDateButton;
    TextView dateTextView;
    Button editDateButton;

    String date;

    String editingDate;

    LinearLayout selectedDateLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal);

        ImageView profileImageview = findViewById(R.id.profile_imageview_proposal_activity);
        TextView fullNameTextview = findViewById(R.id.fullname_textview_proposal_activity);

        titleEdittext = findViewById(R.id.title_edittext_proposal_activity);
        detailsEdittext = findViewById(R.id.details_edittext_proposal_activity);
        budgetEdittext = findViewById(R.id.budget_edittext_proposal_activity);

        submitButton = findViewById(R.id.submit_button_proposal_activity);
        progressBar = findViewById(R.id.progressbar_proposal_activity);

        selectDateButton = findViewById(R.id.select_date_button_proposal_activity);
        dateTextView = findViewById(R.id.date_textview_proposal_activity);
        editDateButton = findViewById(R.id.edit_date_button_proposal_actvity);
        selectedDateLayout = findViewById(R.id.selected_date_linear_layout);

        Toolbar toolbar = findViewById(R.id.toolbar_proposal_activity);
        toolbar.setTitle("Hire");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ownUid = FirebaseAuth.getInstance().getUid();

        Intent i = getIntent();
        if (i != null) {

            isEditing = i.getStringExtra("isEditing");

            if (isEditing.equals("Yes")) {
                // if proposal is being editing
                // this data is when proposal is edited comes from viewProposal activity.
                editingProposalId = i.getStringExtra("proposalId");
                editingTitle = i.getStringExtra("title");
                editingDetails = i.getStringExtra("details");
                editingBudget = i.getStringExtra("budget");
                editingDate = i.getStringExtra("date");

                oppositeUid = i.getStringExtra("senderUid");
                oppositeFullName = i.getStringExtra("oppositeFullName");
                oppositeProfileUrl = i.getStringExtra("oppositeProfileUrl");
                editingCounterProposalId = i.getStringExtra("counterProposalId");
                professionalId = i.getStringExtra("professionalId");
                customerId = i.getStringExtra("customerId");
                date = editingDate;

                titleEdittext.setText(editingTitle);
                detailsEdittext.setText(editingDetails);
                budgetEdittext.setText(editingBudget);

                // we have date so make select date button invisible
                selectDateButton.setVisibility(View.GONE);
                selectedDateLayout.setVisibility(View.VISIBLE);
                dateTextView.setText(editingDate);


                fullNameTextview.setText(oppositeFullName);
                Picasso.get().load(oppositeProfileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageview);

                checkFcmTokenIsGenerated(oppositeUid);

            } else {
                //if proposal is new
                // this data comes from myprofile activity
                fullName = i.getStringExtra("fullName");
                profileUrl = i.getStringExtra("profileUrl");
                oppositeUid = i.getStringExtra("uid");

                fullNameTextview.setText(fullName);
                Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageview);

                checkFcmTokenIsGenerated(oppositeUid);
            }
        }

        if (date == null && editingDate == null){
            selectDateButton.setVisibility(View.VISIBLE);
            selectedDateLayout.setVisibility(View.GONE);
        }else {
            selectDateButton.setVisibility(View.GONE);
            selectedDateLayout.setVisibility(View.VISIBLE);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (titleEdittext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ProposalActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (detailsEdittext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ProposalActivity.this, "Please enter details", Toast.LENGTH_SHORT).show();
                } else if (budgetEdittext.getText().toString().trim().isEmpty()) {
                    Toast.makeText(ProposalActivity.this, "Please enter price", Toast.LENGTH_SHORT).show();
                } else if (date == null) {
                    Toast.makeText(ProposalActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                } else {
                    if (isEditing.equals("No")) {
                        submitButton.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        sendNewProposal();
                    } else {
                        submitButton.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        sendCounterProposal(editingProposalId);
                    }

                }
            }
        });

        selectDateButton.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        editDateButton.setOnClickListener(v -> {
            showDatePickerDialog();
        });
    }

    private void sendNewProposal() {
        String title = titleEdittext.getText().toString().trim();
        String details = detailsEdittext.getText().toString().trim();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String budget = budgetEdittext.getText().toString().trim();
        String proposalId = ownUid + "_" + oppositeUid + "_" + timestamp;
        String startDate = date;

        //notifications
        String notificationId = "Notification_" + proposalId;
        String isSeen = "No";

        DatabaseReference proposalReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");
        DatabaseReference notificationsReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications");

        PendingProposalObject proposal = new PendingProposalObject(ownUid, oppositeUid, proposalId, title, details, budget, timestamp, hasAccepted, null, "none", oppositeUid, ownUid,startDate);

        NotificationsObject notificationsObject = new NotificationsObject(ownUid, oppositeUid, proposalId, title, timestamp, isSeen, notificationId, null, proposalId, null, null,null,null);

        proposalReference.child(ownUid).child(proposalId).setValue(proposal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                proposalReference.child(oppositeUid).child(proposalId).setValue(proposal, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notificationsReference.child(oppositeUid).child(notificationId).setValue(notificationsObject);
                        findOwnFullnameAndSendNotification(proposalId,title,null,oppositeUid,ownUid);

                        Toast.makeText(ProposalActivity.this, "Proposal sent", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

            }
        });
    }

    private void sendCounterProposal(String proposalId) {
        String title = titleEdittext.getText().toString().trim();
        String details = detailsEdittext.getText().toString().trim();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String budget = budgetEdittext.getText().toString().trim();
        String counterProposalId = ownUid + "_" + oppositeUid + "_" + timestamp;
        String startDate = date;
        //notifications
        String notificationId = "Notification_" + proposalId;
        String isSeen = "No";

        DatabaseReference proposalReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");
        DatabaseReference notificationsReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications");

        PendingProposalObject proposal = new PendingProposalObject(ownUid, oppositeUid, proposalId, title, details, budget, timestamp, hasAccepted, counterProposalId, "none", professionalId, customerId,startDate);

        NotificationsObject notificationsObject = new NotificationsObject(ownUid, oppositeUid, proposalId, title, timestamp, isSeen, notificationId, counterProposalId, proposalId, null, null,null,null);

        // adding to database
        proposalReference.child(ownUid).child(proposalId).child("counterProposal").child(counterProposalId).setValue(proposal, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                //adding to database
                proposalReference.child(oppositeUid).child(proposalId).child("counterProposal").child(counterProposalId).setValue(proposal, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        notificationsReference.child(oppositeUid).child(notificationId).setValue(notificationsObject, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                //now change earlier proposal hasAccepted to 'editing'
                                if (editingCounterProposalId != null) {
                                    setHasAcceptedToEdited(proposalId, editingCounterProposalId);
                                    findOwnFullnameAndSendNotification(proposalId,title,"counterNotification",oppositeUid,ownUid);
                                } else {
                                    setHasAcceptedToEdited(proposalId, null);
                                    findOwnFullnameAndSendNotification(proposalId,title,"counterNotification",oppositeUid,ownUid);

                                }
                                // counter proposal sent
                                Intent i = new Intent(ProposalActivity.this,MainActivity.class);
                                startActivity(i);
                            }
                        });
                    }
                });
            }
        });

    }

    private void setHasAcceptedToEdited(String editingProposalId, String editingCounterProposalId) {
        DatabaseReference proposalReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");


        Map<String, Object> update = new HashMap<>();


        if (editingCounterProposalId != null && !editingCounterProposalId.equals("")) {
            update.put("hasAccepted", "Edited");
            proposalReference.child(ownUid).child(editingProposalId).child("counterProposal").child(editingCounterProposalId).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    proposalReference.child(oppositeUid).child(editingProposalId).child("counterProposal").child(editingCounterProposalId).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProposalActivity.this, "Proposal sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        }else{
            update.put("hasAccepted", "Edited");
            proposalReference.child(ownUid).child(editingProposalId).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    proposalReference.child(oppositeUid).child(editingProposalId).updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProposalActivity.this, "Proposal sent", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }
    }

    private void findOwnFullnameAndSendNotification(String notificationProposalId, String notificationTitle,String counterNotificationChecck,String receiverUid,String senderUid) {
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
                            if (counterNotificationChecck == null){
                                if (fcmToken != null){
                                    try {
                                        SendPushNotification.sendNewProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ProposalActivity.this,receiverUid,senderUid);
                                    } catch (JSONException e) {
                                        Log.d("ProposalActivity","error sending notification in new proposal. error is: " + e);
                                    }
                                }
                            }else {
                                if (fcmToken != null){
                                    try {
                                        SendPushNotification.sendCounterProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ProposalActivity.this,receiverUid,senderUid);
                                    } catch (JSONException e) {
                                        Log.d("ProposalActivity","error sending notification in counter proposal. error is: " + e);
                                    }
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
            if (counterNotificationChecck == null){
                if (fcmToken != null){
                    try {
                        SendPushNotification.sendNewProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ProposalActivity.this,receiverUid,senderUid);
                    } catch (JSONException e) {
                        Log.d("ProposalActivity","error sending notification in new proposal. error is: " + e);
                    }
                }
            }else {
                if (fcmToken != null){
                    try {
                        SendPushNotification.sendCounterProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,ProposalActivity.this,receiverUid,senderUid);
                    } catch (JSONException e) {
                        Log.d("ProposalActivity","error sending notification in counter proposal. error is: " + e);
                    }
                }
            }
        }
    }

    private void checkFcmTokenIsGenerated(String receiverUid){
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
    private void showDatePickerDialog() {
        // Get the current date as the default selected date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // This method will be called when the user selects a date.
                        // You can store the selected date in a variable or perform actions with it.
                        String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        // Update a TextView or EditText with the selected date
                        dateTextView.setText(selectedDate);
                        date = selectedDate;
                        selectedDateLayout.setVisibility(View.VISIBLE);
                        selectDateButton.setVisibility(View.GONE);
                    }
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}