package com.example.rifat.classroom.UnderFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.rifat.classroom.UnderFragments.UnderRoutineFragments.EditActivity;

import com.example.rifat.classroom.UnderFragments.UnderRoutineFragments.TabAccessorAdapterRoutine;
import com.example.rifat.classroom.R;


public class RoutineFragment extends Fragment {
    private Button edit;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapterRoutine myTabAccessorAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.routine,container,false);

        edit = (Button)mylayout.findViewById(R.id.editbtn);

        myViewPager = (ViewPager)mylayout.findViewById(R.id.main_tabs_pager_routine);
        myTabAccessorAdapter = new TabAccessorAdapterRoutine(getChildFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout = (TabLayout)mylayout.findViewById(R.id.main_tabs_routine);
        myTabLayout.setupWithViewPager(myViewPager);



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
