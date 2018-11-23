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
import android.widget.TextView;
import android.widget.Toast;

import com.example.rifat.classroom.MainNavigation;
import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button login;
    private EditText username,password;
    private TextView register;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        login=(Button)findViewById(R.id.login);
        username=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        register=(TextView) findViewById(R.id.register);
        loadingbar = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=(String)username.getText().toString().trim();
                String pass=(String)password.getText().toString().trim();
                if(email == null || pass == null) return ;
                signin(email,pass);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
            }
        });




        if(mAuth.getCurrentUser()!=null){
            ///logged in;
            FirebaseUser user= mAuth.getCurrentUser();
            Intent i= new Intent(Login.this,MainNavigation.class);
            finish();
            startActivity(i);
        }


    }

    private void signin(String email, String pass) {
        loadingbar.setTitle("Signing In");
        loadingbar.setMessage("Please Wait...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        mAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            loadingbar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(Login.this,message,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loadingbar.dismiss();
                            Intent i= new Intent(Login.this,MainNavigation.class);
                            startActivity(i);
                            Login.this.finish();
                        }
                    }
                });
    }
}