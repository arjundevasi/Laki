package com.example.myapplication2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaymentsRecyclerViewAdapter extends RecyclerView.Adapter<PaymentsRecyclerViewAdapter.PaymentsAdapterViewHolder> {
    List<AcceptProposalObject> list;
    String ownUid;
    public PaymentsRecyclerViewAdapter(List<AcceptProposalObject> completedList,String ownUid){
        this.list = completedList;
        this.ownUid = ownUid;
    }

    @NonNull
    @Override
    public PaymentsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_proposal_list_recyler_layout,parent,false);
        return new PaymentsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentsAdapterViewHolder holder, int position) {
        String professionalId = list.get(position).getProfessionalId();
        String customerId = list.get(position).getCustomerId();
        String title = list.get(position).getTitle();
        String budget = list.get(position).getBudget();

        if (ownUid.equals(professionalId)){
            findFullNameAndProfileAndSetData(customerId,title,budget,holder);
        }else {
            findFullNameAndProfileAndSetData(professionalId,title,budget,holder);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PaymentsAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImageview;
        TextView titleTextview;
        TextView budgetTextview;
        public PaymentsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageview = itemView.findViewById(R.id.imageview_pending_proposal_list_layout);
            titleTextview = itemView.findViewById(R.id.full_name_pending_proposal_list_layout);
            budgetTextview = itemView.findViewById(R.id.proposal_title_pending_proposal_list_layout);
        }
    }
    private void findFullNameAndProfileAndSetData(String uid,String title,String budget,PaymentsAdapterViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex");
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String profileUrl = uploadPersonforSeachIndex.getProfileUrl();

                        Picasso.get().load(profileUrl).transform(new RoundImageTransformation()).resize(1024,1024).centerCrop().into(holder.profileImageview);
                        holder.titleTextview.setText(title);
                        holder.budgetTextview.setText(budget);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PendingProposalListAdapter", "failed to load profile at searchIndex " + error);
            }
        });
    }
}
