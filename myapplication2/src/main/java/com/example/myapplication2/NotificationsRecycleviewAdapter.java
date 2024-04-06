package com.example.myapplication2;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsRecycleviewAdapter extends RecyclerView.Adapter<NotificationsRecycleviewAdapter.ViewHolder> {


    private final List<NotificationsObject> notificationsList;

    String ownUid;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        ImageView profileImageview;

        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recycler_textview);
            profileImageview = itemView.findViewById(R.id.profile_imageview_notofication_layout);
            layout = itemView.findViewById(R.id.linear_layout_notification_layout);
        }

    }

    public NotificationsRecycleviewAdapter(List<NotificationsObject> list) {
        notificationsList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recyclerview_layout, parent, false);

        ViewHolder vh = new ViewHolder(view);

        ownUid = FirebaseAuth.getInstance().getUid();


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = vh.getAdapterPosition();

                String type = notificationsList.get(position).getType();

                if (type == null){
                    // we decide by checking proposal id that whether this is a pending proposal or accepted proposal or
                    // completed proposal and we direct the user accordingly.

                    String pendingProposal = notificationsList.get(position).getPendingProposalId();
                    String acceptedProposal = notificationsList.get(position).getOngoingProposalId();
                    String completedProposal = notificationsList.get(position).getCompletedProposalId();


                    String notificationId = null;
                    if (position != RecyclerView.NO_POSITION) {
                        // if the notification is about new proposal which is pending proposal.
                        if (pendingProposal != null) {

                            Intent i = new Intent(view.getContext(), ViewPendingProposalActivity.class);

                            String senderUid = notificationsList.get(position).senderUid;
                            String receiverUid = notificationsList.get(position).receiverUid;
                            String proposalId = notificationsList.get(position).proposalId;
                            String counterProposalId = notificationsList.get(position).counterProposalId;
                            notificationId = notificationsList.get(position).notificationId;


                            if (counterProposalId == null) {
                                i.putExtra("senderUid", senderUid);
                                i.putExtra("receiverUid", receiverUid);
                                i.putExtra("proposalId", proposalId);
                            } else {
                                i.putExtra("senderUid", senderUid);
                                i.putExtra("proposalId", proposalId);
                                i.putExtra("receiverUid", receiverUid);
                                i.putExtra("counterProposalId", counterProposalId);
                            }

                            // set notification seen

                            setNotificationAsSeen(notificationId);


                            view.getContext().startActivity(i);

                        } else if (acceptedProposal != null) {

                            notificationId = notificationsList.get(position).notificationId;

                            // set notification seen

                            setNotificationAsSeen(notificationId);


                            Intent i = new Intent(parent.getContext(), ViewAcceptedProposalActivity.class);
                            i.putExtra("ongoing_proposal_id", acceptedProposal);
                            view.getContext().startActivity(i);

                        } else if (completedProposal != null) {

                            notificationId = notificationsList.get(position).notificationId;

                            // set notification seen

                            setNotificationAsSeen(notificationId);


                            Intent i = new Intent(parent.getContext(), ViewCompletedProposalActivity.class);
                            i.putExtra("completedProposalId", completedProposal);
                            view.getContext().startActivity(i);

                        } else {
                            Toast.makeText(view.getContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                            Log.d("NotificationRecyclerviewAdapter", "error in notification recycler view");
                        }
                    }
                }else {
                    if (position != RecyclerView.NO_POSITION){
                        String notificationId = notificationsList.get(position).notificationId;

                        // set notification seen

                        setNotificationAsSeen(notificationId);


                        Intent i = new Intent(parent.getContext(), PostDetailsActivity.class);
                        i.putExtra("postId", notificationsList.get(position).getPostId());
                        if (type.equals("like")){
                            i.putExtra("like",type);
                        } else if (type.equals("comment")) {
                            i.putExtra("comment",type);
                        }
                        view.getContext().startActivity(i);
                    }
                }
            }
        });


        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText("loading...");

        String senderUid = notificationsList.get(position).getSenderUid();
        String title = notificationsList.get(position).getTitle();
        String isSeen = notificationsList.get(position).getIsSeen();

        String pendingProposal = notificationsList.get(position).getPendingProposalId();
        String proposalAccepted = notificationsList.get(position).getOngoingProposalId();
        String proposalCompleted = notificationsList.get(position).getCompletedProposalId();

        String type = notificationsList.get(position).getType();

        if (position >= 0){
            setNotification(senderUid, title, holder, isSeen, pendingProposal, proposalAccepted, proposalCompleted,type);
        }
    }


    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    private void setNotification(String senderUid, String title, ViewHolder holder, String isSeen, String pendingProposal, String proposalAccepted, String proposalCompleted,String type ) {

        if (senderUid != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(senderUid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);

                        if (uploadPersonforSeachIndex != null){
                            String profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                            String fullName = uploadPersonforSeachIndex.getFullname();

                            String text = null;

                            if (type == null){
                                if (pendingProposal != null) {
                                    text = fullName + " sent " + title;
                                } else if (proposalAccepted != null) {
                                    text = fullName + " Accepted " + title;
                                } else if (proposalCompleted != null) {
                                    text = fullName + " completed " + title;
                                } else {
                                    text = fullName + " sent " + title;
                                }
                            }else {
                                if (type.equals("like")){
                                    text = fullName + " liked your post ";
                                } else if (type.equals("comment")) {
                                    text = fullName + " commented on post";
                                }
                            }



                            if (!isSeen.equals("Yes")) {
                                holder.layout.setBackgroundColor(0xFFB6D8E8);
                            }

                            holder.textView.setText(text);
                            Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(holder.profileImageview);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("NotificationsRecyclerviewAdapter", "unable to load searchIndex");
                }
            });
        } else {
            Toast.makeText(holder.itemView.getContext(), "Unable to load Notification", Toast.LENGTH_SHORT).show();
            Log.d("NotificationsRecycleviewAdapter", "senderUid is null");
        }
    }

    private void setNotificationAsSeen(String notificationId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications").child(ownUid);

        Map<String, Object> updateNotificationAsSeen = new HashMap<>();
        updateNotificationAsSeen.put("isSeen", "Yes");

        databaseReference.child(notificationId).updateChildren(updateNotificationAsSeen);

    }

}
