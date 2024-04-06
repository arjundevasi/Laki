package com.example.myapplication2;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {

    RecyclerView notificationsRecyclerView;

    ProgressBar progressBar;
    String ownUid;

    List<NotificationsObject> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsRecyclerView = view.findViewById(R.id.recyler_view);
        progressBar = view.findViewById(R.id.progress_bar_notification_fragment);

        ownUid = FirebaseAuth.getInstance().getUid();


        if (notificationList == null) {
            fetchNotifications();
        } else {
            loadNotifications(notificationList);
        }


    }

    private void fetchNotifications() {
        notificationList = new ArrayList<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications").child(ownUid);

        databaseReference.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        NotificationsObject notificationsObject = dataSnapshot.getValue(NotificationsObject.class);

                        if (notificationsObject != null){
                            notificationList.add(notificationsObject);
                        }
                    }
                    Collections.reverse(notificationList);

                    loadNotifications(notificationList);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Unable to load notifications", Toast.LENGTH_SHORT).show();
                Log.d("NotificationFragment", "error loading notification from database");
            }
        });
    }

    private void loadNotifications(List<NotificationsObject> notificationList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        NotificationsRecycleviewAdapter adapter = new NotificationsRecycleviewAdapter(notificationList);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationsRecyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        notificationsRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (notificationList != null && !notificationList.isEmpty()) {
            loadNotifications(notificationList);
        }
    }
}