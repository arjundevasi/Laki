package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PendingProposalListActivity extends AppCompatActivity {

    String ownUid;
    RecyclerView recyclerView;

    List<PendingProposalObject> proposalList;

    TextView noDataTextview;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_proposal_list);

        recyclerView = findViewById(R.id.recyclerview_pending_proposal_list_activity);

        ownUid = FirebaseAuth.getInstance().getUid();

        noDataTextview = findViewById(R.id.no_data_textview_pending_proposal_list_activity);
        progressBar = findViewById(R.id.progress_bar_pending_proposal_list_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_pending_proposal_list_activity);
        toolbar.setTitle("Pending Proposal");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fetchPendingProposal();
    }

    private void fetchPendingProposal() {

        proposalList = new ArrayList<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");

        databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String proposalId = null;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PendingProposalObject pendingProposalObject = dataSnapshot.getValue(PendingProposalObject.class);

                        if (pendingProposalObject != null){
                            String hasAccepted = pendingProposalObject.getHasAccepted();

                            proposalId = pendingProposalObject.getProposalId();

                            if (hasAccepted.equals("No")) {
                                proposalList.add(pendingProposalObject);
                            }
                        }

                        if (proposalId != null) {
                            fetchCounterProposal(proposalId);
                        }
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    noDataTextview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PendingProposalListActivity", "error fetching pending proposal. " + error);
                Toast.makeText(PendingProposalListActivity.this, "failed to load proposals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecycler(List<PendingProposalObject> proposalList) {

        if (!proposalList.isEmpty()){
            noDataTextview.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            noDataTextview.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        PendingProposalListAdapter adapter = new PendingProposalListAdapter(proposalList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void fetchCounterProposal(String proposalId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");
        databaseReference.child(ownUid).child(proposalId).child("counterProposal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PendingProposalObject pendingProposalObject = dataSnapshot.getValue(PendingProposalObject.class);

                        if (pendingProposalObject != null){
                            String hasAccepted = pendingProposalObject.getHasAccepted();

                            if (hasAccepted.equals("No")) {
                                proposalList.add(pendingProposalObject);
                                setupRecycler(proposalList);
                            }
                        }
                    }
                }else {
                    setupRecycler(proposalList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PendingProposalListActivity", "error adding counter proposal to list");
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
}