package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class ProfessionalsCompleteRegistration extends AppCompatActivity {

    FirebaseUser mUser;
    String username;
    String fullname;
    String location;
    String year;
    String category;
    String services;

    String profileUrl;

    String fcmToken = null;

    String ownUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professionals_complete_registration);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        ownUid = FirebaseAuth.getInstance().getUid();

        // find fcmToken for user.

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Save the token to your app's database
                    fcmToken = task.getResult();
                } else {
                    // Handle token retrieval failure
                    Log.d("MessageActivity","failed to find fcm token");
                }
            }
        });

        Intent intent = getIntent();

        fullname = intent.getStringExtra("fullname");
        username = intent.getStringExtra("username");
        location = intent.getStringExtra("location");
        profileUrl = intent.getStringExtra("profileUrl");
        year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        LinearLayout linearLayoutcheck = findViewById(R.id.register_as_professional_check_linear_layout);
        LinearLayout registerprofessioanllayout = findViewById(R.id.register_as_professional_layout);

        ImageView imageView = findViewById(R.id.professional_complete_registration_imageview);
        Picasso.get().load(profileUrl).resize(600, 600).centerCrop().into(imageView);

        Button registerAsProfessionalButton = findViewById(R.id.register_as_professional_button_register_activity);
        Button ContinueAsRegularButton = findViewById(R.id.continue_as_regular_user_button_register_activity);

        ContinueAsRegularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fcmToken != null){

                    DatabaseReference userReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("public").child(mUser.getUid());
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = new UploadPersonforSeachIndex(fullname,username,category,profileUrl,0,0,0,ownUid,location,year,null,fcmToken);
                    userReference.setValue(uploadPersonforSeachIndex, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error != null) {
                                Toast.makeText(getApplicationContext(), "Unable to proceed.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "User Registerred Succesfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    Intent i = new Intent(ProfessionalsCompleteRegistration.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(ProfessionalsCompleteRegistration.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    Log.d("ProfessionalsCompleteRegistration","failed to find fcmToken in regular method");
                }
            }
        });

        registerAsProfessionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutcheck.setVisibility(View.GONE);
                registerprofessioanllayout.setVisibility(View.VISIBLE);
            }
        });

        Button addButton = findViewById(R.id.add_services_button);
        EditText CategoryEdittext = findViewById(R.id.category_edittext_register_activity);
        EditText serviceEditText = findViewById(R.id.add_service_edittext_register_activity);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                services = serviceEditText.getText().toString().trim();
                category = CategoryEdittext.getText().toString().trim().toLowerCase(Locale.ROOT);
                registerProfessional(fullname.toLowerCase(), username.toLowerCase(), location, year, category.toLowerCase(), services);
                Intent i = new Intent(ProfessionalsCompleteRegistration.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void registerProfessional(String fullname, String username, String location, String year, String category, String services) {

        if (fcmToken != null){

            DatabaseReference categoryReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex").child(mUser.getUid());
            UploadPersonforSeachIndex uploadPersonforSeachIndex = new UploadPersonforSeachIndex(fullname, username, category, profileUrl, 0, 0, 0,ownUid,location,year,services,fcmToken);
            categoryReference.setValue(uploadPersonforSeachIndex, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Log.d("ProfessionalsCompleteRegistration", "data saved for searching");
                }
            });
        }else {
            Toast.makeText(ProfessionalsCompleteRegistration.this, "something went wrong", Toast.LENGTH_SHORT).show();
            Log.d("ProfessionalsCompleteRegistration","failed to find fcmToken in registerProfessional method");
        }
    }

}

