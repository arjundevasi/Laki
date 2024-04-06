package com.example.myapplication2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class RegisterUsersActivity extends AppCompatActivity {

    FirebaseUser mUser;

    EditText mFullNameEditText;
    EditText mPhoneNumberEditText;
    EditText mUsernameEditText;
    EditText mLocationEditText;

    Button mSubmitButton;

    LinearLayout profileImageLayout;
    ImageView profileImageView;


    Boolean isUniqueUsername;

    ActivityResultLauncher<Intent> profileImageLauncher;

    String profileUrl = "";

    Bitmap profileBitmap;

    ProgressBar progressBar;


    String fullName;
    String phoneNumber;
    String username;
    String location;

    String isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_users);

        isUniqueUsername = true;

        mUser = FirebaseAuth.getInstance().getCurrentUser();


        profileImageLayout = findViewById(R.id.register_user_profile_image_layout);
        profileImageView = findViewById(R.id.register_user_profile_imageview);

        mFullNameEditText = findViewById(R.id.fullname_edittext_register_activity);
        mPhoneNumberEditText = findViewById(R.id.phone_number_edittext_register_activity);
        mUsernameEditText = findViewById(R.id.username_edittext_register_activity);
        mLocationEditText = findViewById(R.id.city_edittext_register_activity);

        mSubmitButton = findViewById(R.id.submit_button_register_activity);

        progressBar = findViewById(R.id.register_user_progress_bar);


        Intent i = getIntent();
        if (i != null){
            isEditing = i.getStringExtra("isEditing");
            if (isEditing != null){
                setupActivityForProfileEdit();
            }
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // we check if user is new or updating its data
                if (isEditing == null || isEditing.isEmpty()){
                    validateAndSubmitData();
                }else {
                    if (profileBitmap != null){
                        updateProfileImage(profileBitmap);
                    }else {
                        updateUserData(null);
                    }
                }
            }
        });

        profileImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                profileImageLauncher.launch(intent);
            }
        });


        profileImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            try {
                                profileBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                           //delete comment after checking if the picasso code works.
                            // profileImageView.setImageBitmap(profileBitmap);
                            Picasso.get().load(uri).resize(1080,1080).centerCrop().transform(new RoundImageTransformation()).into(profileImageView);
                        }
                    } else {
                        Toast.makeText(this, "Unable to take Image", Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void validateAndSubmitData() {
        isUniqueUsername = true;

        fullName = mFullNameEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
        phoneNumber = mPhoneNumberEditText.getText().toString().trim();
        username = mUsernameEditText.getText().toString().trim();
        location = mLocationEditText.getText().toString().trim();


        if (fullName.isEmpty()) {
            Toast.makeText(this, "please provide full name", Toast.LENGTH_SHORT).show();
        } else if (phoneNumber.length() != 10) {
            Toast.makeText(this, "please provide phone number", Toast.LENGTH_SHORT).show();
        } else if (username.isEmpty()) {
            Toast.makeText(this, "please provide username", Toast.LENGTH_SHORT).show();
        } else if (username.contains(" ") || username.contains("#") || username.contains(".") || username.contains("$") || username.contains("[") || username.contains("]")) {
            Toast.makeText(getApplicationContext(), "characters are not allowed in username", Toast.LENGTH_SHORT).show();
        }else if (location.isEmpty()) {
            Toast.makeText(this, "please provide location", Toast.LENGTH_SHORT).show();
        }  else {

            progressBar.setVisibility(View.VISIBLE);
            mSubmitButton.setVisibility(View.GONE);

            DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("usernames").child(username);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        isUniqueUsername = false;
                    }
                    if (!isUniqueUsername) {
                        Toast.makeText(getApplicationContext(), "Choose another username", Toast.LENGTH_SHORT).show();
                    } else {
                        saveUserName(fullName, phoneNumber, username, location);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void saveUserName(String fullName, String phoneNumber, String username,String location) {
        if (isUniqueUsername && !fullName.isEmpty() && !phoneNumber.isEmpty() && !username.isEmpty()  && !location.isEmpty()) {

            DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("usernames").child(username);
            reference.setValue(mUser.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("RegisterUser", "Username set");
                        uploadProfileImage(profileBitmap);
                    } else {
                        Toast.makeText(getApplicationContext(), "username not set", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void uploadProfileImage(Bitmap bitmap) {

        File tempFile = new File(getCacheDir(), "temp_file");
        if (bitmap != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "ProfileImage" + timestamp + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("profile_images").child(mUser.getUid()).child(filename);

        // compressing image

        UploadTask uploadTask = reference.putFile(Uri.fromFile(tempFile));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profileUrl = uri.toString();
                        saveUserData(fullName, phoneNumber, username, location, profileUrl);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterUsersActivity.this, "Unable to upload profile image", Toast.LENGTH_SHORT).show();
                        Log.d("Register user activity upload profile image", "Error uploadung image");
                    }
                });
            }
        });

    }

    private void saveUserData(String fullName, String phoneNumber, String username, String location, String profileUrl) {
        DatabaseReference userReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(mUser.getUid());
        if (profileUrl != null) {
            RegisterUser registerUser = new RegisterUser(fullName,phoneNumber,username,location,profileUrl);
            userReference.setValue(registerUser, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Toast.makeText(getApplicationContext(), "Unable to proceed.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(RegisterUsersActivity.this, ProfessionalsCompleteRegistration.class);
                        i.putExtra("fullname", fullName);
                        i.putExtra("location", location);
                        i.putExtra("username", username);
                        i.putExtra("profileUrl", profileUrl);
                        startActivity(i);
                    }
                }
            });
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            Log.d("RegisterUser", "Error in registering user beacuse profile url is null");
        }
    }

    private void setupActivityForProfileEdit(){
        TextView usernameTextview = findViewById(R.id.username_textview_register_user_activity);

        EditText usernameEditTextForUpdate = findViewById(R.id.username_edittext_register_activity);

        usernameTextview.setVisibility(View.GONE);
        usernameEditTextForUpdate.setVisibility(View.GONE);

    }

    private void updateUserData(String profileUrl){
        String fullname = mFullNameEditText.getText().toString().trim().toLowerCase(Locale.ROOT);
        String phoneNumber = mPhoneNumberEditText.getText().toString().trim();
        String location = mLocationEditText.getText().toString().trim();

        DatabaseReference userReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(mUser.getUid());

        HashMap<String,Object> update = new HashMap<>();

        if (!fullname.isEmpty()){
            update.put("fullName",fullname);
        } else if (phoneNumber.length() == 10) {
            update.put("phoneNumber",phoneNumber);
        } else if (!location.isEmpty()){
            update.put("location",location);
        }else {
            if (profileUrl != null){
                update.put("profileUrl",profileUrl);
                // we update the profile url in public and userIndex
                HashMap<String,Object> searchIndexUpdate = new HashMap<>();
                searchIndexUpdate.put("profileUrl",profileUrl);
                searchIndexUpdate.put("fullname",fullname);

                HashMap<String,Object> publicUpdate = new HashMap<>();
                publicUpdate.put("fullName",fullname);
                publicUpdate.put("profileUrl",profileUrl);



                DatabaseReference publicReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("public").child(mUser.getUid());
                DatabaseReference userIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex").child(mUser.getUid());

                publicReference.updateChildren(publicUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        userIndexReference.updateChildren(searchIndexUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (!update.isEmpty()){
                                    userReference.updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegisterUsersActivity.this, "profile updated", Toast.LENGTH_SHORT).show();

                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("fullname", fullName);
                                            editor.putString("profileUrl", profileUrl);
                                            editor.apply();

                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterUsersActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            Log.d("RegisterUserActivity","failed to update profile. error is " + e);
                                        }
                                    });
                                }else {
                                    Toast.makeText(RegisterUsersActivity.this, "nothing to update", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });

            }else {

                progressBar.setVisibility(View.VISIBLE);
                mSubmitButton.setVisibility(View.GONE);
                // we update the profile url in public and userIndex
                HashMap<String,Object> searchIndexUpdate = new HashMap<>();
                searchIndexUpdate.put("fullname",fullname);

                HashMap<String,Object> publicUpdate = new HashMap<>();
                publicUpdate.put("fullName",fullname);



                DatabaseReference publicReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("public").child(mUser.getUid());
                DatabaseReference userIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex").child(mUser.getUid());

                publicReference.updateChildren(publicUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        userIndexReference.updateChildren(searchIndexUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (!update.isEmpty()){
                                    userReference.updateChildren(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegisterUsersActivity.this, "profile updated", Toast.LENGTH_SHORT).show();

                                            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("fullname", fullName);
                                            editor.apply();

                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterUsersActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                            Log.d("RegisterUserActivity","failed to update profile. error is " + e);
                                        }
                                    });
                                }else {
                                    Toast.makeText(RegisterUsersActivity.this, "nothing to update", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void updateProfileImage(Bitmap bitmap) {

        File tempFile = new File(getCacheDir(), "temp_file");
        if (bitmap != null) {
            try {
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
                outputStream.flush();
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = "ProfileImage" + timestamp + ".jpg";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("profile_images").child(mUser.getUid()).child(filename);

        // compressing image

        UploadTask uploadTask = reference.putFile(Uri.fromFile(tempFile));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profileUrl = uri.toString();
                        updateUserData(profileUrl);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterUsersActivity.this, "Unable to upload profile image", Toast.LENGTH_SHORT).show();
                        Log.d("Register user activity upload profile image", "Error uploadung image");
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}