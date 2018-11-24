package com.example.rifat.classroom.UnderFragments.UnderRoutineFragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rifat.classroom.MainNavigation;
import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    FirebaseAuth mAth;
    FirebaseUser user;
    EditText routine[][] = new EditText[10][10];
    Button updtbtn;
    String ref;
    String email;
    Map<String,Object> dataToSave = new HashMap<String, Object>();

    private DocumentReference mDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mAth = FirebaseAuth.getInstance();
        user = mAth.getCurrentUser();
        email = user.getEmail();
        ref = "UserTable/" + email + "/Routine/";


        findViewByIdlist();
        LoadRoutine();
        updtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 2; i <= 6; i++){
                    mDocRef = FirebaseFirestore.getInstance().document(ref + i);
                    dataToSave.clear();
                    for(int j = 1; j <= 9; j++){
                        dataToSave.put(j+"",routine[i][j].getText().toString().trim() == null? "":routine[i][j].getText().toString().trim());
                    }
                    mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditActivity.this,"Routine Updated",Toast.LENGTH_SHORT).show();
                            EditActivity.this.finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("mdocref","Can't Save");
                        }
                    });
                }
            }
        });


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
                        Log.e(i + " " + j,documentSnapshot.getString("" + j));

                    }
                }
            }
        });
    }


    private void findViewByIdlist() {
        updtbtn = (Button)findViewById(R.id.update);
        for(int i = 2; i <= 6; i++){
            for(int j = 1; j <= 9; j++){
                String id = getDay(i) + j + "edit";
                int resId = getResources().getIdentifier(id, "id",getPackageName());
                routine[i][j] = findViewById(resId);
                //routine[i][j].setText(id);
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
