package com.example.rifat.classroom.Fragments.RoutineFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.rifat.classroom.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class edit extends Fragment {

    EditText time,title;
    Button add;
    DatabaseReference databasesubject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.editroutine,container,false);
        time = (EditText)mylayout.findViewById(R.id.time);
        title = (EditText)mylayout.findViewById(R.id.title);
        add = (Button)mylayout.findViewById(R.id.add);
        databasesubject = FirebaseDatabase.getInstance().getReference("routine");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });


        return mylayout;
    }

    private void addArtist(){
        String tm = time.getText().toString().trim();
        String tt = title.getText().toString().trim();
        subject ss = new subject(tm,tt,"1");
        databasesubject.child("1"+ tm).setValue(ss);
    }
}
