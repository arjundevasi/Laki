package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    FirebaseUser mUser;

    FirebaseAuth mAuth;
    LinearLayout mVerifyNumberLayout;
    LinearLayout mVerifySMSLayout;

    EditText mMobileNumberEdittext;
    EditText mSmsEdittext;

    Button mMobileNumberButton;
    Button mVerifySMSButton;

    String mVerificationId;

    String phonenumber;

    private DatabaseReference mDatabase;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mVerifyNumberLayout = findViewById(R.id.verify_mobile_number_layout);
        mVerifySMSLayout = findViewById(R.id.verify_sms_layout);

        mMobileNumberEdittext = findViewById(R.id.mobile_number_edittext);
        mSmsEdittext = findViewById(R.id.sms_code_edittext);

        mMobileNumberButton = findViewById(R.id.mobile_number_button);
        mVerifySMSButton = findViewById(R.id.verify_sms_button);

        mDatabase = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                mVerificationId = s;
                mVerifyNumberLayout.setVisibility(View.GONE);
                mVerifySMSLayout.setVisibility(View.VISIBLE);
                mSmsEdittext.requestFocus();
            }
        };

        mMobileNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = mMobileNumberEdittext.getText().toString().trim();
                if (number.length() != 10) {
                    Toast.makeText(LoginActivity.this, "Please enter correct Mobile number", Toast.LENGTH_SHORT).show();
                    mMobileNumberEdittext.setText("");
                } else {
                    String fullnumber = "+91" + number;
                    phonenumber = "91" + number;
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                    .setPhoneNumber(fullnumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LoginActivity.this)                 // (optional) Activity for callback binding
                                    // If no activity is passed, reCAPTCHA verification can not be used.
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        mVerifySMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = mSmsEdittext.getText().toString().trim();
                if (code.length() != 0) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mUser = task.getResult().getUser();
                    Toast.makeText(LoginActivity.this, "Verification successful", Toast.LENGTH_SHORT).show();
                    checkIfNewUser();
                } else {
                    Toast.makeText(LoginActivity.this, "Verification failed, try agin later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mUser == null) {
            finishAffinity();
        }
    }

    private void checkIfNewUser() {


        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(Objects.requireNonNull(mAuth.getUid()));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(LoginActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    refreshFcmToken();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                } else {
                    refreshFcmToken();
                    Intent i = new Intent(LoginActivity.this, RegisterUsersActivity.class);
                    startActivity(i);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    private void refreshFcmToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Save the token to your app's database
                    String fcmToken = task.getResult();
                    String ownUid = FirebaseAuth.getInstance().getUid();
                    if (ownUid != null){
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
                        databaseReference.child("public").child(ownUid).child("fcmToken").setValue(fcmToken);
                    }
                } else {
                    // Handle token retrieval failure
                    Log.d("MessageActivity","failed to find fcm token");
                }
            }
        });
    }
}