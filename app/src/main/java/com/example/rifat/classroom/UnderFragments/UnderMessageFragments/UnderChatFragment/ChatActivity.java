package com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderChatFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.rifat.classroom.MessageAdapter;
import com.example.rifat.classroom.Messages;
import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage;
    private TextView UserName,UserLastSeen;
    private CircleImageView UserImage;
    private ImageButton SendMessageButton,SendFileButton;
    private EditText SendMessageText;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private StorageReference mStorageRef;
    private ProgressDialog loadingbar;
    private static final int PICK_FILE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_user_image").toString();
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + messageReceiverName + "</font>"));



        RootRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("File");

        SendMessageButton = (ImageButton)findViewById(R.id.send_message_btn);
        SendFileButton = (ImageButton)findViewById(R.id.send_file_btn);
        SendMessageText = (EditText)findViewById(R.id.send_message_txt);

        messageAdapter = new MessageAdapter(messagesList,ChatActivity.this);
        userMessagesList = (RecyclerView)findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        loadingbar = new ProgressDialog(this);

        SendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendFile();
            }
        });
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessge();
            }
        });

    }

    private void SendFile() {
        Intent gallaryIntent = new Intent();
        gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallaryIntent.setType("*/*");
        startActivityForResult(gallaryIntent,PICK_FILE);
    }


    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FILE && resultCode == RESULT_OK){
            loadingbar.setTitle("Uploading File");
            loadingbar.setMessage("Please wait...");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();;
            Uri uri = data.getData();
            final String name = getFileName(uri) ;
            Log.e("FileName", name);
            StorageReference storageRef = mStorageRef.child(CurrentUserID + name );
            final UploadTask uploadTask = storageRef.putFile(uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadurl = taskSnapshot.getDownloadUrl().toString();
                    String message = name;
                    String messageText = SendMessageText.getText().toString().trim();

                    String messageSenderRef = "Messages/" + CurrentUserID + "/" + messageReceiverID;
                    String messageReceiverRef = "Messages/" + messageReceiverID + "/" + CurrentUserID;
                    DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(CurrentUserID)
                            .child(messageReceiverID).push();
                    String messagepushId = userMessageKeyRef.getKey();

                    Map messageTextBody = new HashMap();
                    messageTextBody.put("message",name);
                    messageTextBody.put("type","file");
                    messageTextBody.put("from",CurrentUserID);
                    messageTextBody.put("link",downloadurl);

                    Map messageBodyDetail = new HashMap();
                    messageBodyDetail.put(messageSenderRef + "/" + messagepushId,messageTextBody);
                    messageBodyDetail.put(messageReceiverRef + "/" + messagepushId,messageTextBody);
                    RootRef.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                loadingbar.dismiss();
                                Toast.makeText(ChatActivity.this,"Uploaded",Toast.LENGTH_SHORT).show();

                            }
                            else{
                                loadingbar.dismiss();
                                Toast.makeText(ChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                            SendMessageText.setText("");

                        }
                    });


                    }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingbar.dismiss();
                }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(CurrentUserID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
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

    private void SendMessge() {
        String messageText = SendMessageText.getText().toString().trim();
        if(TextUtils.isEmpty(messageText))return;
        String messageSenderRef = "Messages/" + CurrentUserID + "/" + messageReceiverID;
        String messageReceiverRef = "Messages/" + messageReceiverID + "/" + CurrentUserID;
        DatabaseReference userMessageKeyRef = RootRef.child("Messages").child(CurrentUserID)
                .child(messageReceiverID).push();
        String messagepushId = userMessageKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("message",messageText);
        messageTextBody.put("type","text");
        messageTextBody.put("from",CurrentUserID);

        Map messageBodyDetail = new HashMap();
        messageBodyDetail.put(messageSenderRef + "/" + messagepushId,messageTextBody);
        messageBodyDetail.put(messageReceiverRef + "/" + messagepushId,messageTextBody);
        RootRef.updateChildren(messageBodyDetail).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){

                }
                else{
                    Toast.makeText(ChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                }
                SendMessageText.setText("");

            }
        });
    }

}
