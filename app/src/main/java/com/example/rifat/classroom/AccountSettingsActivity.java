package com.example.rifat.classroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText username,userstatus;
    private CircleImageView userProfileImage;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int PICK_IMAGE = 100;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar = new ProgressDialog(this);
        findViewByIdList();
        RetriveUserInformation();


        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/");
                startActivityForResult(gallaryIntent,PICK_IMAGE);
            }
        });
    }

    private void RetriveUserInformation() {

        RootRef.child("Users").child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("name")) username.setText(dataSnapshot.child("name").getValue().toString());
                    if(dataSnapshot.hasChild("status")) userstatus.setText(dataSnapshot.child("status").getValue().toString());
                    if(dataSnapshot.hasChild("image")) {
                        String downloadurl = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(downloadurl).into(userProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void findViewByIdList() {
        UpdateAccountSettings = (Button)findViewById(R.id.update_user_settings);
        username = (EditText)findViewById(R.id.set_profile_name);
        userstatus = (EditText)findViewById(R.id.set_user_status);
        userProfileImage = (CircleImageView)findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
    }
    private void UpdateSettings(){
        String uname = username.getText().toString().trim();
        String status = userstatus.getText().toString().trim();
        if(TextUtils.isEmpty(uname)) {
            Toast.makeText(getApplicationContext(),"You must provide a Name",Toast.LENGTH_LONG).show();
            return;
        }
        if(status == null) status = "";
        HashMap<String, Object> profileMap= new HashMap<>();

        profileMap.put("uid",CurrentUserId);
        profileMap.put("name",uname);
        profileMap.put("status",status);
        RootRef.child("Users").child(CurrentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                    Intent gotoNavigation = new Intent(AccountSettingsActivity.this,MainNavigation.class);
                    startActivity(gotoNavigation);
                    finish();
                }
                else{
                    String message = task.getException().toString();
                    Toast.makeText(getApplicationContext(),"Error : "+ message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE){
            Log.e("OnIf","AsChe");
            Uri ImageUri = data.getData();
            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(AccountSettingsActivity.this);
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == Activity.RESULT_OK){
                loadingbar.setTitle("Set Profile Image");
                loadingbar.setMessage("Please wait, your profile image is updating...");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();;
                Uri resultUri = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(CurrentUserId + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Profile Image Uploaded Successfully",Toast.LENGTH_LONG).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            RootRef.child("Users").child(CurrentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingbar.dismiss();
                                    }
                                    else{
                                        loadingbar.dismiss();
                                        String message = task.getException().toString();
                                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }
    }
}
