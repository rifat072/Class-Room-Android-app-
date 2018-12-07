package com.example.rifat.classroom;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderChatFragment.ChatActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainConversationActivity_v2 extends AppCompatActivity {
    private RecyclerView chatList;
    private DatabaseReference ChatRef,UsersRef;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_conversation_v2);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Chats" + "</font>"));
        //startActivity(new Intent(MainConversationActivity_v2.this,MainActivity_v2.class));
        chatList = (RecyclerView)findViewById(R.id.chat_lists_v2);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

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
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRef,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, MainConversationActivity_v2.ChatViewHolder_v2> adapter =
                new FirebaseRecyclerAdapter<Contacts, MainConversationActivity_v2.ChatViewHolder_v2>(options ) {
                    @Override
                    protected void onBindViewHolder(@NonNull final MainConversationActivity_v2.ChatViewHolder_v2 holder, int position, @NonNull Contacts model) {
                        final String userIDs = getRef(position).getKey();
                        UsersRef.child(userIDs);
                        UsersRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                String UserImage = "default";
                                if (dataSnapshot.exists()) {

                                    if (dataSnapshot.hasChild("image")) {
                                        String RequestUserImage = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(RequestUserImage).placeholder(R.drawable.profile_image)
                                                .into(holder.profileImage);
                                        UserImage = RequestUserImage;
                                    }
                                    holder.userStatus.setText("Last Seen : \nDate + Time");
                                    if (dataSnapshot.hasChild("name"))
                                        holder.userName.setText(dataSnapshot.child("name").getValue().toString());
                                    final String finalUserImage = UserImage;
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(MainConversationActivity_v2.this, ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id", userIDs);
                                            chatIntent.putExtra("visit_user_name", dataSnapshot.child("name").getValue().toString());
                                            chatIntent.putExtra("visit_user_image", finalUserImage);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MainConversationActivity_v2.ChatViewHolder_v2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        return new MainConversationActivity_v2.ChatViewHolder_v2(view);
                    }
                };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ChatViewHolder_v2 extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName, userStatus;
        public ChatViewHolder_v2(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
