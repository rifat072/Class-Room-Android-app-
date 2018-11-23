package com.example.rifat.classroom.UnderFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rifat.classroom.R;
import com.example.rifat.classroom.UnderFragments.UnderMessageFragments.TabAccessorAdapterMessages;

public class MessageFragment extends Fragment {

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapterMessages myTabAccessorAdapter;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.message,container,false);
        myViewPager = (ViewPager)mylayout.findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter = new TabAccessorAdapterMessages(getFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout = (TabLayout)mylayout.findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
        return mylayout;
    }
}
