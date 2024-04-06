package com.example.myapplication2;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.logging.LoggingMXBean;

public class ShowPostsAdapter extends RecyclerView.Adapter<ShowPostsAdapter.ShowPostViewHolder> {
    private final List<PostObject> postList;

    private String profileUrl = "";
    private String fullname = "";

    private String ownUid;

    private Context context;

    
    long likesCount;
    long commentCount;

    Boolean didILikePost = false;
    public ShowPostsAdapter(List<PostObject> posts,Context newContext) {
        postList = posts;
        context = newContext;
    }


    @NonNull
    @Override
    public ShowPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ownUid = FirebaseAuth.getInstance().getUid();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post_layout, parent, false);
        ShowPostViewHolder viewHolder = new ShowPostViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShowPostViewHolder holder, int position) {

        String uid = postList.get(position).getUid();

        if (uid != null) {
            fetchProfile(uid, holder);
        }


        if (postList.get(position).getImage1path() != null && postList.get(position).getImage2path() == null && postList.get(position).getImage3path() == null) {

            holder.two_image_layout.setVisibility(View.GONE);
            holder.three_image_layout.setVisibility(View.GONE);

            holder.one_imageview_post.setVisibility(View.VISIBLE);

            Picasso.get().load(postList.get(position).getImage1path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.one_imageview_post);

        }else if (postList.get(position).getImage1path() != null && postList.get(position).getImage2path() != null && postList.get(position).getImage3path() == null) {

            holder.one_imageview_post.setVisibility(View.GONE);
            holder.three_image_layout.setVisibility(View.GONE);

            holder.two_image_layout.setVisibility(View.VISIBLE);

            Picasso.get().load(postList.get(position).getImage1path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.imageview1_two_post_layout);
            Picasso.get().load(postList.get(position).getImage2path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.imageview2_two_post_layout);

        }else if (postList.get(position).getImage1path() != null && postList.get(position).getImage2path() != null && postList.get(position).getImage3path() != null) {

            holder.one_imageview_post.setVisibility(View.GONE);
            holder.two_image_layout.setVisibility(View.GONE);

            holder.three_image_layout.setVisibility(View.VISIBLE);

            Picasso.get().load(postList.get(position).getImage1path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.imageview1_three_post_layout);
            Picasso.get().load(postList.get(position).getImage2path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.imageview2_three_post_layout);
            Picasso.get().load(postList.get(position).getImage3path()).placeholder(R.drawable.placeholder).resize(1080,1080).centerCrop().into(holder.imageview3_three_post_layout);
        }

        if (postList.get(position).text != null) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(postList.get(position).text);
        }

        holder.like_image_button.setOnClickListener(v -> {
            String postOwnerUid = postList.get(position).getUid();
            checkFcmTokenIsGenerated(postList.get(position).getPostId(),postOwnerUid,holder);
        });

        holder.comment_image_button.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(), ViewCommentPostListActivity.class);
            i.putExtra("postId",postList.get(position).getPostId());
            i.putExtra("uid",uid);
            holder.itemView.getContext().startActivity(i);
        });

        holder.likesCountTextview.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(), ViewPostLikeListActivity.class);
            i.putExtra("postId",postList.get(position).getPostId());
            holder.itemView.getContext().startActivity(i);
        });

        holder.commentCountTextview.setOnClickListener(v -> {
            Intent i = new Intent(holder.itemView.getContext(),ViewCommentPostListActivity.class);
            i.putExtra("postId",postList.get(position).getPostId());
            i.putExtra("uid",uid);
            holder.itemView.getContext().startActivity(i);
        });

        // show images in full screen
        String image1Url = postList.get(position).getImage1path();
        String image2Url = postList.get(position).getImage2path();
        String image3Url = postList.get(position).getImage3path();

        holder.one_imageview_post.setOnClickListener(v -> {
            if (image1Url != null){
                Intent k = new Intent(context,FullScreenImageActivity.class);
                k.putExtra("imageUrl",image1Url);
                context.startActivity(k);
            }
        });

        holder.imageview1_two_post_layout.setOnClickListener(v -> {
            Intent k = new Intent(context,FullScreenImageActivity.class);
            k.putExtra("imageUrl",image1Url);
            context.startActivity(k);
        });

        holder.imageview2_two_post_layout.setOnClickListener(v -> {
            Intent k = new Intent(context,FullScreenImageActivity.class);
            k.putExtra("imageUrl",image2Url);
            context.startActivity(k);
        });

        holder.imageview1_three_post_layout.setOnClickListener(v -> {
            Intent k = new Intent(context,FullScreenImageActivity.class);
            k.putExtra("imageUrl",image1Url);
            context.startActivity(k);
        });

        holder.imageview2_three_post_layout.setOnClickListener(v -> {
            Intent k = new Intent(context,FullScreenImageActivity.class);
            k.putExtra("imageUrl",image2Url);
            context.startActivity(k);
        });

        holder.imageview3_three_post_layout.setOnClickListener(v -> {
            Intent k = new Intent(context,FullScreenImageActivity.class);
            k.putExtra("imageUrl",image3Url);
            context.startActivity(k);
        });

        setPostViewed(postList.get(position).getPostId());

        fetchLikeCount(postList.get(position).getPostId(),holder);
        fetchCommentCount(postList.get(position).getPostId(),holder);
        checkLikeStatus(postList.get(position).getPostId(),holder);

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ShowPostViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ImageView profileimage;
        TextView fullnameText;

        ImageView one_imageview_post;
        LinearLayout two_image_layout;

        ImageView imageview1_two_post_layout;
        ImageView imageview2_two_post_layout;
        LinearLayout three_image_layout;

        ImageView imageview1_three_post_layout;
        ImageView imageview2_three_post_layout;
        ImageView imageview3_three_post_layout;

        ImageView like_image_button;
        ImageView comment_image_button;

        TextView likesCountTextview;
        TextView commentCountTextview;
        public ShowPostViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview_view_post_layout);

            one_imageview_post = itemView.findViewById(R.id.imageview1_post_show_post_layout);
            two_image_layout = itemView.findViewById(R.id.two_image_linear_layout_view_post_layout);
            three_image_layout = itemView.findViewById(R.id.three_image_linear_layout_view_post_layout);

            imageview1_two_post_layout = itemView.findViewById(R.id.post_imageview1_2image_layout_show_post_layout);
            imageview2_two_post_layout = itemView.findViewById(R.id.post_imageview2_2image_layout_show_post_layout);

            imageview1_three_post_layout = itemView.findViewById(R.id.post_imageview1_3image_layout_show_post_layout);
            imageview2_three_post_layout = itemView.findViewById(R.id.post_imageview2_3image_layout_show_post_layout);
            imageview3_three_post_layout = itemView.findViewById(R.id.post_imageview3_3image_layout_show_post_layout);


            profileimage = itemView.findViewById(R.id.profile_imageview_viewpost_layout);
            fullnameText = itemView.findViewById(R.id.fullname_textview_viewpost_layout);

            like_image_button = itemView.findViewById(R.id.like_button_view_post_layout);
            comment_image_button = itemView.findViewById(R.id.comment_button_view_post_layout);

            likesCountTextview = itemView.findViewById(R.id.likesCountTextview_view_post_layout);
            commentCountTextview = itemView.findViewById(R.id.commentCountTextview_view_post_layout);
            
        }
    }

    private void fetchProfile(String uid, ShowPostViewHolder holder) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null) {
                        fullname = uploadPersonforSeachIndex.getFullname();
                        profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        String capitalName = UtilityMethods.capitalizeFullName(fullname);
                        holder.fullnameText.setText(capitalName);
                        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(holder.profileimage);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ShowPostAdapter", "unable to find username and photo");
            }
        });
    }

    private void likePost(String postId,String receiverUid,ShowPostViewHolder holder,String fcmToken){
        if (!didILikePost){
            holder.like_image_button.setImageResource(R.drawable.icon_like_filled);

            didILikePost = true;

            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

            database.child("postLikes").child(postId).child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        // since user has not liked post earlier

                        String timestamp = String.valueOf(System.currentTimeMillis());
                        PostLikesObject likesObject = new PostLikesObject(postId,ownUid,timestamp);

                        database.child("postLikes").child(postId).child(ownUid).setValue(likesObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                database.child("postTrend").child(postId).child("likes").runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                        Long data = currentData.getValue(Long.class);
                                        if (data != null){
                                            currentData.setValue(data + 1);
                                        }else {
                                            currentData.setValue(1);
                                        }
                                        return Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                        if (!Objects.equals(ownUid, receiverUid)){
                                            SendPostPushNotification.findOwnFullnameAndSendLikeNotification(postId,fcmToken,ownUid,receiverUid,context);
                                        }
                                        fetchLikeCount(postId,holder);
                                    }
                                });
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            // undo like post
            holder.like_image_button.setImageResource(R.drawable.icon_like_outline);

            didILikePost = false;

            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
            database.child("postLikes").child(postId).child(ownUid).removeValue();



            database.child("postTrend").child(postId).child("likes").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    Long data = currentData.getValue(Long.class);
                    if (data !=  null && data > 0){
                        currentData.setValue(data - 1);

                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    fetchLikeCount(postId,holder);

                }
            });
        }
    }

    private void setPostViewed(String postId){
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        database.child("postViewed").child(postId).child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    database.child("postViewed").child(postId).child(ownUid).setValue(ownUid).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.child("postTrend").child(postId).child("views").runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    Long data = currentData.getValue(Long.class);
                                    if (data != null){
                                        currentData.setValue(data + 1);
                                    }else {
                                        currentData.setValue(1);
                                    }
                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchLikeCount(String postId, ShowPostViewHolder holder){
        
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        database.child("postLikes").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    likesCount = snapshot.getChildrenCount();
                    holder.likesCountTextview.setText(likesCount + " like");
                }else {
                    holder.likesCountTextview.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchCommentCount(String postId, ShowPostViewHolder holder){
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        database.child("postComment").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    commentCount = snapshot.getChildrenCount();
                }
                holder.commentCountTextview.setText(commentCount + " comment");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFcmTokenIsGenerated(String postId,String receiverUid,ShowPostViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        databaseReference.child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String fcmToken = uploadPersonforSeachIndex.getFcmToken();
                        likePost(postId,receiverUid,holder,fcmToken);
                    }
                }else {
                    Toast.makeText(context, "unable to like", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkLikeStatus(String postId,ShowPostViewHolder holder){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("postLikes");
        databaseReference.child(postId).child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.like_image_button.setImageResource(R.drawable.icon_like_filled);
                    didILikePost = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
