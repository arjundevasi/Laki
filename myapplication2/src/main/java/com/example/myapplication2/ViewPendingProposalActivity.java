package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewPendingProposalActivity extends AppCompatActivity {

    TextView fullNameTextview;

    ImageView profileImageview;

    ProgressBar progressBar;

    String oppositeFullName = null;
    String oppositeProfileUrl = null;
    String hasAccepted = "No";

    String senderUid = null;
    String proposalId = null;

    String ownUid;


    LinearLayout oppositeLinearLayout;
    RecyclerView recyclerView;

    DatabaseReference databaseReference;

    List<PendingProposalObject> proposalList;

    String receiverUid;

    NestedScrollView nestedScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pending_proposal);

        ownUid = FirebaseAuth.getInstance().getUid();

        fullNameTextview = findViewById(R.id.fullname_textview_view_proposal_activity);
        profileImageview = findViewById(R.id.profile_imageview_view_proposal_activity);

        oppositeLinearLayout = findViewById(R.id.opposite_fullname_profile_image_layout_view_proposal_activity);
        recyclerView = findViewById(R.id.recyclerview_view_proposal_activity);

        proposalList = new ArrayList<>();

        progressBar = findViewById(R.id.progressbar_view_proposal_activity);

        nestedScrollView = findViewById(R.id.nested_scroll_view_view_pending_proposal_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_view_pending_proposal_activity);
        toolbar.setTitle("Pending Proposal");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();


        // the intent gets data from 2 adapters notificationAdapter and pendingProposalListAdapter
        Intent i = getIntent();
        if (i != null) {
            senderUid = i.getStringExtra("senderUid");
            proposalId = i.getStringExtra("proposalId");

            receiverUid = i.getStringExtra("receiverUid");

            if (senderUid.equals(ownUid)) {
                findOppositeFullNameAndProfile(receiverUid);
            } else {
                findOppositeFullNameAndProfile(senderUid);
            }

            fetchProposal(proposalId);

        }


    }

    private void fetchProposal(String proposalId) {
        proposalList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        databaseReference.child("pending_Proposal").child(ownUid).child(proposalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PendingProposalObject proposal = snapshot.getValue(PendingProposalObject.class);
                    if (proposal != null) {
                        hasAccepted = proposal.getHasAccepted();

                        proposalList.add(proposal);

                        fetchCounterProposal(proposalId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewPendingProposalActivity.this, "Unable to load proposal", Toast.LENGTH_SHORT).show();
                Log.d("ViewPendingProposalActivity", "database failed to load pending proposal collection");
            }
        });
    }

    private void setOppositeFullNameAndProfile(String fullName, String profileUrl) {
        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageview);


        fullNameTextview.setText(fullName);

    }

    private void fetchCounterProposal(String proposalId) {

        String ownUid = FirebaseAuth.getInstance().getUid();

        if (ownUid != null){
            databaseReference.child("pending_Proposal").child(ownUid).child(proposalId).child("counterProposal").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot != null) {
                                PendingProposalObject proposal = dataSnapshot.getValue(PendingProposalObject.class);
                                proposalList.add(proposal);
                            }
                        }
                    }
                    setupRecyclerView(proposalList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ViewPendingProposalActivity.this, "Unable to laod proposal", Toast.LENGTH_SHORT).show();
                    Log.d("ViewPendingProposalActivity", "error in retrieving counter posts. " + error);
                }
            });
        }else {
            Toast.makeText(this, "unable to load", Toast.LENGTH_SHORT).show();
        }

    }

    private void findOppositeFullNameAndProfile(String senderUid) {
        oppositeFullName = null;
        oppositeProfileUrl = null;

        databaseReference.child("searchIndex").child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null) {
                        oppositeFullName = uploadPersonforSeachIndex.getFullname();
                        oppositeProfileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        setOppositeFullNameAndProfile(oppositeFullName, oppositeProfileUrl);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ViewPendingProposalActivity", "failed to load profile image and full name");
            }
        });
    }

    private void setupRecyclerView(List<PendingProposalObject> proposalList) {

        if (!proposalList.isEmpty()){
            progressBar.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            ViewPendingProposalAdapter adapter = new ViewPendingProposalAdapter(proposalList, oppositeFullName, oppositeProfileUrl, senderUid, recyclerView);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            adapter.scrollToLastItem();

            progressBar.setVisibility(View.GONE);
            oppositeLinearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            if (isTaskRoot()){
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
            }else {
                onBackPressed();
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
        }else {
            finish();
        }
        super.onBackPressed();
    }
}