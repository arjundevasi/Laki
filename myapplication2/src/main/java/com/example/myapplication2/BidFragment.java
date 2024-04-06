package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BidFragment extends Fragment {

    RecyclerView recyclerView;

    ProgressBar progressBar;

    List<PostObject> postsList;

    int adapterLastPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview_bid_fragment);

        progressBar = view.findViewById(R.id.progress_bar_bid_fragment);
        FloatingActionButton fab = view.findViewById(R.id.fab_bid_fragment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), CreatePostActivity.class);
                startActivity(i);
            }
        });

        if (postsList == null) {
            fetchPosts();
        } else {
            showPosts(postsList);
        }
    }

    private void fetchPosts() {
        postsList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PostObject posts = dataSnapshot.getValue(PostObject.class);
                        postsList.add(posts);
                    }
                    Collections.reverse(postsList);
                    showPosts(postsList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to load posts", Toast.LENGTH_SHORT).show();
                Log.d("BidFragment", "failed to fetch posts");
            }
        });
    }

    private void showPosts(List<PostObject> postList) {
        ShowPostsAdapter postsAdapter = new ShowPostsAdapter(postList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postsAdapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (postsList != null && !postsList.isEmpty()) {
            showPosts(postsList);
        }

        if (adapterLastPosition >= 0){
            LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
            if (layoutManager != null){
                layoutManager.scrollToPositionWithOffset(adapterLastPosition,0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        if (layoutManager != null){
            adapterLastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }
    }


}