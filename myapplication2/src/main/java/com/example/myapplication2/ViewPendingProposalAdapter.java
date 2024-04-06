package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ViewPendingProposalAdapter extends RecyclerView.Adapter<ViewPendingProposalAdapter.ViewProposalViewHolder> {

    private final List<PendingProposalObject> proposalList;

    private String ownUid;

    private static String staticOwnUid;
    private final String oppositeUid;

    private final String oppositeFullName;
    private final String oppositeProfileUrl;

    private final RecyclerView recyclerView;

    private static String fcmToken;

    private static String ownFullname;

    public ViewPendingProposalAdapter(List<PendingProposalObject> proposalList, String oppositeFullName, String oppositeProfileUrl, String oppositeUid, RecyclerView recyclerView) {
        this.proposalList = proposalList;
        this.oppositeFullName = oppositeFullName;
        this.oppositeProfileUrl = oppositeProfileUrl;
        this.oppositeUid = oppositeUid;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewProposalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_proposal_adapter_layout, parent, false);

        ownUid = FirebaseAuth.getInstance().getUid();
        staticOwnUid = ownUid;
        checkFcmTokenIsGenerated(oppositeUid);

        return new ViewProposalViewHolder(view, proposalList, oppositeFullName, oppositeProfileUrl, oppositeUid, ownUid);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProposalViewHolder holder, int position) {

        // make accepted CardView layout invisible
        String senderUid = proposalList.get(position).senderUid;

        String hasAccepted = proposalList.get(position).getHasAccepted();


        int size = proposalList.size();

        if (size - position != 1) {
            holder.ownProposalAcceptButton.setVisibility(View.GONE);
            holder.oppositeProposalAcceptButton.setVisibility(View.GONE);

            holder.ownProposalEditButton.setVisibility(View.GONE);
            holder.oppositeProposalEditButton.setVisibility(View.GONE);
        } else {
            holder.ownProposalAcceptButton.setVisibility(View.VISIBLE);
            holder.oppositeProposalAcceptButton.setVisibility(View.VISIBLE);

            holder.ownProposalEditButton.setVisibility(View.VISIBLE);
            holder.oppositeProposalEditButton.setVisibility(View.VISIBLE);
        }


        switch (hasAccepted) {
            case "Yes":
                holder.ownProposalAcceptButton.setVisibility(View.GONE);
                holder.oppositeProposalAcceptButton.setVisibility(View.GONE);

                holder.ownProposalEditButton.setVisibility(View.GONE);
                holder.oppositeProposalEditButton.setVisibility(View.GONE);

                holder.oppositeProposalCancelButton.setVisibility(View.GONE);
                holder.ownProposalCancelButton.setVisibility(View.GONE);

                holder.acceptedProposalTextview.setVisibility(View.VISIBLE);
                break;
            case "Cancelled":
                holder.ownProposalAcceptButton.setVisibility(View.GONE);
                holder.ownProposalEditButton.setVisibility(View.GONE);
                holder.ownProposalCancelButton.setVisibility(View.GONE);

                holder.oppositeProposalAcceptButton.setVisibility(View.GONE);
                holder.oppositeProposalEditButton.setVisibility(View.GONE);
                holder.oppositeProposalCancelButton.setVisibility(View.GONE);

                holder.acceptedProposalTextview.setText("This proposal is cancelled");
                holder.acceptedProposalTextview.setVisibility(View.VISIBLE);
                break;
            case "Edited":
                holder.ownProposalCancelButton.setVisibility(View.GONE);
                holder.oppositeProposalCancelButton.setVisibility(View.GONE);
                break;
            default:
                holder.acceptedProposalTextview.setVisibility(View.GONE);
                break;
        }

        if (Objects.equals(ownUid, senderUid)) {
            holder.oppositeCardViewLayout.setVisibility(View.GONE);
            holder.ownCardViewLayout.setVisibility(View.VISIBLE);

            holder.ownProposalAcceptButton.setVisibility(View.GONE);

            String title = proposalList.get(position).getTitle();
            String details = proposalList.get(position).getDetails();
            String budget = "₹" + proposalList.get(position).getBudget();
            String date = proposalList.get(position).getStartDate();

            holder.ownProposalTitleTextView.setText(title);
            holder.ownProposalDetailsTextview.setText(details);
            holder.ownProposalBudgetTextview.setText(budget);
            holder.ownProposalStartDateTextview.setText(date);
        } else {
            holder.ownCardViewLayout.setVisibility(View.GONE);
            holder.oppositeCardViewLayout.setVisibility(View.VISIBLE);

            holder.ownProposalAcceptButton.setVisibility(View.VISIBLE);

            String title = proposalList.get(position).getTitle();
            String details = proposalList.get(position).getDetails();
            String budget = "₹" + proposalList.get(position).getBudget();
            String date = proposalList.get(position).getStartDate();

            holder.oppositeProposalTitleTextView.setText(title);
            holder.oppositeProposalDetailsTextview.setText(details);
            holder.oppositeProposalBudgetTextview.setText(budget);
            holder.oppositeProposalStartDateTextview.setText(date);

        }
    }

    @Override
    public int getItemCount() {
        return proposalList.size();
    }

    public static class ViewProposalViewHolder extends RecyclerView.ViewHolder {
        TextView ownProposalTitleTextView;
        TextView ownProposalDetailsTextview;
        TextView ownProposalBudgetTextview;

        Button ownProposalAcceptButton;
        Button ownProposalEditButton;

        Button ownProposalCancelButton;

        TextView oppositeProposalTitleTextView;
        TextView oppositeProposalDetailsTextview;
        TextView oppositeProposalBudgetTextview;

        Button oppositeProposalAcceptButton;
        Button oppositeProposalEditButton;

        Button oppositeProposalCancelButton;

        CardView ownCardViewLayout;
        CardView oppositeCardViewLayout;

        TextView ownProposalStartDateTextview;
        TextView oppositeProposalStartDateTextview;

        TextView acceptedProposalTextview;


        public ViewProposalViewHolder(@NonNull View itemView, List<PendingProposalObject> proposalList, String oppositeFullName, String oppositeProfileUrl, String oppositeUid, String ownUid) {
            super(itemView);
            ownProposalTitleTextView = itemView.findViewById(R.id.own_proposal_title_textview_view_proposal_activity);
            ownProposalDetailsTextview = itemView.findViewById(R.id.own_proposal_details_textview_view_proposal_activity);
            ownProposalBudgetTextview = itemView.findViewById(R.id.own_proposal_budget_textview_view_proposal_activity);
            ownProposalAcceptButton = itemView.findViewById(R.id.own_proposal_accept_button_view_proposal_activity);
            ownProposalEditButton = itemView.findViewById(R.id.own_proposal_edit_button_proposal_activity);
            ownCardViewLayout = itemView.findViewById(R.id.own_proposal_cardview_layout);
            ownProposalStartDateTextview = itemView.findViewById(R.id.own_proposal_start_date_textview_view_proposal_activity);
            ownProposalCancelButton = itemView.findViewById(R.id.own_proposal_cancel_button_proposal_activity);


            oppositeProposalTitleTextView = itemView.findViewById(R.id.opposite_proposal_title_textview_view_proposal_activity);
            oppositeProposalDetailsTextview = itemView.findViewById(R.id.opposite_proposal_details_textview_view_proposal_activity);
            oppositeProposalBudgetTextview = itemView.findViewById(R.id.opposite_proposal_budget_textview_view_proposal_activity);
            oppositeProposalAcceptButton = itemView.findViewById(R.id.opposite_proposal_accept_button_view_proposal_activity);
            oppositeProposalEditButton = itemView.findViewById(R.id.opposite_proposal_edit_button_proposal_activity);
            oppositeCardViewLayout = itemView.findViewById(R.id.opposite_proposal_cardview_layout);
            oppositeProposalStartDateTextview = itemView.findViewById(R.id.opposite_proposal_start_date_textview_view_proposal_activity);
            oppositeProposalCancelButton = itemView.findViewById(R.id.opposite_proposal_cancel_button_proposal_activity);

            acceptedProposalTextview = itemView.findViewById(R.id.proposal_accepted_textview_proposal_activity);

            ownProposalAcceptButton.setOnClickListener(v -> {
                String proposalId = proposalList.get(getAdapterPosition()).getProposalId();
                String counterProposalId = proposalList.get(getAdapterPosition()).getCounterProposalId();

                PendingProposalObject proposal = proposalList.get(getAdapterPosition());


                if (counterProposalId != null) {
                    acceptProposal(proposalId, counterProposalId, proposal, ownUid, oppositeUid);
                } else if (proposalId != null) {
                    acceptProposal(proposalId, null, proposal, ownUid, oppositeUid);
                } else {
                    Toast.makeText(itemView.getContext(), "Unable to accept proposal", Toast.LENGTH_SHORT).show();
                    Log.d("ViewPendingProposalAdapter", "error in accepting proposal. either proposalId or counterProposalId are null. happened at : acceptButtonOnclick");
                }
            });

            oppositeProposalAcceptButton.setOnClickListener(v -> {
                String proposalId = proposalList.get(getAdapterPosition()).getProposalId();
                String counterProposalId = proposalList.get(getAdapterPosition()).getCounterProposalId();

                PendingProposalObject proposal = proposalList.get(getAdapterPosition());

                if (counterProposalId != null) {
                    acceptProposal(proposalId, counterProposalId, proposal, ownUid, oppositeUid);
                } else if (proposalId != null) {
                    acceptProposal(proposalId, null, proposal, ownUid, oppositeUid);
                } else {
                    Toast.makeText(itemView.getContext(), "Unable to accept proposal", Toast.LENGTH_SHORT).show();
                    Log.d("ViewPendingProposalAdapter", "error in accepting proposal. either proposalId or counterProposalId are null. happened at : acceptButtonOnclick");
                }
            });

            ownProposalEditButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Context context = itemView.getContext();

                PendingProposalObject proposal = proposalList.get(position);

                sendProposalForEdit(context, proposal, oppositeFullName, oppositeProfileUrl, oppositeUid, ownUid);
            });


            oppositeProposalEditButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Context context = itemView.getContext();

                PendingProposalObject proposal = proposalList.get(position);

                sendProposalForEdit(context, proposal, oppositeFullName, oppositeProfileUrl, oppositeUid, ownUid);
            });


            ownProposalCancelButton.setOnClickListener(v -> {
                String proposalId = proposalList.get(getAdapterPosition()).getProposalId();
                String counterProposalId = proposalList.get(getAdapterPosition()).getCounterProposalId();

                cancelProposal(proposalId, counterProposalId,ownUid,oppositeUid);



            });

            oppositeProposalCancelButton.setOnClickListener(v -> {
                String proposalId = proposalList.get(getAdapterPosition()).getProposalId();
                String counterProposalId = proposalList.get(getAdapterPosition()).getCounterProposalId();

                cancelProposal(proposalId,counterProposalId,ownUid,oppositeUid);
            });
        }

        private void sendProposalForEdit(Context context, PendingProposalObject proposal, String oppositeFullName, String oppositeProfileUrl, String oppositeUid, String ownUid) {

            // here opposite uid means the uid that was passed by notification adapter to view proposal activity which then passed it to this adapter
            // the issue is if i sent a proposal then i am sender. if i click on edit on the proposal that i sent then senderUid == oppositeUid and i become both ownUid and opposite Uid
            // so to counter this we check if opposite == ownUid then we say oppositeUid == receiverUid.


            if (oppositeUid.equals(ownUid)) {
                oppositeUid = proposal.getReceiverUid();
            }


            String counterProposalId = proposal.getCounterProposalId();

            Intent i = new Intent(context, ProposalActivity.class);

            i.putExtra("isEditing", "Yes");
            i.putExtra("proposalId", proposal.getProposalId());
            i.putExtra("title", proposal.getTitle());
            i.putExtra("details", proposal.getDetails());
            i.putExtra("budget", proposal.getBudget());
            i.putExtra("senderUid", oppositeUid);
            i.putExtra("oppositeFullName", oppositeFullName);
            i.putExtra("oppositeProfileUrl", oppositeProfileUrl);
            i.putExtra("professionalId", proposal.getProfessionalId());
            i.putExtra("customerId", proposal.getCustomerId());
            i.putExtra("date",proposal.getStartDate());

            if (counterProposalId != null) {
                i.putExtra("counterProposalId", counterProposalId);
            } else {
                i.putExtra("counterProposalId", "");
            }
            context.startActivity(i);
        }


        private void acceptProposal(String proposalId, String counterProposalId, PendingProposalObject pendingProposalObject, String ownUid, String oppositeUid) {

            //here proposal id may mean both counterProposalId and proposalId

            String acceptedProposalId;
            if (counterProposalId == null) {
                acceptedProposalId = proposalId;
            } else {
                acceptedProposalId = counterProposalId;
            }

            String proposalTitle = pendingProposalObject.getTitle();
            String details = pendingProposalObject.getDetails();
            String budget = pendingProposalObject.getBudget();
            String professionalId = pendingProposalObject.getProfessionalId();
            String customerId = pendingProposalObject.getCustomerId();
            String startDate = pendingProposalObject.getStartDate();

            String hasAccepted = "Yes";

            DatabaseReference notificationReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications");
            String timestamp = String.valueOf(System.currentTimeMillis());
            String isSeen = "No";
            String notificationId = "notification_accepted" + acceptedProposalId;

            NotificationsObject notification = new NotificationsObject(ownUid, oppositeUid, acceptedProposalId, proposalTitle, timestamp, isSeen, notificationId, null, null, acceptedProposalId, null,null,null);


            AcceptProposalObject proposal = new AcceptProposalObject(ownUid, oppositeUid, acceptedProposalId, proposalTitle, details, budget, timestamp, hasAccepted, ownUid, professionalId, customerId, "No", null,startDate);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("ongoing_Proposal");

            // we are filling ongoing collection with proposal since it has been accepted
            databaseReference.child(ownUid).child(acceptedProposalId).setValue(proposal, (error, ref) -> {
                if (error != null) {
                    // check for error
                    Toast.makeText(itemView.getContext(), "Unable to accept proposal", Toast.LENGTH_SHORT).show();
                    Log.d("ViewPendingProposalAdapter", "Error accepting proposal in ownUid acceptProposal Method. error is : " + error);
                } else {

                    // we are also filling data for other user
                    databaseReference.child(oppositeUid).child(acceptedProposalId).setValue(proposal, (error1, ref1) -> {
                        if (error1 != null) {
                            // again checking error
                            Toast.makeText(itemView.getContext(), "Unable to accept proposal", Toast.LENGTH_SHORT).show();
                            Log.d("ViewPendingProposalAdapter", "Error accepting proposal in oppositeUid acceptProposal Method. error is : " + error1);
                        } else {

                            // for both the user the proposal data for ongoing collection is filled.


                            //  now sending notifications
                            notificationReference.child(oppositeUid).child(notificationId).setValue(notification);


                            // update hasAccepted data from pending collection since it has been accepted.

                            HashMap<String, Object> updates = new HashMap<>();
                            updates.put("hasAccepted", "Yes");

                            DatabaseReference pendingProposalReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("pending_Proposal");

                            if (counterProposalId != null) {
                                pendingProposalReference.child(ownUid).child(proposalId).child("counterProposal").child(counterProposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pendingProposalReference.child(oppositeUid).child(proposalId).child("counterProposal").child(counterProposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                Toast.makeText(itemView.getContext(), "Proposal Accepted", Toast.LENGTH_SHORT).show();
                                                findOwnFullnameAndSendNotification(acceptedProposalId,proposalTitle,itemView.getContext());
                                            }
                                        });
                                    }
                                });

                            } else {
                                pendingProposalReference.child(ownUid).child(proposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        pendingProposalReference.child(oppositeUid).child(proposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(itemView.getContext(), "Proposal Accepted", Toast.LENGTH_SHORT).show();
                                                findOwnFullnameAndSendNotification(acceptedProposalId,proposalTitle,itemView.getContext());
                                            }
                                        });
                                    }
                                });
                            }

                        }
                    });
                }
            });
        }

        private void cancelProposal(String proposalId,String counterProposalId, String ownUid, String oppositeUid){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("pending_Proposal");

            HashMap<String, Object> cancelProposalHashMap = new HashMap<>();
            cancelProposalHashMap.put("hasAccepted","Cancelled");

            if (counterProposalId == null){
                databaseReference.child(ownUid).child(proposalId).updateChildren(cancelProposalHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child(oppositeUid).child(proposalId).updateChildren(cancelProposalHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent i = new Intent(itemView.getContext(), MainActivity.class);
                                itemView.getContext().startActivity(i);
                            }
                        });
                    }
                });
            }else {
                databaseReference.child(ownUid).child(proposalId).child("counterProposal").child(counterProposalId).updateChildren(cancelProposalHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child(oppositeUid).child(proposalId).child("counterProposal").child(counterProposalId).updateChildren(cancelProposalHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                    Intent i = new Intent(itemView.getContext(), MainActivity.class);
                                    itemView.getContext().startActivity(i);
                            }
                        });
                    }
                });

            }

        }

    }

    public void scrollToLastItem() {
        if (proposalList.size() > 0) {
            recyclerView.scrollToPosition(proposalList.size() - 1);
        }
    }

    private static void findOwnFullnameAndSendNotification(String notificationProposalId, String notificationTitle,Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String sharedFullname = sharedPreferences.getString("fullname", null);



        if (sharedFullname == null) {

            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(staticOwnUid);

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            ownFullname = uploadPersonforSeachIndex.getFullname();
                            if (fcmToken != null){
                                try {
                                    SendPushNotification.sendAcceptedProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,context);
                                } catch (JSONException e) {
                                    Log.d("ViewPendingProposalActivity","error sending notification in pending proposal. error is: " + e);
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
                    SendPushNotification.sendAcceptedProposalNotification(ownFullname,notificationProposalId,notificationTitle,fcmToken,context);
                } catch (JSONException e) {
                    Log.d("ViewPendingProposalActivity","error sending notification in pending proposal. error is: " + e);
                }
            }
        }

        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
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
}
