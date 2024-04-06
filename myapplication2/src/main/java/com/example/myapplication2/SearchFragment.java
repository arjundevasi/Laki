package com.example.myapplication2;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment {

    SearchView searchView;
    String searchQuery;

    RecyclerView recyclerView;

    SearchResultAdapter nameAdapter;

    SearchResultAdapter categoryAdapter;

    ProgressBar progressBar;

    String ownUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        searchView = view.findViewById(R.id.searchview_search_fragment);

        ownUid = FirebaseAuth.getInstance().getUid();

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        progressBar = view.findViewById(R.id.search_fragment_progessbar);

        if (getArguments() != null){
            String query = getArguments().getString("query");
            if (query != null){
               query = query.toLowerCase(Locale.ROOT);
               performSearch(query);
               searchView.setQuery(query,false);
            }
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query.trim().toLowerCase(Locale.ROOT);
                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                performSearch(searchQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    //recyclerView.setVisibility(View.GONE);

                    if (nameAdapter != null) {
                        nameAdapter.clearData();
                    }

                    if (categoryAdapter != null) {
                        categoryAdapter.clearData();
                    }
                }

                return false;
            }
        });
    }

    private void performSearch(String query) {

        progressBar.setVisibility(View.VISIBLE);

        if (nameAdapter != null){
            nameAdapter.clearData();
        }

        if (categoryAdapter != null) {
            categoryAdapter.clearData();
        }

        searchForCategory(query);
    }

    private void searchForName(String query) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        Query search = reference.orderByChild("fullname").startAt(query).endAt(query + "\uf8ff");
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<UploadPersonforSeachIndex> resultList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = dataSnapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null){
                            String uid = uploadPersonforSeachIndex.getUid();
                            if (!uid.equals(ownUid)){
                                resultList.add(uploadPersonforSeachIndex);
                            }
                        }

                    }

                    if (resultList.size() < 1){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
                    }else {
                        nameAdapter = new SearchResultAdapter(resultList);
                        recyclerView.setAdapter(nameAdapter);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to search" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchForCategory(String query) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        Query search = reference.orderByChild("category").startAt(query).endAt(query + "\uf8ff");
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<UploadPersonforSeachIndex> resultList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = dataSnapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null){
                            String uid = uploadPersonforSeachIndex.getUid();
                            if (!uid.equals(ownUid)){
                                resultList.add(uploadPersonforSeachIndex);
                            }
                        }

                    }

                    if (resultList.size() < 1){
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "No User Found", Toast.LENGTH_SHORT).show();
                    }else {
                        categoryAdapter = new SearchResultAdapter(resultList);
                        recyclerView.setAdapter(categoryAdapter);
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }


                } else {
                    searchForName(query);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to search" + error, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            if (getArguments() != null){
                String query = getArguments().getString("query");
                if (query != null){
                    query = query.toLowerCase(Locale.ROOT);
                    performSearch(query);
                    searchView.setQuery(query,false);
                }
            }
        }
    }
}