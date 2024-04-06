package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

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

public class PostDetailsActivity extends AppCompatActivity {

    TextView postTextview;

    ImageView postImageview;

    LinearLayout twoImageLayout;

    ImageView imageView1_2post_layout;
    ImageView imageView2_2post_layout;

    LinearLayout three_image_layout;

    ImageView imageView1_3post_layout;
    ImageView imageView2_3post_layout;
    ImageView imageView3_3post_layout;

    ImageView likeImageView;
    ImageView commentImageview;

    TextView likeTextview;
    TextView commentTextview;

    String postId = null;

    ImageView profileImageView;
    TextView fullNameTextView;

    String ownUid;

    long likes;
    long comment;

    String postOwnerUid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        ownUid = FirebaseAuth.getInstance().getUid();


        postTextview = findViewById(R.id.textview_post_details_activity);
        postImageview = findViewById(R.id.imageview1_post_details_activity);

        twoImageLayout = findViewById(R.id.two_image_linear_layout_post_details_activity);
        imageView1_2post_layout = findViewById(R.id.post_imageview1_2image_layout_post_details_activity);
        imageView2_2post_layout = findViewById(R.id.post_imageview2_2image_layout_post_details_activity);

        three_image_layout = findViewById(R.id.three_image_linear_layout_post_details_activity);
        imageView1_3post_layout = findViewById(R.id.post_imageview1_3image_layout_post_details_activity);
        imageView2_3post_layout = findViewById(R.id.post_imageview2_3image_layout_post_details_activity);
        imageView3_3post_layout = findViewById(R.id.post_imageview3_3image_layout_post_details_activity);

        likeImageView = findViewById(R.id.like_button_post_details_activity);
        commentImageview  = findViewById(R.id.comment_button_post_details_activity);

        likeTextview = findViewById(R.id.likesCountTextview_post_details_activity);
        commentTextview = findViewById(R.id.commentCountTextview_post_details_activity);


        profileImageView = findViewById(R.id.profile_imageview_post_details_activity);
        fullNameTextView  = findViewById(R.id.fullname_textview_post_details_activity);



        Intent i = getIntent();

        if (i != null){
            postId = i.getStringExtra("postId");

            fetchPost(postId);
            fetchLikeAndCommentCount(postId);

        }else {
            Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        likeTextview.setOnClickListener(v -> {
            if (postId != null){
                Intent k = new Intent(PostDetailsActivity.this, ViewPostLikeListActivity.class);
                k.putExtra("postId",postId);
                startActivity(k);

            }else {
                Toast.makeText(this, "failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        commentTextview.setOnClickListener(v -> {
            if (postId != null){
                if (postOwnerUid != null){
                    Intent j = new Intent(PostDetailsActivity.this,ViewCommentPostListActivity.class);
                    j.putExtra("postId",postId);
                    j.putExtra("uid",postOwnerUid);
                    startActivity(j);
                }
            }else {
                Toast.makeText(this, "failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        likeImageView.setOnClickListener(v -> {
            if (postId != null){
                likePost(postId);
            }
        });

        commentImageview.setOnClickListener(v -> {
            if (postOwnerUid != null){
                Intent j = new Intent(PostDetailsActivity.this,ViewCommentPostListActivity.class);
                j.putExtra("postId",postId);
                j.putExtra("uid",postOwnerUid);
                startActivity(j);
            }

        });
    }

    private void fetchPost(String postId){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        databaseReference.child("posts").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    PostObject posts = snapshot.getValue(PostObject.class);
                    if (posts != null){
                        loadPost(posts);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PostDetailsActivity","failed to load post. error is : " + error);
            }
        });
    }

    private void loadPost(PostObject posts){

        postOwnerUid = posts.getUid();

        fetchProfile(postOwnerUid);

        if (posts.getImage1path() != null && posts.getImage2path() == null && posts.getImage3path() == null) {

            twoImageLayout.setVisibility(View.GONE);
            three_image_layout.setVisibility(View.GONE);

            Picasso.get().load(posts.getImage1path()).resize(1080,1080).centerCrop().into(postImageview);

        }else if (posts.getImage1path() != null && posts.getImage2path() != null && posts.getImage3path() == null) {

            postImageview.setVisibility(View.GONE);
            three_image_layout.setVisibility(View.GONE);

            twoImageLayout.setVisibility(View.VISIBLE);

            Picasso.get().load(posts.getImage1path()).resize(1080,1080).centerCrop().into(imageView1_2post_layout);
            Picasso.get().load(posts.getImage2path()).resize(1080,1080).centerCrop().into(imageView2_2post_layout);

        }else if (posts.getImage1path() != null && posts.getImage2path() != null && posts.getImage3path() != null) {

            postImageview.setVisibility(View.GONE);
            twoImageLayout.setVisibility(View.GONE);

            three_image_layout.setVisibility(View.VISIBLE);

            Picasso.get().load(posts.getImage1path()).resize(1080,1080).centerCrop().into(imageView1_3post_layout);
            Picasso.get().load(posts.getImage2path()).resize(1080,1080).centerCrop().into(imageView2_3post_layout);
            Picasso.get().load(posts.getImage3path()).resize(1080,1080).centerCrop().into(imageView3_3post_layout);
        }

        if (posts.text != null) {
            postTextview.setVisibility(View.VISIBLE);
            postTextview.setText(posts.text);
        }
    }

    private void fetchProfile(String uid) {
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null) {
                        String fullname = uploadPersonforSeachIndex.getFullname();
                        String profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        fullNameTextView.setText(fullname);
                        Picasso.get().load(profileUrl).resize(1080, 1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ShowPostAdapter", "unable to find username and photo");
            }
        });
    }

    private void fetchLikeAndCommentCount(String postId){
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        database.child("postLikes").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    likes = snapshot.getChildrenCount();
                }

                database.child("postComment").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            comment = snapshot.getChildrenCount();
                        }
                        likeTextview.setText(likes + " like");
                        commentTextview.setText(comment + " comment");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void likePost(String postId){
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
}