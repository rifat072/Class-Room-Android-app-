package com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderGroupChatFragment;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.rifat.classroom.Contacts;

import com.example.rifat.classroom.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddFreindGroupActivity extends AppCompatActivity {

    private String CurrentGropupId;
    private ArrayList< Pair<String, Contacts> > list;
    private RecyclerView add_list;
    private DatabaseReference UsersRef,UserRefForGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_freind_group);

        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"black\">" + "Add to Group" + "</font>"));
        CurrentGropupId = getIntent().getExtras().get("groupId").toString();
        list = new ArrayList<>();
        add_list = (RecyclerView)findViewById(R.id.add_list);
        add_list.setLayoutManager(new LinearLayoutManager(this));

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserRefForGroup = UsersRef;


        //RefillList();

    }



    @Override
    protected void onStart() {
        super.onStart();
        RefillList();
        RefillList();

    }

    private void RefillList() {

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,AddGroupViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, AddGroupViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final AddGroupViewHolder holder, final int position, @NonNull final Contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profileImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user_id = model.uid;
                        Map<String, Object> mp = new HashMap<>();
                        mp.put(CurrentGropupId,"");
                        UserRefForGroup.child(user_id).child("Groups").updateChildren(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddFreindGroupActivity.this,"user has been added on this group", Toast.LENGTH_SHORT).show();

                            }
                        });
                        }
               });
            }

            @NonNull
            @Override
            public AddGroupViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                AddGroupViewHolder viewHolder = new AddGroupViewHolder(view);
                return viewHolder;
            }
        };
        add_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();



    }

    public static class AddGroupViewHolder extends RecyclerView.ViewHolder{
        TextView userName,userStatus;
        CircleImageView profileImage;

        public AddGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.user_profile_image);
        }
    }


}
