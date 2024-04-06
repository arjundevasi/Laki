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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentsActivity extends AppCompatActivity {

    TextView totalEarnedTextview;
    TextView outstandingTextview;
    String ownUid;

    ProgressBar progressBar;
    TextView historyTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        ownUid = FirebaseAuth.getInstance().getUid();

        Toolbar toolbar = findViewById(R.id.toolbar_payments_activity);
        toolbar.setTitle("Payments");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        totalEarnedTextview = findViewById(R.id.total_earned_textview_payments_activity);
        outstandingTextview = findViewById(R.id.outstanding_textview_payments_activity);
        progressBar = findViewById(R.id.progressbar_payments_activity);

        progressBar.setVisibility(View.VISIBLE);
        if (ownUid != null){
            fetchProposal();
            fetchTotalEarnedAndOutstanding();
        }else {
            ownUid = FirebaseAuth.getInstance().getUid();
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
    private void fetchProposal(){
        List<AcceptProposalObject> proposalList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("completed_Proposal");
        databaseReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        AcceptProposalObject proposalObject = dataSnapshot.getValue(AcceptProposalObject.class);
                        if (proposalObject != null){
                            String professionalUid = proposalObject.getProfessionalId();
                            if (professionalUid.equals(ownUid)){
                                proposalList.add(proposalObject);
                            }
                        }
                    }
                    setupRecycler(proposalList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("OngoingProposalListActivity", " failed to load proposals at fetchProposal " + error);
            }
        });
    }
    private void setupRecycler(List<AcceptProposalObject> proposalList){
        progressBar.setVisibility(View.GONE);
        RecyclerView recyclerView = findViewById(R.id.recylerview_payments_activity);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        PaymentsRecyclerViewAdapter adapter = new PaymentsRecyclerViewAdapter(proposalList,ownUid);
        recyclerView.setAdapter(adapter);
    }

    private void fetchTotalEarnedAndOutstanding(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        databaseReference.child("PaymentOutstanding").child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CalculateOutstandingPaymentObject paymentObject = snapshot.getValue(CalculateOutstandingPaymentObject.class);
                    if (paymentObject != null){
                        String totalEarned = paymentObject.getTotalEarned();
                        String outstanding = paymentObject.getTotalOutstanding();

                        totalEarnedTextview.setText(totalEarned);
                        outstandingTextview.setText(outstanding);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}