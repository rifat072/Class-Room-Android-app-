package com.example.rifat.classroom.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class Account extends Fragment {

    FirebaseAuth mAth;
    FirebaseUser user;

    TextView id,email;
    EditText name,oldpass,newpass,school;
    Button update;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylaout =  inflater.inflate(R.layout.account,container,false);
        mAth = FirebaseAuth.getInstance();
        user = mAth.getCurrentUser();
        id = (TextView)mylaout.findViewById(R.id.id);
        name = (EditText)mylaout.findViewById(R.id.name);
        oldpass = (EditText)mylaout.findViewById(R.id.oldpass);
        newpass = (EditText)mylaout.findViewById(R.id.newpass);
        school = (EditText)mylaout.findViewById(R.id.school);
        email = (TextView)mylaout.findViewById(R.id.email);
        update = (Button)mylaout.findViewById(R.id.update);

        id.setText(user.getDisplayName());
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name.getText().toString().trim()).build();
                user.updateProfile(profileupdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Successfull",Toast.LENGTH_LONG);
                        }
                        else{
                            Toast.makeText(getActivity(),"Can't update",Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });

        return mylaout;
    }
}
