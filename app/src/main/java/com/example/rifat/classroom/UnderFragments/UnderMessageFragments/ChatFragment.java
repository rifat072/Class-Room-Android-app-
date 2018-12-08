package com.example.rifat.classroom.UnderFragments.UnderMessageFragments;


import android.content.Intent;
import android.os.Bundle;;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rifat.classroom.Contacts;

import com.example.rifat.classroom.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private RecyclerView chatList;
    private DatabaseReference ChatRef,UsersRef;
    private FirebaseAuth mAuth;
    private String CurrentUserID;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myView =  inflater.inflate(R.layout.fragment_chat, container, false);
        chatList = (RecyclerView) myView.findViewById(R.id.chats_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, ChatViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options ) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {
                        final String userIDs = getRef(position).getKey();
                        UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                String UserImage = "default";
                                if(dataSnapshot.exists()){

                                    if(dataSnapshot.hasChild("image")){
                                        String RequestUserImage = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(RequestUserImage).placeholder(R.drawable.profile_image)
                                                .into(holder.profileImage);
                                        UserImage = RequestUserImage;
                                    }
                                    if(dataSnapshot.hasChild("status"))holder.userStatus.setText(dataSnapshot.child("status").getValue().toString());
                                    if(dataSnapshot.hasChild("name"))holder.userName.setText(dataSnapshot.child("name").getValue().toString());
                                    final String finalUserImage = UserImage;
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("visit_user_id",userIDs);
                                            chatIntent.putExtra("visit_user_name",dataSnapshot.child("name").getValue().toString());
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
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        return new ChatViewHolder(view);
                    }
                };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage;
        TextView userName, userStatus;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.user_profile_image);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
