package com.example.myapplication2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AllMessageAdapter extends RecyclerView.Adapter<AllMessageAdapter.ViewHolder> {

    List<AllMessageListObject> allMessageListObjectList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImageView;
        TextView fullNameTextView;
        TextView lastMessageTextview;
        TextView timeTextview;

        LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullNameTextView = itemView.findViewById(R.id.fullname_textview_all_message_layout);
            profileImageView = itemView.findViewById(R.id.profile_imageview_all_message_layout);
            lastMessageTextview = itemView.findViewById(R.id.message_text_textview_all_message_layout);
            timeTextview = itemView.findViewById(R.id.timestamp_textview_all_message_recycler_layout);
            layout = itemView.findViewById(R.id.linear_layout_all_message_recycler_layout);
        }
    }

    public AllMessageAdapter(List<AllMessageListObject> allMessageListList) {
        allMessageListObjectList = allMessageListList;
    }

    @NonNull
    @Override
    public AllMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_message_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMessageAdapter.ViewHolder holder, int position) {
        String fullName = allMessageListObjectList.get(position).getFullName();
        String profileUrl = allMessageListObjectList.get(position).getProfileUrl();
        String lastMessage = allMessageListObjectList.get(position).getLastMessage();
        String timestamp = allMessageListObjectList.get(position).getTimestamp();

        Boolean isSeen = allMessageListObjectList.get(position).getSeenBoolean();

        Picasso.get().load(profileUrl).transform(new RoundImageTransformation()).resize(1024,1024).centerCrop().into(holder.profileImageView);

        String capitalFullName = UtilityMethods.capitalizeFullName(fullName);
        holder.fullNameTextView.setText(capitalFullName);

        holder.lastMessageTextview.setText(lastMessage);

        if (!isSeen){
            holder.lastMessageTextview.setTextColor(Color.BLACK);
            holder.lastMessageTextview.setTypeface(holder.lastMessageTextview.getTypeface(), Typeface.BOLD);
        }
        String timeElapsed = UtilityMethods.findTimeElapsedFromTimestamp(timestamp);

        holder.timeTextview.setText(timeElapsed);

        holder.layout.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(),MessageActivity.class);
            i.putExtra("uid",allMessageListObjectList.get(position).getOppositeUid());
            holder.itemView.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return allMessageListObjectList.size();
    }
}
