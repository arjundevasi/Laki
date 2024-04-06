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

public class LikesListAdapter extends RecyclerView.Adapter<LikesListAdapter.LikeListViewHolder> {

    private final List<PostLikesObject> likeList;

    String fullname;
    String profileUrl;

    public LikesListAdapter (List<PostLikesObject> postLikesObjectList){
        this.likeList = postLikesObjectList;
    }

    @NonNull
    @Override
    public LikeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list_layout,parent,false);
        LikeListViewHolder listViewHolder = new LikeListViewHolder(v);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeListViewHolder holder, int position) {
        fetchProfile(likeList.get(position).getLikedby(),holder);
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public static class LikeListViewHolder extends RecyclerView.ViewHolder{
        ImageView profileImageview;
        TextView fullnameTextview;
        public LikeListViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImageview = itemView.findViewById(R.id.profile_imageview_like_list_layout);
            fullnameTextview = itemView.findViewById(R.id.fullname_like_list_layout);
        }
    }
    private void fetchProfile(String uid, LikeListViewHolder holder) {
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
