package com.example.myapplication2;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {

    private final List<SendAndRecieveMessage> messages;

    private final String oppositeUid;

    private final String ownUid;

    private final String conversationId;
    public MessageRecyclerAdapter(List<SendAndRecieveMessage> messages, String oppositeUid, String ownUid,String conversationId) {
        this.messages = messages;
        this.oppositeUid = oppositeUid;
        this.ownUid = ownUid;
        this.conversationId = conversationId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String sender = messages.get(position).getSenderId();

        String isImage = messages.get(position).getIsImage();

        String imageUri = messages.get(position).getImageUrl();

        Boolean isSeen = messages.get(position).getSeenBoolean();


        if (isImage.equals("Yes")){
            if (sender.equals(ownUid)) {
                //set visibilty
                holder.oppositeLinearLayout.setVisibility(View.GONE);

                holder.ownLinearLayout.setVisibility(View.VISIBLE);

                //
                holder.ownTextView.setVisibility(View.GONE);

                holder.cardViewOwnChatImageView.setVisibility(View.VISIBLE);
                holder.ownChatImageview.setVisibility(View.VISIBLE);

                // since we are displaying own image we make oppositeChatImageview invisible
                holder.oppositeChatImageview.setVisibility(View.GONE);
                holder.cardViewOppositeChatImageView.setVisibility(View.GONE);

                if (imageUri != null){
                    Picasso.get().load(imageUri).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.ownChatImageview);
                }

                if (isSeen){
                    holder.ownMessageSeenImageview.setImageResource(R.drawable.icon_message_seen);
                }else {
                    holder.ownMessageSeenImageview.setImageResource(R.drawable.icon_message_delivered);
                }


            } else {
                // set visibilty
                holder.oppositeLinearLayout.setVisibility(View.VISIBLE);

                holder.ownLinearLayout.setVisibility(View.GONE);


                holder.oppositeTextview.setVisibility(View.GONE);

                holder.cardViewOppositeChatImageView.setVisibility(View.VISIBLE);
                holder.oppositeChatImageview.setVisibility(View.VISIBLE);

                // since we are displaying opposite image we make ownchatimageview invisible
                holder.ownChatImageview.setVisibility(View.GONE);

                if (imageUri != null){
                    Picasso.get().load(imageUri).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.oppositeChatImageview);
                }

            }
        }else {
            if (sender.equals(ownUid)) {
                //set visibilty
                holder.oppositeLinearLayout.setVisibility(View.GONE);
                holder.ownLinearLayout.setVisibility(View.VISIBLE);

                holder.ownChatImageview.setVisibility(View.GONE);
                holder.cardViewOwnChatImageView.setVisibility(View.GONE);

                holder.cardViewOppositeChatImageView.setVisibility(View.GONE);
                holder.oppositeChatImageview.setVisibility(View.GONE);

                holder.ownTextView.setText(messages.get(position).getMessageText());

                if (isSeen){
                    holder.ownMessageSeenImageview.setImageResource(R.drawable.icon_message_seen);
                }else {
                    holder.ownMessageSeenImageview.setImageResource(R.drawable.icon_message_delivered);
                }

            } else {
                // set visibilty
                holder.oppositeLinearLayout.setVisibility(View.VISIBLE);
                holder.ownLinearLayout.setVisibility(View.GONE);

                holder.ownChatImageview.setVisibility(View.GONE);
                holder.cardViewOwnChatImageView.setVisibility(View.GONE);

                holder.cardViewOppositeChatImageView.setVisibility(View.GONE);
                holder.oppositeChatImageview.setVisibility(View.GONE);

                holder.oppositeTextview.setText(messages.get(position).getMessageText());

            }
        }

        // we make messages seen if we are receiving it and checking it it has not marked as seen earlier.
            if (!isSeen && sender.equals(oppositeUid)){
                setIsSeenToYes(messages.get(position).getMessageId());
            }


        holder.ownChatImageview.setOnClickListener(v -> {
                Intent k = new Intent(holder.itemView.getContext(),FullScreenImageActivity.class);
                k.putExtra("imageUrl",imageUri);
                holder.itemView.getContext().startActivity(k);

        });

        holder.oppositeChatImageview.setOnClickListener(v -> {
            Intent k = new Intent(holder.itemView.getContext(),FullScreenImageActivity.class);
            k.putExtra("imageUrl",imageUri );
            holder.itemView.getContext().startActivity(k);
        });


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends NotificationsRecycleviewAdapter.ViewHolder {

        private final TextView oppositeTextview;
        private final TextView ownTextView;
        private final ImageView ownMessageSeenImageview;

        private final LinearLayout oppositeLinearLayout;

        private final LinearLayout ownLinearLayout;

        private final ImageView ownChatImageview;
        private final ImageView oppositeChatImageview;

        private final CardView cardViewOwnChatImageView;
        private final CardView cardViewOppositeChatImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ownMessageSeenImageview = itemView.findViewById(R.id.message_layout_own_message_seen_profile_picture_imageview_recycler);
            oppositeTextview = itemView.findViewById(R.id.message_layout_opposite_textview_recycler);
            ownTextView = itemView.findViewById(R.id.message_layout_own_textview_recycler);

            ownChatImageview = itemView.findViewById(R.id.message_layout_own_chat_ImageView);
            oppositeChatImageview = itemView.findViewById(R.id.message_layout_opposite_chat_ImageView);

            oppositeLinearLayout = itemView.findViewById(R.id.opposite_linear_layout_message_layout);
            ownLinearLayout = itemView.findViewById(R.id.own_linear_layout_message_layout);

            cardViewOwnChatImageView = itemView.findViewById(R.id.cardview_message_layout_own_chat_ImageView);
            cardViewOppositeChatImageView = itemView.findViewById(R.id.cardview_message_layout_oppsoite_chat_ImageView);
        }
    }

    private void setIsSeenToYes(String messageId){
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference chatReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chats");

        if (conversationId != null && messageId != null){
            chatReference.child(conversationId).child(messageId).child("seenTimestamp").setValue(timestamp);
            chatReference.child(conversationId).child(messageId).child("seenBoolean").setValue(true);
        }
    }
}
