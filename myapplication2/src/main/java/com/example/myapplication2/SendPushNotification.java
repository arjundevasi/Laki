package com.example.myapplication2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendPushNotification {

    private static final String TAG = "SendPushNotification";
    private static RequestQueue requestQueue;
    public static void sendNewMessageNotification(String fullname, String senderUid, String messageText, String fcmToken, Context context,String isImage) throws JSONException {


        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        // we set notification data
        if (isImage != null){
            body.put("type","image");
        }else {
            body.put("type","message");
        }
        body.put("fullname",fullname);
        body.put("senderUid",senderUid);
        body.put("messageText",messageText);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    public static void sendNewProposalNotification(String fullname, String proposalId, String title, String fcmToken, Context context,String receiverUid,String senderUid) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","newProposal");
        body.put("fullname",fullname);
        body.put("proposalId",proposalId);
        body.put("title",title);
        body.put("senderUid", senderUid);
        body.put("receiverUid", receiverUid);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    public static void sendCounterProposalNotification(String fullname, String proposalId, String title, String fcmToken, Context context,String receiverUid,String senderUid) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","counterProposal");
        body.put("fullname",fullname);
        body.put("proposalId",proposalId);
        body.put("title",title);
        body.put("senderUid", senderUid);
        body.put("receiverUid", receiverUid);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    public static void sendAcceptedProposalNotification(String fullname, String proposalId, String title, String fcmToken, Context context) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","acceptedProposal");
        body.put("fullname",fullname);
        body.put("proposalId",proposalId);
        body.put("title",title);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    public static void sendCompletedProposalNotification(String fullname, String proposalId, String title, String fcmToken, Context context) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","completedProposal");
        body.put("fullname",fullname);
        body.put("proposalId",proposalId);
        body.put("title",title);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }


    public static void sendLikeNotification(String fullname, String postId, String fcmToken, Context context) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","like");
        body.put("fullname",fullname);
        body.put("postId",postId);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    public static void sendCommentNotification(String fullname, String postId, String fcmToken, Context context) throws JSONException {
        JSONObject notification = new JSONObject();
        JSONObject body = new JSONObject();

        body.put("type","comment");
        body.put("fullname",fullname);
        body.put("postId",postId);

        notification.put("to",fcmToken);
        notification.put("data",body);

        sendNotification(notification,context);
    }

    private static void sendNotification(JSONObject notification, Context context){

        requestQueue = Volley.newRequestQueue(context);

        String url = "https://fcm.googleapis.com/fcm/send";
        String serverKey = "key=" + "AAAAhJwCDJQ:APA91bF1YnR8EYFMDEEmlY2bsqJaPU4Pm2ZUTQicpN95Ei-6TWGUTGP6LGNzh_ecxlCqhN9PLTNbStvK_2Ymvaz3iAb8z4CyMXRw00ODrzNtxE2Tcou5ZE91RxN6lIpqQ_EEYeaAwJzD";
        String contentType = "application/json";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,notification, response -> {

            Log.d(TAG,"notification sent");
        }, error -> {
            Log.d(TAG,"error in jsonObjectRequest. error is : " + error);
        }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        requestQueue.add(request);
    }
}
