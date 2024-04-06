package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OngoingProposalListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String ownUid;

    List<AcceptProposalObject> proposalList;

    TextView noDataTextview;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_propsal);

        ownUid = FirebaseAuth.getInstance().getUid();
        recyclerView = findViewById(R.id.recyclerview_ongoing_proposal_activity);

        noDataTextview = findViewById(R.id.no_data_textview_ongoing_proposal_list_activity);
        progressBar = findViewById(R.id.progress_bar_ongoing_proposal_list_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_ongoing_proposal_list_activity);
        toolbar.setTitle("Ongoing Proposal");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        proposalList = new ArrayList<>();
        fetchProposal();
    }

    private void fetchProposal() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("ongoing_Proposal");
        databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AcceptProposalObject proposalObject = dataSnapshot.getValue(AcceptProposalObject.class);
                        if (proposalObject != null){
                            String hasAcceppted = proposalObject.getHasAccepted();
                            String professionalMarkedAsCompleted = proposalObject.getProfessionalMarkedAsCompleted();

                            if (!hasAcceppted.equals("Yes") || !professionalMarkedAsCompleted.equals("Yes")) {
                                proposalList.add(proposalObject);
                            }
                        }
                    }

                    if (!proposalList.isEmpty()){
                        setupRecycler(proposalList);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.GONE);
                        noDataTextview.setVisibility(View.VISIBLE);
                    }

                }else {
                    progressBar.setVisibility(View.GONE);
                    noDataTextview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("OngoingProposalListActivity", " failed to load proposals at fetchProposal " + error);
            }
        });

    }

    private void setupRecycler(List<AcceptProposalObject> proposalList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());


        OngoingProposalListAdapter adapter = new OngoingProposalListAdapter(proposalList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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