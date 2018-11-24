package com.example.rifat.classroom.UnderFragments.UnderMessageFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {


    public GroupFragment() {
        // Required empty public constructor
    }

    private Button CreateGroup;
    private DatabaseReference RootRefinsert;
    private ListView  listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();

    private DatabaseReference RootRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mylaout = inflater.inflate(R.layout.fragment_group, container, false);
        CreateGroup = mylaout.findViewById(R.id.create_group);


        RootRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        Initialize(mylaout);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String CurrentGroupName = parent.getItemAtPosition(position).toString();
                Intent groupChat = new Intent(getContext(),GroupChatActivity.class);
                groupChat.putExtra("groupName",CurrentGroupName);
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
        RootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);
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

    private void CreateNewGroup(String groupname) {
        RootRef.child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Created a new Group",Toast.LENGTH_LONG);
                }
            }
        });
    }

}
