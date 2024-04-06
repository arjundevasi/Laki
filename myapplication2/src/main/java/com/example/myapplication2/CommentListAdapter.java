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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder> {
    private final List<PostCommentObject> commentList;

    String fullname;
    String profileUrl;


    public CommentListAdapter(List<PostCommentObject> postCommentObjects){
        this.commentList = postCommentObjects;
    }

    @NonNull
    @Override
    public CommentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_adapter_layout,parent,false);

        return new CommentListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListViewHolder holder, int position) {
        String comment = commentList.get(position).getComment();

        fetchProfile(commentList.get(position).getCommentBy(),comment,holder);

        String timestamp = commentList.get(position).getTimestamp();

        String timeAgo = UtilityMethods.findTimeElapsedFromTimestamp(timestamp);

        holder.dateTextview.setText(timeAgo);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentListViewHolder extends RecyclerView.ViewHolder{

        ImageView profileImageview;
        TextView fullnameTextview;

        TextView commentTextview;

        TextView dateTextview;
        public CommentListViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageview = itemView.findViewById(R.id.profile_imageview_comments_adapter_layout);
            fullnameTextview = itemView.findViewById(R.id.fullname_comments_adapter_layout);
            commentTextview = itemView.findViewById(R.id.comment_textview_comment_adapter_layout);

            dateTextview = itemView.findViewById(R.id.date_textview_comment_adapter_layout);

        }
    }
    private void fetchProfile(String uid, String comment, CommentListViewHolder holder) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null) {
                        fullname = uploadPersonforSeachIndex.getFullname();
                        profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        holder.fullnameTextview.setText(fullname);
                        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(holder.profileImageview);
                        holder.commentTextview.setText(comment);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("LikeListAdapter", "unable to find username and photo");
            }
        });

        holder.profileImageview.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(),MyProfileActivity.class);
            i.putExtra("uid",uid);
            holder.itemView.getContext().startActivity(i);
        });

        holder.fullnameTextview.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(),MyProfileActivity.class);
            i.putExtra("uid",uid);
            holder.itemView.getContext().startActivity(i);
        });
    }


}
