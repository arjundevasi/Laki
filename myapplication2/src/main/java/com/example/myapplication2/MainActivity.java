package com.example.myapplication2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnCategoryCardButtonClickListener {

    private FirebaseUser mUser;
    private String ownUid;
    BottomNavigationView bottomNavigationView;

    String queryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            checkIfNewUser();
            ownUid = FirebaseAuth.getInstance().getUid();
        }

        if (mUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }


        // initialising fragments
        HomeFragment homeFragment = new HomeFragment();
        SearchFragment searchFragment = new SearchFragment();
        BidFragment bidFragment = new BidFragment();
        NotificationFragment notificationFragment = new NotificationFragment();
        ProfileFragment profileFragment = new ProfileFragment();


        bottomNavigationView = findViewById(R.id.BottomNavigation);

        // setting default fragment as home fragment
        switchFragment(homeFragment, "home",null);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Get the selected menu item ID

            // Use if-else statements to handle the selected menu items
            if (itemId == R.id.main_home) {
                switchFragment(homeFragment, "home",null);
            } else if (itemId == R.id.main_search) {
                switchFragment(searchFragment, "search",null);
            } else if (itemId == R.id.main_bid) {
                switchFragment(bidFragment, "bid",null);
            } else if (itemId == R.id.main_notification) {
                switchFragment(notificationFragment, "notification",null);
            } else if (itemId == R.id.main_profile) {
                switchFragment(profileFragment, "profile",null);
            } else {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            return true;
        });


        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());


        String uid = FirebaseAuth.getInstance().getUid();

        if (uid != null) {
            findUserDetails(uid);
        }

        refreshFcmToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void checkIfNewUser() {


        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(Objects.requireNonNull(mUser.getUid()));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("MainActivity", "checkNewUser: user exists");
                } else {
                    Intent i = new Intent(MainActivity.this, RegisterUsersActivity.class);
                    startActivity(i);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void findUserDetails(String uid) {


        new Thread(() -> {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(uid);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = null;
                        String fullName = null;
                        String category = null;
                        String profileUrl = null;

                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);

                        if (uploadPersonforSeachIndex != null) {
                            username = uploadPersonforSeachIndex.getUsername();
                            fullName = uploadPersonforSeachIndex.getFullname();
                            category = uploadPersonforSeachIndex.getCategory();
                            profileUrl = uploadPersonforSeachIndex.getProfileUrl();

                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("fullname", fullName);
                            editor.putString("category", category);
                            editor.putString("profileUrl", profileUrl);
                            editor.apply();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(HomeFragment.class.getSimpleName(), "Unable to find user details in background");
                }
            });

        }).start();
    }

    private void switchFragment(Fragment fragment, String tag,Bundle data) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment existingFragment = fragmentManager.findFragmentByTag(tag);

        if (existingFragment == null) {

            if (data != null){
                fragment.setArguments(data);
            }

            fragmentTransaction.add(R.id.frame_layout, fragment, tag);
        } else {
            if (data != null){
                existingFragment.setArguments(data);
            }
            fragmentTransaction.show(existingFragment);
        }

        List<Fragment> fragmentList = fragmentManager.getFragments();

        if (!fragmentList.isEmpty()) {
            for (Fragment frags : fragmentList) {
                if (frags != existingFragment) {
                    fragmentTransaction.hide(frags);
                }
            }
        }

        fragmentTransaction.commit();
    }
    private void refreshFcmToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Save the token to your app's database
                    String fcmToken = task.getResult();
                    updateTokenInDatabase(fcmToken);
                } else {
                    // Handle token retrieval failure
                    Log.d("MessageActivity","failed to find fcm token");
                }
            }
        });
    }

    private void updateTokenInDatabase(String fcmToken){
        if (ownUid != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
            databaseReference.child("searchIndex").child(ownUid).child("fcmToken").setValue(fcmToken);
        }
    }


    @Override
    public void onCategoryCardButtonClick(String query) {
        bottomNavigationView.setSelectedItemId(R.id.main_search);
        SearchFragment searchFragment = new SearchFragment();
        //searchFragment.setQuery(query,true);
        Bundle bundle = new Bundle();
        bundle.putString("query",query);
        switchFragment(searchFragment,"search",bundle);
    }

}