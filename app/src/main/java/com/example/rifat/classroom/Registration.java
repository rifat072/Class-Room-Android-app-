package com.example.rifat.classroom;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration extends AppCompatActivity {

    private EditText username,password,passwordrepeat;
    private Button register;
    private String uname,pass,pass2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registration);
        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        passwordrepeat=(EditText)findViewById(R.id.passwordrepeat);
        register=(Button)findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname=username.getText().toString().trim();
                pass=password.getText().toString().trim();
                pass2=passwordrepeat.getText().toString().trim();
                Log.d("uname",uname);
                Log.d("password",pass);
                if(pass.equals(pass2)){
                    createUser(uname,pass);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Registratin Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void createUser(String email, String pass) {
        Log.d("email",email);
        Log.d("pass",pass);
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Registratin Failed",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(),Login.class));
                            Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


}
