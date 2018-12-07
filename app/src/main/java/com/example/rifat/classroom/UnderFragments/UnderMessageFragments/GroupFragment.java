package com.example.rifat.classroom.UnderFragments.UnderMessageFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rifat.classroom.R;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.UnderGroupChatFragment.GroupChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {


    public GroupFragment() {
        // Required empty public constructor
    }

    private Button CreateGroup;
    private DatabaseReference UsersGroup;
    private ListView  listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private ArrayList<String> list_of_groups_id = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mylaout = inflater.inflate(R.layout.fragment_group, container, false);
        CreateGroup = mylaout.findViewById(R.id.create_group);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        UsersGroup = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Groups");


        Initialize(mylaout);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CurrentGroupName = parent.getItemAtPosition(position).toString();
                String CurrentGroupId = list_of_groups_id.get(position);
                Intent groupChat = new Intent(getContext(),GroupChatActivity.class);
                groupChat.putExtra("groupName",CurrentGroupName);
                groupChat.putExtra("groupId",CurrentGroupId);
                startActivity(groupChat);
            }
        });

        CreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Enter Group Name");
                final EditText groupNameField= new EditText(getActivity());
                groupNameField.setHint("e.g. Friend Zone");
                builder.setView(groupNameField);
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupname = groupNameField.getText().toString().trim();
                        if(TextUtils.isEmpty(groupname)){
                            Toast.makeText(getActivity(),"Please Write Group Name",Toast.LENGTH_LONG);
                        }
                        else{
                                CreateNewGroup(groupname);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        return mylaout;
    }

    @Override
    public void onStart() {
        super.onStart();
        RetriveAndDisplayGroup();
    }

    private void RetriveAndDisplayGroup() {
        UsersGroup.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_of_groups.clear();
                list_of_groups_id.clear();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot temp = (DataSnapshot)iterator.next();
                    //list_of_groups.add(temp.getKey());
                    list_of_groups_id.add(temp.getKey());

                    RootRef.child(temp.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            list_of_groups.add(dataSnapshot.child("GroupName").getValue().toString());

                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void Initialize(View mylaout) {

        listView = mylaout.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listView.setAdapter(arrayAdapter);
    }

    private void CreateNewGroup(final String groupname) {
        DatabaseReference newGroup = RootRef.child(groupname).push();
        final String newGroupId = newGroup.getKey();
        HashMap<String,Object> groupDetails = new HashMap<>();
        groupDetails.put("GroupName",groupname);
        RootRef.child(newGroupId).setValue(groupDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Map<String, Object> mp = new HashMap<>();
                    mp.put(newGroupId,"");
                    UsersGroup.updateChildren(mp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Group Created",Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

}
