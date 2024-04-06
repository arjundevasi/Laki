package com.example.myapplication2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity {

    private EditText editTextMessage;

    private String receiverUid;

    private String ownUid;
    private String conversationId = null;

    private DatabaseReference chatIndexReference;

    private DatabaseReference chatReference;

    private List<SendAndRecieveMessage> messages;

    private RecyclerView recyclerView;

    private String fcmToken = null;

    private boolean loadMessageCalled = false;

    private String receiverUidFromNotification = null;

    private TextView oppositeFullNameTextview;
    private ImageView oppositeProfileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        oppositeFullNameTextview = findViewById(R.id.opposite_fullname_textview_message_activity);
        oppositeProfileImageView = findViewById(R.id.opposite_pofile_imageview_message_activity);

        LinearLayout userInfoLayout = findViewById(R.id.username_image_layout_message_activity);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ownUid = mAuth.getUid();

        messages = new ArrayList<>();

        chatReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chats");
        chatIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chatIndex");

        Intent i = getIntent();
        if (i != null) {
            receiverUid = i.getStringExtra("uid");

            // check if fcm token is generated or not.
            if (receiverUid != null){
                checkFcmTokenIsGenerated(receiverUid);
                findConversationIdAndLoadMessages(receiverUid);
            }
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            receiverUidFromNotification = extras.getString("senderUid");
            if (receiverUidFromNotification != null){
                checkFcmTokenIsGenerated(receiverUidFromNotification);
                findConversationIdAndLoadMessages(receiverUidFromNotification);
                receiverUid = receiverUidFromNotification;
                
            }
            findOppositeProfileInfo(receiverUid);
        }

        ImageView gallery = findViewById(R.id.gallery_message_activity);
        ImageView sendButton = findViewById(R.id.send_button_message_activity);
        recyclerView = findViewById(R.id.message_activity_recycler_view);

        editTextMessage = findViewById(R.id.edittext_message_activity);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findConversationId();
            }
        });


        ActivityResultLauncher<Intent> gallerylauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri uri = data.getData();
                            Intent intent = new Intent(this, SendImageInChatActivity.class);
                            intent.putExtra("imageUri",uri.toString());
                            intent.putExtra("ownUid",ownUid);
                            intent.putExtra("receiverUid",receiverUid);
                            intent.putExtra("fcmToken",fcmToken);
                            intent.putExtra("conversationId",conversationId);
                            startActivity(intent);
                        }

                    }
                });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                gallerylauncher.launch(Intent.createChooser(i, "Select Image"));
            }
        });

        Toolbar toolbar = findViewById(R.id.message_activity_toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userInfoLayout.setOnClickListener(v -> {
            Intent j = new Intent(this,MyProfileActivity.class);
            j.putExtra("uid",receiverUid);
            startActivity(j);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findConversationId() {


        chatIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chatIndex");

        chatIndexReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.hasChild(receiverUid)) {
                            conversationId = dataSnapshot.getKey();
                            break;
                        }
                    }
                }
                sendMessage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("MessageActivity", "failed to serach for conversationId");
            }
        });


    }

    private void sendMessage() {

        String messageText = editTextMessage.getText().toString().trim();

        if (!messageText.isEmpty()){
            String timestamp = String.valueOf(System.currentTimeMillis());



            if (conversationId == null) {

                DatabaseReference pushReference = chatReference.push();
                DatabaseReference messageReference = pushReference.push();

                SendAndRecieveMessage sendAndRecieveMessage = new SendAndRecieveMessage(messageText, timestamp, ownUid, receiverUid, "", false,"No",null,messageReference.getKey());

                messageReference.setValue(sendAndRecieveMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String newConversationId = pushReference.getKey();
                        if (fcmToken == null){
                            Toast.makeText(MessageActivity.this, "message failed", Toast.LENGTH_SHORT).show();
                            Log.d("MessageActivity","fcm token is null for " + receiverUid);
                        }else {
                            ChatIndexObject chatIndexObject = new ChatIndexObject(newConversationId,ownUid,receiverUid);
                            if (newConversationId != null){
                                chatIndexReference.child(ownUid).child(newConversationId).setValue(chatIndexObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        chatIndexReference.child(receiverUid).child(newConversationId).setValue(chatIndexObject).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                conversationId = pushReference.getKey();
                                                editTextMessage.setText("");
                                                setChildListener(newConversationId);
                                                findOwnFullNameAndSendNotification(ownUid,messageText);
                                            }
                                        });
                                    }
                                });
                            }else {
                                Toast.makeText(MessageActivity.this, "message failed", Toast.LENGTH_SHORT).show();
                                Log.d("MessageActivity","failed to create newConversationId");
                            }

                        }
                    }
                });


            } else {
                DatabaseReference messageReference = chatReference.child(conversationId).push();
                SendAndRecieveMessage sendAndRecieveMessage = new SendAndRecieveMessage(messageText, timestamp, ownUid, receiverUid, "", false,"No",null,messageReference.getKey());

                messageReference.setValue(sendAndRecieveMessage);
                editTextMessage.setText("");
                findOwnFullNameAndSendNotification(ownUid,messageText);
            }
        }else{
            Toast.makeText(this, "empty message", Toast.LENGTH_SHORT).show();
        }

    }

    private void findConversationIdAndLoadMessages(String localReceiverUid) {

        chatIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chatIndex");

        chatIndexReference.child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatIndexObject chatIndexObject = dataSnapshot.getValue(ChatIndexObject.class);
                        if (chatIndexObject != null){
                            if (chatIndexObject.getUser1().equals(localReceiverUid)){
                                conversationId = chatIndexObject.getConversationId();
                                setChildListener(conversationId);
                                break;
                            } else if (chatIndexObject.getUser2().equals(localReceiverUid)) {
                                conversationId = chatIndexObject.getConversationId();
                                setChildListener(conversationId);
                                break;
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("MessageActivity", "failed to serach for conversationId");
            }
        });

    }


    private void loadMessages(List<SendAndRecieveMessage> messages) {


        if (receiverUid == null && receiverUidFromNotification != null){
            receiverUid = receiverUidFromNotification;
        }
        
        MessageRecyclerAdapter adapter = new MessageRecyclerAdapter(messages, receiverUid, ownUid,conversationId);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        loadMessageCalled = true;

    }

    private void setChildListener(String conversationId) {
        chatReference.child(conversationId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                SendAndRecieveMessage sendAndRecieveMessage = snapshot.getValue(SendAndRecieveMessage.class);
                if (sendAndRecieveMessage != null) {
                    messages.add(sendAndRecieveMessage);
                    if (!loadMessageCalled){
                        loadMessages(messages);
                    }else {
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemInserted(messages.size() - 1);
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFcmTokenIsGenerated(String receiverUid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        databaseReference.child(receiverUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        fcmToken = uploadPersonforSeachIndex.getFcmToken();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void findOwnFullNameAndSendNotification(String uid, String messageText){
        // first we find user name of own

        SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String sharedFullname = sharedPreferences.getString("fullname", null);

        if (sharedFullname == null){
            DatabaseReference searchReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
            searchReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null){
                            String ownFullName = uploadPersonforSeachIndex.getFullname();

                            // now we send notification
                            if (fcmToken != null){
                                try {
                                    SendPushNotification.sendNewMessageNotification(ownFullName,uid,messageText,fcmToken,MessageActivity.this,null);
                                } catch (JSONException e) {
                                    Log.d("MessageActivity","error sending notification. error is: " + e);
                                }
                            }else {
                                checkFcmTokenIsGenerated(receiverUid);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else {
            if (fcmToken != null){
                try {
                    SendPushNotification.sendNewMessageNotification(sharedFullname,uid,messageText,fcmToken,MessageActivity.this,null);
                } catch (JSONException e) {
                    Log.d("MessageActivity","error sending notification. error is: " + e);
                }
            }else {
                checkFcmTokenIsGenerated(receiverUid);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        finish();
        startActivity(intent);
    }

    private void findOppositeProfileInfo(String oppositeUid) {
        DatabaseReference publicReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        publicReference.child(oppositeUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String oppositeProfileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        String oppositeFullName = uploadPersonforSeachIndex.getFullname();

                        String capitalName = UtilityMethods.capitalizeFullName(oppositeFullName);
                        oppositeFullNameTextview.setText(capitalName);
                        Picasso.get().load(oppositeProfileUrl).transform(new RoundImageTransformation()).centerCrop().resize(1024,1024).into(oppositeProfileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MessageActivity", "Unable to fetch opposite profile url");
            }
        });
    }
}