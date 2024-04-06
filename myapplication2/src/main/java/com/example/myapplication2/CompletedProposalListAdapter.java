package com.example.myapplication2;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CompletedProposalListAdapter extends RecyclerView.Adapter<CompletedProposalListAdapter.CompletedProposalViewHolder> {
    List<AcceptProposalObject> proposalList;
    String ownUid;

    public CompletedProposalListAdapter(List<AcceptProposalObject> proposalList) {
        this.proposalList = proposalList;
    }

    @NonNull
    @Override
    public CompletedProposalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_proposal_list_recyler_layout, parent, false);
        CompletedProposalViewHolder vh = new CompletedProposalViewHolder(view);

        view.setOnClickListener(v -> {
            int position = vh.getAdapterPosition();
            String proposalId = proposalList.get(position).getProposalId();

            Intent i = new Intent(parent.getContext(), ViewCompletedProposalActivity.class);
            i.putExtra("completedProposalId", proposalId);

            parent.getContext().startActivity(i);
        });

        ownUid = FirebaseAuth.getInstance().getUid();

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedProposalViewHolder holder, int position) {
        String title = proposalList.get(position).getTitle();
        String senderUid = proposalList.get(position).getSenderUid();
        String receiverUid = proposalList.get(position).getReceiverUid();

        if (senderUid.equals(ownUid)) {
            fetchProfileUrlandFullName(title, receiverUid, holder);
        } else {
            fetchProfileUrlandFullName(title, senderUid, holder);
        }

    }

    @Override
    public int getItemCount() {
        return proposalList.size();
    }

    public static class CompletedProposalViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView fullNameTextview;
        TextView titleTextview;

        public CompletedProposalViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview_pending_proposal_list_layout);
            fullNameTextview = itemView.findViewById(R.id.full_name_pending_proposal_list_layout);
            titleTextview = itemView.findViewById(R.id.proposal_title_pending_proposal_list_layout);
        }
    }

    private void fetchProfileUrlandFullName(String title, String senderUid, CompletedProposalViewHolder holder) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");
        databaseReference.child(senderUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    String fullName = uploadPersonforSeachIndex.getFullname();
                    String profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                    setupView(title, fullName, profileUrl, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PendingProposalListAdapter", "failed to load profile at searchIndex " + error);
            }
        });
    }

    private void setupView(String title, String fullName, String profileUrl, CompletedProposalViewHolder holder) {
        holder.titleTextview.setText(title);
        holder.fullNameTextview.setText(fullName);
        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(holder.imageView);
    }
}
