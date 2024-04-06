package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class CompletedProposalListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String ownUid;

    List<AcceptProposalObject> proposalList;

    ProgressBar progressBar;
    CardView recylerCardview;

    TextView noDataTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_list_proposal);

        ownUid = FirebaseAuth.getInstance().getUid();

        proposalList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerview_completed_proposal_activity);

        progressBar = findViewById(R.id.progressbar_completed_proposal_list_activiy);
        recylerCardview = findViewById(R.id.cardview_recyler_completed_proposal_list_activity);

        noDataTextview = findViewById(R.id.no_data_textview_completed_proposal_list_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_completed_proposal_list_activity);
        toolbar.setTitle("Completed Proposal");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fetchCompletedProposal();
    }

    private void fetchCompletedProposal() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("completed_Proposal");
        databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AcceptProposalObject proposalObject = dataSnapshot.getValue(AcceptProposalObject.class);
                        proposalList.add(proposalObject);
                    }
                    setupRecycler(proposalList);
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
        if (!proposalList.isEmpty()){
            progressBar.setVisibility(View.GONE);
            noDataTextview.setVisibility(View.GONE);

            recylerCardview.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

            CompletedProposalListAdapter adapter = new CompletedProposalListAdapter(proposalList);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}