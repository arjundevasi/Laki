package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;

    RecyclerView.LayoutManager layoutManager;

    ProgressBar progressBar;

    LinearLayout linearLayout;

    private OnCategoryCardButtonClickListener categoryCardButtonClickListener;


    public interface OnCategoryCardButtonClickListener {
        void onCategoryCardButtonClick(String query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.toolbar_messages);


        recyclerView = view.findViewById(R.id.categories_recycler_view);
        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        progressBar = view.findViewById(R.id.home_fragment_progessbar);
        linearLayout = view.findViewById(R.id.home_fragment_linear_layout);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(),AllMessageActivity.class);
                startActivity(i);
            }
        });

        setRecyclerView();

    }

    private void setRecyclerView() {

        List<CategoryObject> categories = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("category");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryText = categorySnapshot.getKey();
                    String url = categorySnapshot.child("imageUri").getValue(String.class);
                    CategoryObject categoryObject = new CategoryObject(categoryText, url);
                    categories.add(categoryObject);
                }

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                CategoryRecyclerView adapter = new CategoryRecyclerView(categories, categoryCardButtonClickListener);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            categoryCardButtonClickListener = (OnCategoryCardButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnButtonClickListener");
        }
    }
}