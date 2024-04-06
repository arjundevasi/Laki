package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewPostLikeListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_like_list);

        recyclerView = findViewById(R.id.recycler_view_view_post_like_list_activity);

        Intent i = getIntent();
        String postId = null;
        if (i != null){
            postId = i.getStringExtra("postId");
        }

        if (postId != null){
            fetchLikePersonListAndShowList(postId);
        }else {
            Toast.makeText(this, "failed to load data", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchLikePersonListAndShowList(String postId){
        DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        List<PostLikesObject> postLikesObjectsList = new ArrayList<>();
        database.child("postLikes").child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PostLikesObject postLikesObject = dataSnapshot.getValue(PostLikesObject.class);
                        postLikesObjectsList.add(postLikesObject);
                    }
                    setupViews(postLikesObjectsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupViews(List<PostLikesObject> likeList){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);

        LikesListAdapter adapter = new LikesListAdapter(likeList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}