package com.example.rifat.classroom.UnderFragments.UnderMessageFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rifat.classroom.Contacts;
import com.example.rifat.classroom.R;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderContactFragment.FindFriendActivity;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderContactFragment.RequestActivity;
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
public class ContactsFragment extends Fragment {

    private Button findfriend,request;
    private RecyclerView myContactsList;
    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView =  inflater.inflate(R.layout.fragment_contacts, container, false);
        findfriend = (Button)myView.findViewById(R.id.FindFriend);
        request = (Button)myView.findViewById(R.id.Request);

        myContactsList = (RecyclerView)myView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserId);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");



        findfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findfriend = new Intent(getContext(), FindFriendActivity.class);
                startActivity(findfriend);
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent request = new Intent(getContext(), RequestActivity.class);
                startActivity(request);
            }
        });





        return myView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ContactsRef,Contacts.class)
                .build();

        final FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter  =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                        final String userIDs = getRef(position).getKey();
                        UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("image")){
                                    String UserImage = dataSnapshot.child("image").getValue().toString();
                                    Picasso.get().load(UserImage).placeholder(R.drawable.profile_image)
                                            .into(holder.profileImage);
                                }
                                if(dataSnapshot.hasChild("status")) holder.userStatus.setText(dataSnapshot.child("status").getValue().toString().trim());
                                if(dataSnapshot.hasChild("name"))holder.userName.setText(dataSnapshot.child("name").getValue().toString());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                        return viewHolder;
                    }
                };
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userStatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.user_profile_image);
        }
    }
}
