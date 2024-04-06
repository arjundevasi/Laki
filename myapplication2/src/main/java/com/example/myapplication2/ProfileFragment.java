package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    String fullname = null;
    String profileUrl = null;

    ImageView profileImageview;

    TextView fullnameTextview;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout userlayout = view.findViewById(R.id.user_rofile_profilefragment);

        profileImageview = view.findViewById(R.id.profile_imageview_profile_fragment);
        fullnameTextview = view.findViewById(R.id.fullname_textview_profile_fragment);

        TextView pendingProposalTextview = view.findViewById(R.id.pending_proposal_textview_profile_fragment);
        TextView ongoingProposalTextview = view.findViewById(R.id.ongoing_proposal_textview_profile_fragment);
        TextView completedProposalTextview = view.findViewById(R.id.completed_proposal_textview_profile_fragment);
        TextView paymentsTextview = view.findViewById(R.id.payments_textview_profile_fragment);

        pendingProposalTextview.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), PendingProposalListActivity.class);
            startActivity(i);
        });

        ongoingProposalTextview.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), OngoingProposalListActivity.class);
            startActivity(i);
        });

        completedProposalTextview.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), CompletedProposalListActivity.class);
            startActivity(i);
        });

        paymentsTextview.setOnClickListener(v -> {
            Intent i = new Intent(getContext(),PaymentsActivity.class);
            startActivity(i);
        });


        userlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MyProfileActivity.class);
                startActivity(i);
            }
        });

        String uid = FirebaseAuth.getInstance().getUid();
        findUsernameandProfileUrl(uid);
    }

    private void findUsernameandProfileUrl(String uid) {
        String sharedFullname = sharedPreferences.getString("fullname", null);
        String sharedprofileUrl = sharedPreferences.getString("profileUrl", null);

        if (sharedFullname == null || sharedprofileUrl == null) {
            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            fullname = uploadPersonforSeachIndex.getUsername();
                            profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                            Picasso.get().load(profileUrl).transform(new RoundImageTransformation()).into(profileImageview);
                            fullnameTextview.setText(fullname);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("CreatePostActivity", "unable to find username and photo");
                }
            });
        } else {
            Picasso.get().load(sharedprofileUrl).transform(new RoundImageTransformation()).into(profileImageview);
            fullnameTextview.setText(sharedFullname);
        }
    }
}