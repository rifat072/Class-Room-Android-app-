package com.example.rifat.classroom.Fragments.RoutineFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.rifat.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Today extends Fragment {

    ListView listViewSubject;
    DatabaseReference databasesubject;
    List<subject> subjectList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.todayroutine,container,false);
        /*
        listViewSubject = (ListView)mylayout.findViewById(R.id.listviewsubject);
        databasesubject = FirebaseDatabase.getInstance().getReference("Routine");
        subjectList = new ArrayList<>();
        */
        return mylayout;
    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        databasesubject.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                subjectList.clear();
                for(DataSnapshot subjectnapshot : dataSnapshot.getChildren()){
                    subject subj = subjectnapshot.getValue(subject.class);
                    subjectList.add(subj);
                }
                ArrayAdapter<subject> nadapter = new ArrayAdapter<subject>(
                        getActivity(),android.R.layout.simple_list_item_1,subjectList
                );
                listViewSubject.setAdapter(nadapter);
                //todaylist adapter = new todaylist(Today.this,subjectList);
                //listViewSubject.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    */
}
