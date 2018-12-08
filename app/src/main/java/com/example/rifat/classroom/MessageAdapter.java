package com.example.rifat.classroom;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private Context context;

    public MessageAdapter(List<Messages> userMessagesList,Context context){
        this.userMessagesList = userMessagesList;
        this.context = context;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView senderMessagesText, ReceiverMessagesText;
        public CircleImageView receiverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessagesText = itemView.findViewById(R.id.sender_message_text);
            ReceiverMessagesText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout,viewGroup,false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    public Pair<String, String> getNameExt(String name){
        String filename = "",fileext = "";
        int i = 0;
        for(i = 0; i < name.length(); i++){
            char c = name.charAt(i);
            if(c == '.'){
                break;
            }
            filename += c;
        }
        i++;
        for(; i < name.length(); i++){
            char c = name.charAt(i);

            fileext += c;
        }

        return new Pair<>(filename,fileext);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        final Messages messages = userMessagesList.get(i);
        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("image")){
                        String receiverImage = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(fromMessageType.equals("text")){
            messageViewHolder.ReceiverMessagesText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessagesText.setVisibility(View.INVISIBLE);

            if(fromUserID.equals(messageSenderID)){
                messageViewHolder.senderMessagesText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessagesText.setText(messages.getMessage());
            }
            else{

                messageViewHolder.ReceiverMessagesText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.ReceiverMessagesText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.ReceiverMessagesText.setText(messages.getMessage());
            }
        }
        else if(fromMessageType.equals("file")){
            messageViewHolder.ReceiverMessagesText.setVisibility(View.INVISIBLE);
            messageViewHolder.receiverProfileImage.setVisibility(View.INVISIBLE);
            messageViewHolder.senderMessagesText.setVisibility(View.INVISIBLE);


            if(fromUserID.equals(messageSenderID)){
                messageViewHolder.senderMessagesText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessagesText.setTextColor(Color.BLUE);

                SpannableString content = new SpannableString(messages.getMessage());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                messageViewHolder.senderMessagesText.setText(content);
                messageViewHolder.senderMessagesText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pair<String, String > file = getNameExt(messages.getMessage());
                        try {
                            DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(messages.getLink());
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,messages.getMessage());
                            request.setTitle(messages.getMessage());
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            Long reference = downloadManager.enqueue(request);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("Clicked", messages.getLink());
                    }
                });
            }
            else{

                messageViewHolder.ReceiverMessagesText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.ReceiverMessagesText.setTextColor(Color.BLUE);
                messageViewHolder.ReceiverMessagesText.setBackgroundResource(R.drawable.receiver_messages_layout);


                SpannableString content = new SpannableString(messages.getMessage());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                messageViewHolder.ReceiverMessagesText.setText(content);
                messageViewHolder.ReceiverMessagesText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Pair<String, String > file = getNameExt(messages.getMessage());
                        try {
                            DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                            Uri uri = Uri.parse(messages.getLink());
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_DOWNLOADS,messages.getMessage());
                            request.setTitle(messages.getMessage());
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            Long reference = downloadManager.enqueue(request);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("Clicked", messages.getLink());
                    }
                });
            }

        }


    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
