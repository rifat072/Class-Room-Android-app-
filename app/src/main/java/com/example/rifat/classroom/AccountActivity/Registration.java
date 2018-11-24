package com.example.rifat.classroom.AccountActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rifat.classroom.AccountSettingsActivity;
import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    private EditText username,password,passwordrepeat;
    private Button register;
    private String uname,pass,pass2;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    private DocumentReference mDocRef;
    private DatabaseReference RootRef;

    Map<String, Object > dataToSave = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        loadingbar = new ProgressDialog(this);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        passwordrepeat=(EditText)findViewById(R.id.passwordrepeat);
        register=(Button)findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname=username.getText().toString().trim();
                pass=password.getText().toString().trim();
                pass2=passwordrepeat.getText().toString().trim();
                if(uname == null || pass == null || pass2 == null) return;
                if(pass.equals(pass2)){
                    createUser(uname,pass);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Password doesn't match",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void createUser(String email, String pass) {
        loadingbar.setTitle("Creating New Account");
        loadingbar.setMessage("Please Wait, While we are creating new account for you.");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            String meassage = task.getException().toString();
                            Toast.makeText(getApplicationContext(),"Error: "+meassage,Toast.LENGTH_LONG);
                            loadingbar.dismiss();
                        }
                        else{

                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();
                            String devicetoken = FirebaseInstanceId.getInstance().getToken();

                            RootRef.child("Users").child(uid).child("device token")
                                    .setValue(devicetoken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                                    loadingbar.dismiss();
                                    startActivity(new Intent(getApplicationContext(), AccountSettingsActivity.class));
                                    Registration.this.finish();
                                }
                            });

                        }
                    }
                });

    }


}
