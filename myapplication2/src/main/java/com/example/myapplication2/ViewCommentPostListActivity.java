package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewCommentPostListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    String ownUid;

    EditText commentEditText;
    ImageView sendButton;
    ProgressBar progressBar;

    String ownFullname;
    String fcmToken;

    String receiverUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comment_post_list);

        ownUid = FirebaseAuth.getInstance().getUid();

        recyclerView = findViewById(R.id.recycler_view_view_post_comment_list_activity);

        commentEditText = findViewById(R.id.edittext_view_post_comment_list_activity);
        sendButton = findViewById(R.id.send_comment_button_view_post_comment_list_activity);
        progressBar = findViewById(R.id.progressbar_view_post_comment_list_activity);

        Intent i = getIntent();
        String postId = null;
        if (i != null){
            postId = i.getStringExtra("postId");
            receiverUid = i.getStringExtra("uid");
            checkFcmTokenIsGenerated(receiverUid);
        }

        if (postId != null){
            fetchCommentPersonList(postId);
        }

        String finalPostId = postId;
        sendButton.setOnClickListener(v -> {
            if (finalPostId != null){
                String comment = commentEditText.getText().toString().trim();

                if (!comment.equals("")){
                    commentOnPost(finalPostId,comment);
                }else {
                    Toast.makeText(this, "comment cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void fetchCommentPersonList(String postId){
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        List<PostCommentObject> postCommentObjectsList = new ArrayList<>();

        database.child("postComment").child(postId).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()){
                        PostCommentObject postCommentObject = snapshot.getValue(PostCommentObject.class);
                        if (postCommentObject != null){
                            postCommentObjectsList.add(postCommentObject);
                        }

                    setupViews(postCommentObjectsList);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("postComment").child(postId).orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void setupViews(List<PostCommentObject> commentList){
        Collections.reverse(commentList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);

        CommentListAdapter adapter = new CommentListAdapter(commentList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void commentOnPost(String postId, String comment){
        sendButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        PostCommentObject postCommentObject = new PostCommentObject(postId,ownUid,comment,timestamp);

        String commentPath = ownUid + timestamp;

        database.child("postComment").child(postId).child(commentPath).setValue(postCommentObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                database.child("postTrend").child(postId).child("comments").runTransaction(new Transaction.Handler() {
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
                        progressBar.setVisibility(View.GONE);
                        sendButton.setVisibility(View.VISIBLE);

                        commentEditText.setText("");

                        if (!ownUid.equals(receiverUid)){
                            SendPostPushNotification.findOwnFullnameAndSendCommentNotification(postId,fcmToken,ownUid,receiverUid,getApplicationContext());
                        }
                    }
                });
            }
        });
    }
    private void checkFcmTokenIsGenerated(String receiverUid){
        fcmToken = null;
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