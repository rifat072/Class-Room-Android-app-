package com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderGroupChatFragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rifat.classroom.MessageAdapter;
import com.example.rifat.classroom.Messages;
import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GroupChatActivity extends AppCompatActivity {

    private ImageButton sendMessageButton;
    private EditText userMessageInput;

    private String CurrentGroupName,CurrentGroupId, currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, GroupRef;
    private RecyclerView groupMessageList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        CurrentGroupName = getIntent().getExtras().get("groupName").toString();
        CurrentGroupId = getIntent().getExtras().get("groupId").toString();
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + CurrentGroupName + "</font>"));


        Initialize();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfotoDatabase();
                userMessageInput.setText("");

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        messagesList.clear();
        GroupRef.child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                groupMessageList.smoothScrollToPosition(groupMessageList.getAdapter().getItemCount());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveMessageInfotoDatabase() {

        String message = userMessageInput.getText().toString().trim();
        if(TextUtils.isEmpty(message)){
            Toast.makeText(GroupChatActivity.this,"Please type a message",Toast.LENGTH_SHORT).show();
        }
        else{
            String messageKey = GroupRef.child("Messages").push().getKey();
            Map messageTextBody = new HashMap();
            messageTextBody.put("message",message);
            messageTextBody.put("type","text");
            messageTextBody.put("from",currentUserId);
            GroupRef.child("Messages").child(messageKey).updateChildren(messageTextBody).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){

                    }
                    if(!task.isSuccessful()){
                        Toast.makeText(GroupChatActivity.this,"Message can't be sent",Toast.LENGTH_SHORT).show();
                    }
                }

            });

        }
    }

    private void Initialize() {

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(CurrentGroupId);

        sendMessageButton = (ImageButton)findViewById(R.id.send_message_button);
        userMessageInput = (EditText)findViewById(R.id.input_group_message);
        groupMessageList = (RecyclerView)findViewById(R.id.group_messages_list_of_users);

        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        groupMessageList.setLayoutManager(linearLayoutManager);
        groupMessageList.setAdapter(messageAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent addFriend = new Intent(GroupChatActivity.this,AddFreindGroupActivity
            .class);
            addFriend.putExtra("groupId",CurrentGroupId);
            startActivity(addFriend);


        }
        return super.onOptionsItemSelected(item);
    }

}
