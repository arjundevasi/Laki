package com.example.myapplication2;

import android.content.Intent;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UploadUserRating {
    public interface CompletionCallback{
        void onCompletion();
    }
    public static void uploadRatingAndReviewForUser(int rating,String review,String proposalId, String professionalUid,String customerUid,String ownUid,String ratedByName,String oppositeUid, CompletionCallback callback){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        RatingAndReviewObject ratings = new RatingAndReviewObject(rating, review, proposalId, professionalUid, customerUid, ownUid,ratedByName);

        // we have the rating for the other user and now we upload the same in his/her collection.
        databaseReference.child("ratingAndReview").child(oppositeUid).child(proposalId).setValue(ratings).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // we have rated the other user.

                HashMap<String, Object> updates = new HashMap<>();
                if (ownUid.equals(professionalUid)) {
                    updates.put("professionalHasRatedCustomer", "Yes");
                } else {
                    updates.put("customerHasRatedProfessional", "Yes");
                }

                databaseReference.child("rating_Manager").child(professionalUid).child(proposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        databaseReference.child("rating_Manager").child(customerUid).child(proposalId).updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // method has finished execution.
                                // now upload data for admin to calculate rating
                                uploadDataForAdminToRateIndex(oppositeUid,proposalId,ownUid,callback);
                            }
                        });
                    }
                });
            }
        });
    }

    private static void uploadDataForAdminToRateIndex(String userId, String proposalId, String uploadedBy, CompletionCallback callback){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        AdminRateIndexObject adminRateIndexObject = new AdminRateIndexObject(userId,proposalId,uploadedBy,"No");

        String path = userId + "_" + System.currentTimeMillis();
        databaseReference.child("AdminCalculateRatingIndex").child(path).setValue(adminRateIndexObject).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // now we notify user that this class has finished executing.
                callback.onCompletion();
            }
        });
    }
}
