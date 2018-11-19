package com.example.rifat.classroom.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.rifat.classroom.EditActivity;
import com.example.rifat.classroom.Fragments.RoutineFragments.Full;
import com.example.rifat.classroom.Fragments.RoutineFragments.Today;
import com.example.rifat.classroom.R;


public class RoutineFragment extends Fragment {
    Button today,full,edit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.routine,container,false);
        today = (Button)mylayout.findViewById(R.id.todaybtn);
        full = (Button)mylayout.findViewById((R.id.fullbtn));
        edit = (Button)mylayout.findViewById(R.id.editbtn);

        if(savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.routine_fragment_container,
                    new Today()).commit();
        }

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.routine_fragment_container,
                        new Today()).commit();
            }
        });

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.routine_fragment_container,
                        new Full()).commit();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k = new Intent(getActivity(), EditActivity.class);
                startActivity(k);
            }
        });
        return mylayout;

    }
}
