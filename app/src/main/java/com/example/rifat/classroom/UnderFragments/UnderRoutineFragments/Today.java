package com.example.rifat.classroom.UnderFragments.UnderRoutineFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.TimeZone;

public class Today extends Fragment {

    FirebaseAuth mAth;
    FirebaseUser user;
    TextView routine[] = new TextView[10];
    String ref;
    String email;

    private DocumentReference mDocRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout = inflater.inflate(R.layout.todayroutine,container,false);

        findViewByIdList(mylayout);
        int day = getDay();
        mAth = FirebaseAuth.getInstance();
        user = mAth.getCurrentUser();
        email = user.getEmail();
        ref = "UserTable/" + email + "/Routine/" + day;
        mDocRef = FirebaseFirestore.getInstance().document(ref );


        return mylayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    for(int i = 1; i <= 9; i++){
                        routine[i].setText(documentSnapshot.getString("" + i));
                    }
                }
            }
        });
    }

    private int getDay() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if(Calendar.SATURDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 1;
        }
        else if(Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 2;
        }
        else if(Calendar.MONDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 3;
        }
        else if(Calendar.TUESDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 4;
        }
        else if(Calendar.WEDNESDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 5;
        }
        else if(Calendar.THURSDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 6;
        }
        else if(Calendar.FRIDAY == calendar.get(Calendar.DAY_OF_WEEK)){
            return 7;
        }
        return 0;
    }

    private void findViewByIdList(View mylayout) {
        for(int i = 1; i <= 9; i++){
            String id = "s" + i;
            int resId = getResources().getIdentifier(id, "id",getActivity().getPackageName());
            routine[i] = mylayout.findViewById(resId);
            routine[i].setText(id);

        }
    }


}
