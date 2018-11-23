package com.example.rifat.classroom;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId,SenderUserId, CurrentState;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button SendMessageRequestButton, DeclineMessageRequestButton;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,ChatRequestRef,ContactRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();

        userProfileImage = (CircleImageView) findViewById(R.id.visit_profile_image);
        userProfileName = (TextView)findViewById(R.id.visit_user_name);
        userProfileStatus = (TextView)findViewById(R.id.visit_user_status);
        SendMessageRequestButton = (Button)findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton = (Button)findViewById(R.id.decline_message_request_button);
        CurrentState = "new";
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        SenderUserId = mAuth.getCurrentUser().getUid();
        RetriveUserInfo();
    }

    private void RetriveUserInfo() {
        UserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("name")) userProfileName.setText(dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.hasChild("status")) userProfileStatus.setText(dataSnapshot.child("status").getValue().toString());
                    if(dataSnapshot.hasChild("image")) {
                        String downloadurl = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(downloadurl).into(userProfileImage);
                    }
                    ManageChatRequest();
                    
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequest() {
        ChatRequestRef.child(SenderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(receiverUserId)){
                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        CurrentState = "request_sent";
                        SendMessageRequestButton.setText("Cancel Chat Request");
                    }
                    else if (request_type.equals("received")){
                        CurrentState = "request_received";
                        SendMessageRequestButton.setText("Accept Chat Request");
                        DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                        DeclineMessageRequestButton.setEnabled(true);
                        DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelChatRequest();
                            }
                        });
                    }

                }
                else{
                    ContactRef.child(SenderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiverUserId)){
                                CurrentState = "friends";
                                SendMessageRequestButton.setText("Remove this Contact");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(!SenderUserId.equals(receiverUserId)){
            SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendMessageRequestButton.setEnabled(false);
                    if(CurrentState.equals("new")){
                        SendChatRequest();
                    }
                    else if(CurrentState.equals("request_sent")){
                        CancelChatRequest();
                    }
                    else if(CurrentState.equals("request_received")){
                        AcceptChatRequest();
                    }
                    else if(CurrentState.equals("friends")){
                        RemoveSpecificContact();
                    }
                }
            });
        }
        else{
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact() {

        ContactRef.child(SenderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactRef.child(receiverUserId).child(SenderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendMessageRequestButton.setEnabled(true);
                                                CurrentState = "new";
                                                SendMessageRequestButton.setText("Send Message");

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactRef.child(SenderUserId).child(receiverUserId).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ContactRef.child(receiverUserId).child(SenderUserId).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ChatRequestRef.child(SenderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    ChatRequestRef.child(receiverUserId).child(SenderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        SendMessageRequestButton.setEnabled(true);
                                                                                        CurrentState = "friend";
                                                                                        SendMessageRequestButton.setText("Remove this Contacts");
                                                                                        DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineMessageRequestButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {
        ChatRequestRef.child(SenderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestRef.child(receiverUserId).child(SenderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                SendMessageRequestButton.setEnabled(true);
                                                CurrentState = "new";
                                                SendMessageRequestButton.setText("Send Message");
                                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                DeclineMessageRequestButton.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {
        ChatRequestRef.child(SenderUserId).child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ChatRequestRef.child(receiverUserId).child(SenderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SendMessageRequestButton.setEnabled(true);
                            CurrentState = "request_sent";
                            SendMessageRequestButton.setText("Cancel Chat Request");
                        }
                    });
                }
            }
        });
    }
}
