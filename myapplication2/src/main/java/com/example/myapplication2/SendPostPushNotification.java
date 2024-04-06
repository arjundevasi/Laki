package com.example.myapplication2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;

public class SendPostPushNotification {
    public static void findOwnFullnameAndSendLikeNotification(String postId,String fcmToken,String ownUid, String receiverUid,Context context) {

        final String[] ownFullname = new String[1];
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String sharedFullname = sharedPreferences.getString("fullname", null);



        if (sharedFullname == null) {
            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(ownUid);

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            ownFullname[0] = uploadPersonforSeachIndex.getFullname();
                            if (fcmToken != null){
                                try {
                                    SendPushNotification.sendLikeNotification(ownFullname[0],postId,fcmToken,context);
                                    sendInAppNotification(postId,receiverUid,ownUid,"like");
                                } catch (JSONException e) {
                                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                                }
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("CreatePostActivity", "unable to find username and photo");
                }
            });
        } else {
            // we have full name in shared preferences.
            ownFullname[0] = sharedFullname;
            if (fcmToken != null){
                try {
                    SendPushNotification.sendLikeNotification(ownFullname[0],postId,fcmToken,context);
                    sendInAppNotification(postId,receiverUid,ownUid,"like");
                } catch (JSONException e) {
                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                }
            }
        }
    }

    public static void findOwnFullnameAndSendCommentNotification(String postId,String fcmToken,String ownUid,String receiverUid,Context context) {

        final String[] ownFullname = new String[1];

        SharedPreferences sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
        String sharedFullname = sharedPreferences.getString("fullname", null);



        if (sharedFullname == null) {
            DatabaseReference database = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("searchIndex").child(ownUid);

            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                        if (uploadPersonforSeachIndex != null) {
                            ownFullname[0] = uploadPersonforSeachIndex.getFullname();
                            if (fcmToken != null){
                                try {
                                    SendPushNotification.sendCommentNotification(ownFullname[0],postId,fcmToken,context);
                                    sendInAppNotification(postId,receiverUid,ownUid,"comment");
                                } catch (JSONException e) {
                                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                                }
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("CreatePostActivity", "unable to find username and photo");
                }
            });
        } else {
            // we have full name in shared preferences.
            ownFullname[0] = sharedFullname;
            if (fcmToken != null){
                try {
                    SendPushNotification.sendCommentNotification(ownFullname[0],postId,fcmToken,context);
                    sendInAppNotification(postId,receiverUid,ownUid,"comment");
                } catch (JSONException e) {
                    Log.d("ViewAcceptedProposalActivity","error sending notification in completed proposal. error is: " + e);
                }
            }
        }
    }

    private static void sendInAppNotification(String postId, String receiverUid, String ownUid,String type){
        DatabaseReference notificationsReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("notifications");
        String timestamp = String.valueOf(System.currentTimeMillis());
        String notificationId = type + "_" + ownUid + "_" + timestamp;

        NotificationsObject notificationsObject = new NotificationsObject(ownUid,receiverUid,null,null,timestamp,"No",notificationId,null,null,null,null,type,postId);
        notificationsReference.child(receiverUid).child(notificationId).setValue(notificationsObject);
    }
}
