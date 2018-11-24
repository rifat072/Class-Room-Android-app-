package com.example.rifat.classroom.UnderFragments.UnderRoutineFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

public class Full extends Fragment{


    FirebaseAuth mAth;
    FirebaseUser user;
    TextView routine[][] = new TextView[10][10];
    String ref;
    String email;

    private DocumentReference mDocRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylayout =  inflater.inflate(R.layout.fullroutine,container,false);

        mAth = FirebaseAuth.getInstance();
        user = mAth.getCurrentUser();
        email = user.getEmail();
        ref = "UserTable/" + email + "/Routine/";
        findViewByIdlist(mylayout);


        return mylayout;

    }

    @Override
    public void onStart() {
        super.onStart();
        LoadRoutine();
    }

    private void LoadRoutine() {
        for(int i = 2; i <= 6; i++){
            mDocRef = FirebaseFirestore.getInstance().document(ref + i);
            LoadData(i);
        }
    }

    private void LoadData(final int i) {
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    for(int j = 1; j <= 9 ; j++){
                        routine[i][j].setText(documentSnapshot.getString("" + j));

                    }
                }
            }
        });
    }

    private void findViewByIdlist(View mylayout) {
        for(int i = 2; i <= 6; i++) {
            for (int j = 1; j <= 9; j++) {
                String id = getDay(i) + j + "";
                int resId = getResources().getIdentifier(id, "id", getActivity().getPackageName());
                routine[i][j] = mylayout.findViewById(resId);
                routine[i][j].setText(id);
            }
        }
    }
    private String getDay(int i) {
        if(i == 2) return "su";
        else if(i == 3) return "mo";
        else if(i == 4) return "tu";
        else if(i == 5) return "we";
        else return "th";
    }

}
