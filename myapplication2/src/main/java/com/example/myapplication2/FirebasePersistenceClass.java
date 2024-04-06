package com.example.myapplication2;


import com.google.firebase.database.FirebaseDatabase;

public class FirebasePersistenceClass extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
