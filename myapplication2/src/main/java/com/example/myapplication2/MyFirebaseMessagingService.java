package com.example.myapplication2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    private int requestcode = 1;

    private final Map<String, Integer> userNotificationIds = new HashMap<>();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String type = remoteMessage.getData().get("type");

        if (type != null){
            if (type.equals("message")){
                String fullname = remoteMessage.getData().get("fullname");
                String uid = remoteMessage.getData().get("senderUid");
                String messageText = remoteMessage.getData().get("messageText");
                sendMessageNotification(fullname,uid,messageText,null);
            } else if (type.equals("newProposal")) {
                // new proposal
                String fullname = remoteMessage.getData().get("fullname");
                String proposalId = remoteMessage.getData().get("proposalId");
                String title = remoteMessage.getData().get("title");
                String receiverUid = remoteMessage.getData().get("receiverUid");
                String senderUid = remoteMessage.getData().get("senderUid");
                sendNewProposalNotification(fullname,proposalId,title,receiverUid,senderUid);

            } else if (type.equals("counterProposal")) {
                // counter proposal
                String fullname = remoteMessage.getData().get("fullname");
                String proposalId = remoteMessage.getData().get("proposalId");
                String title = remoteMessage.getData().get("title");
                String receiverUid = remoteMessage.getData().get("receiverUid");
                String senderUid = remoteMessage.getData().get("senderUid");
                sendCounterProposalNotification(fullname,proposalId,title,receiverUid,senderUid);

            } else if (type.equals("acceptedProposal")) {
                // accepted proposal
                String fullname = remoteMessage.getData().get("fullname");
                String proposalId = remoteMessage.getData().get("proposalId");
                String title = remoteMessage.getData().get("title");
                sendAcceptedProposalNotification(fullname,proposalId,title);

            } else if (type.equals("completedProposal")) {
                // completed proposal
                String fullname = remoteMessage.getData().get("fullname");
                String proposalId = remoteMessage.getData().get("proposalId");
                String title = remoteMessage.getData().get("title");
                sendCompletedProposalNotification(fullname,proposalId,title);

            } else if (type.equals("image")) {
                // image message
                String fullname = remoteMessage.getData().get("fullname");
                String uid = remoteMessage.getData().get("senderUid");
                sendMessageNotification(fullname,uid,null,"Yes");

            } else if (type.equals("like")) {

                String fullname = remoteMessage.getData().get("fullname");
                String postId = remoteMessage.getData().get("postId");
                sendLikeNotification(fullname,postId);

            } else if (type.equals("comment")) {

                String fullname = remoteMessage.getData().get("fullname");
                String postId = remoteMessage.getData().get("postId");
                sendCommentNotification(fullname,postId);

            } else {
                Log.d(TAG,"error the type is not recognised. type is " + type);
            }
        }

    }

    private void sendMessageNotification(String fullname, String uid, String messageText,String isImage) {

        Integer notificationId = userNotificationIds.get(uid);

        if (notificationId == null){
            notificationId = randomID();
            userNotificationIds.put(uid,notificationId);
        }


        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("senderUid",uid);
        intent.putExtra(uid,notificationId);
        intent.setAction(uid);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder;
        if (isImage != null){
            notificationBuilder =
                    new NotificationCompat.Builder(this, "1") // Replace with your notification channel ID
                            .setSmallIcon(R.drawable.ic_star_filled)
                            .setContentTitle(fullname)
                            .setContentText("sent a photo")
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSound(defaultSoundUri);
        }else {
             notificationBuilder =
                    new NotificationCompat.Builder(this, "1") // Replace with your notification channel ID
                            .setSmallIcon(R.drawable.ic_star_filled)
                            .setContentTitle(fullname)
                            .setContentText(messageText)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setSound(defaultSoundUri);
        }



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "1",
                    "Chat Message",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, notificationBuilder.build());

    }

    private void sendNewProposalNotification(String fullname, String proposalId, String title,String receiverUid,String senderUid) {

        Intent intent = new Intent(this, ViewPendingProposalActivity.class);
        intent.putExtra("senderUid", senderUid);
        intent.putExtra("receiverUid", receiverUid);
        intent.putExtra("proposalId", proposalId);
        intent.setAction(proposalId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "2")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullname + " sent new Proposal")
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "2",
                    "Proposal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());

    }

    private void sendCounterProposalNotification(String fullname, String proposalId, String title,String receiverUid,String senderUid) {

        Intent intent = new Intent(this, ViewPendingProposalActivity.class);
        intent.putExtra("senderUid", senderUid);
        intent.putExtra("receiverUid", receiverUid);
        intent.putExtra("proposalId", proposalId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        intent.setAction(proposalId + timestamp);
        intent.setAction(proposalId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "2")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullname + " sent reply to Proposal")
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "2",
                    "Proposal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());

    }

    private void sendAcceptedProposalNotification(String fullname, String proposalId, String title) {

        Intent intent = new Intent(this, ViewAcceptedProposalActivity.class);
        intent.putExtra("ongoing_proposal_id",proposalId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        intent.setAction(proposalId + timestamp);
        intent.setAction(proposalId);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "2")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullname + " accepted Proposal")
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "2",
                    "Proposal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());

    }

    private void sendCompletedProposalNotification(String fullname, String proposalId, String title) {

        Intent intent = new Intent(this, ViewCompletedProposalActivity.class);
        intent.putExtra("completedProposalId",proposalId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        intent.setAction(proposalId + timestamp);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "2")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullname + " completed Proposal")
                        .setContentText(title)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "2",
                    "Proposal",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());

    }

    private void sendLikeNotification(String fullName,String postId){
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("postId",postId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        intent.setAction(postId + timestamp);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "3")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullName)
                        .setContentText("liked a post")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "3",
                    "Likes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());
    }

    private void sendCommentNotification(String fullName, String postId){
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra("postId",postId);
        String timestamp = String.valueOf(System.currentTimeMillis());
        intent.setAction(postId + timestamp);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestcode++, intent,
                PendingIntent.FLAG_MUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "4")
                        .setSmallIcon(R.drawable.ic_star_filled)
                        .setContentTitle(fullName)
                        .setContentText("commented on post")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel for Android 8.0+
            NotificationChannel channel = new NotificationChannel(
                    "4",
                    "Comments",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(randomID(), notificationBuilder.build());
    }
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        String ownUid = FirebaseAuth.getInstance().getUid();
        if (ownUid != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
            databaseReference.child("searchIndex").child(ownUid).child("fcmToken").setValue(token);
        }

    }

    private int randomID(){
        Random random = new Random();

        int notificationId = random.nextInt();

        notificationId = Math.abs(notificationId);
        return notificationId;
    }

}