package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SendImageInChatActivity extends AppCompatActivity {

    private FloatingActionButton sendButton;
    private ImageView imageView;
    private ImageButton cancelButton;

    private String conversationId;

    private String ownUid;

    private String receiverUid;

    private String fcmToken;

    private Bitmap imageBitmap;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image_in_chat);

        sendButton = findViewById(R.id.send_fab_send_image_in_chat_activity);
        imageView = findViewById(R.id.imageView_send_image_in_chat_activity);
        cancelButton = findViewById(R.id.cancel_button_send_image_in_chat_activit);
        progressBar = findViewById(R.id.progress_bar_send_image_in_chat_activity);

        progressBar.setVisibility(View.GONE);
        sendButton.setVisibility(View.VISIBLE);

        Intent i = getIntent();

        if (i != null){
            String imageUri = i.getStringExtra("imageUri");
            conversationId = i.getStringExtra("conversationId");
            ownUid = i.getStringExtra("ownUid");
            receiverUid = i.getStringExtra("receiverUid");
            fcmToken = i.getStringExtra("fcmToken");

            if (imageUri == null || ownUid == null || receiverUid == null || fcmToken == null){
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("SendImageInChatActivity","null values are passed in intent");
                finish();
            }

            imageView.setImageURI(Uri.parse(imageUri));


            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(imageUri));
            } catch (IOException e) {
                Log.d("SendImageInChatActivity","error converting uri to bitmap. error is: " + e );
            }
        }else {
            finish();
            Log.d("SendImageInChatActivity","no intent received");
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                // upload image and send user message.
                File tempFile = new File(getCacheDir(), "temp_file");
                if (imageBitmap != null) {
                    try {
                        FileOutputStream outputStream = new FileOutputStream(tempFile);
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream);
                        outputStream.flush();
                        outputStream.close();

                    } catch (IOException e) {
                       Log.d("SendImageInChatActivity","error in compressing image. error is: " + e);
                    }
                }

                String timestamp = String.valueOf(System.currentTimeMillis());
                String filename = "Img_" + timestamp + ".jpg";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("chatImages").child(ownUid).child(filename);

                UploadTask uploadTask = reference.putFile(Uri.fromFile(tempFile));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                sendMessage(imageUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendImageInChatActivity.this, "failed to send image", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendImageInChatActivity.this, "failed to send image", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendMessage(String imageUrl){

        DatabaseReference chatReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chats");

        DatabaseReference chatIndexReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("chatIndex");

        String timestamp = String.valueOf(System.currentTimeMillis());



        if (conversationId == null) {
            DatabaseReference pushReference = chatReference.push();
            DatabaseReference messageReference = pushReference.push();
            SendAndRecieveMessage sendAndRecieveMessage = new SendAndRecieveMessage(null, timestamp, ownUid, receiverUid, "", false,"Yes",imageUrl,messageReference.getKey());
            messageReference.setValue(sendAndRecieveMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    String newConversationId = pushReference.getKey();
                    if (fcmToken == null){
                        Toast.makeText(SendImageInChatActivity.this, "message failed", Toast.LENGTH_SHORT).show();
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
                                            findOwnFullnameAndSendNotification(ownUid);
                                        }
                                    });
                                }
                            });
                        }else {
                            Toast.makeText(SendImageInChatActivity.this, "message failed", Toast.LENGTH_SHORT).show();
                            Log.d("MessageActivity","failed to create newConversationId");
                        }

                    }
                }
            });


        } else {
            DatabaseReference messageReference = chatReference.child(conversationId).push();
            SendAndRecieveMessage sendAndRecieveMessage = new SendAndRecieveMessage(null, timestamp, ownUid, receiverUid, "", false,"Yes",imageUrl,messageReference.getKey());
            messageReference.setValue(sendAndRecieveMessage);
            findOwnFullnameAndSendNotification(ownUid);
        }
    }

    private void findOwnFullnameAndSendNotification(String uid){
        // first we find user name

        DatabaseReference searchReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("searchIndex");
        searchReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String fullname = uploadPersonforSeachIndex.getFullname();

                        // now we send notification
                        if (fcmToken != null){
                            try {
                                SendPushNotification.sendNewMessageNotification(fullname,uid,null,fcmToken,SendImageInChatActivity.this,"yes");

                                // we have succesfully uploaded image and sent user.
                                // now we go back to messageActivity
                                finish();

                            } catch (JSONException e) {
                                Log.d("MessageActivity","error sending notification. error is: " + e);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}