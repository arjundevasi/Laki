package com.example.myapplication2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AllMessageActivity extends AppCompatActivity {

    String ownUid;
    List<AllMessageListObject> allMessageListObjectList;

    ProgressBar progressBar;

    TextView noDataTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_message);

        ownUid = FirebaseAuth.getInstance().getUid();

        allMessageListObjectList = new ArrayList<>();

        progressBar = findViewById(R.id.progress_bar_all_message_activity);
        noDataTextview = findViewById(R.id.no_data_textview_all_message_activity);

        progressBar.setVisibility(View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar_all_message_activity);
        toolbar.setTitle("Messages");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        fetchMessages();
    }

    private void fetchMessages(){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        reference.child("chatIndex").child(ownUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int totalChats = (int) snapshot.getChildrenCount();
                    int currentChat = 0;

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        ChatIndexObject chatIndexObject = dataSnapshot.getValue(ChatIndexObject.class);
                        if (chatIndexObject != null){
                            String user1 = chatIndexObject.getUser1();
                            String user2 = chatIndexObject.getUser2();

                            String conversationId = chatIndexObject.getConversationId();

                            if (++currentChat == totalChats) {
                                // This is the last chat
                                if (!user1.equals(ownUid)){
                                    findFullNameAndProfile(user1,conversationId,true);
                                }else {
                                    findFullNameAndProfile(user2,conversationId,true);
                                }
                            }else {
                                // this is not last chat
                                if (!user1.equals(ownUid)){
                                    findFullNameAndProfile(user1,conversationId,false);
                                }else {
                                    findFullNameAndProfile(user2,conversationId,false);
                                }
                            }

                        }
                    }
                }else {
                    noDataTextview.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void findFullNameAndProfile(String uid,String conversationId,Boolean isLastItem){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        reference.child("searchIndex").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    UploadPersonforSeachIndex uploadPersonforSeachIndex = snapshot.getValue(UploadPersonforSeachIndex.class);
                    if (uploadPersonforSeachIndex != null){
                        String fullName = uploadPersonforSeachIndex.getFullname();
                        String profileUrl = uploadPersonforSeachIndex.getProfileUrl();
                        findLastMessage(conversationId,uid,fullName,profileUrl,isLastItem);
                    }

                }else {
                    noDataTextview.setText("something went wrong");
                    noDataTextview.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findLastMessage(String conversationId,String oppositeUid,String oppositeFullName, String oppositeProfileUrl,Boolean isLastItem){
        DatabaseReference reference = FirebaseDatabase.getInstance("https://testprojet-855c5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        reference.child("chats").child(conversationId).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        SendAndRecieveMessage sendAndRecieveMessage = dataSnapshot.getValue(SendAndRecieveMessage.class);
                        if (sendAndRecieveMessage != null){
                            String message = sendAndRecieveMessage.getMessageText();
                            String timestamp = sendAndRecieveMessage.getTimestamp();
                            Boolean isSeen = sendAndRecieveMessage.getSeenBoolean();
                            AllMessageListObject allMessageListObject = new AllMessageListObject(oppositeFullName,oppositeProfileUrl,oppositeUid,conversationId,message,timestamp,isSeen);
                            allMessageListObjectList.add(allMessageListObject);

                            if (isLastItem){
                                setupViews(allMessageListObjectList);
                            }
                        }
                    }
                }else {
                    noDataTextview.setText("something went wrong");
                    noDataTextview.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupViews(List<AllMessageListObject> allMessageListObjectList){

        progressBar.setVisibility(View.GONE);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RecyclerView view = findViewById(R.id.recyler_view_allmessages);
        AllMessageAdapter adapter = new AllMessageAdapter(allMessageListObjectList);
        view.setLayoutManager(layoutManager);
        view.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}