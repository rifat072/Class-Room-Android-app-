package com.example.rifat.classroom.UnderFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rifat.classroom.R;
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
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;



public class Account extends Fragment {


    private Button UpdateAccountSettings;
    private EditText username,userstatus;
    private CircleImageView userProfileImage;
    private String CurrentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static final int PICK_IMAGE = 100;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylaout =  inflater.inflate(R.layout.account,container,false);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingbar = new ProgressDialog(getContext());
        findViewByIdList(mylaout);
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

        return mylaout;
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

    private void findViewByIdList(View mylaout) {
        UpdateAccountSettings = (Button)mylaout.findViewById(R.id.update_user_settings);
        username = (EditText)mylaout.findViewById(R.id.set_profile_name);
        userstatus = (EditText)mylaout.findViewById(R.id.set_user_status);
        userProfileImage = (CircleImageView)mylaout.findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
    }
    private void UpdateSettings(){
        String uname = username.getText().toString().trim();
        String status = userstatus.getText().toString().trim();
        if(uname == null) uname = "";
        if(status == null) status = "";
        HashMap<String, Object> profileMap= new HashMap<>();

        profileMap.put("uid",CurrentUserId);
        profileMap.put("name",uname);
        profileMap.put("status",status);
        RootRef.child("Users").child(CurrentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getContext(),"Profile Updated Successfully",Toast.LENGTH_LONG);
                }
                else{
                    String message = task.getException().toString();
                    Toast.makeText(getContext(),"Error : "+ message,Toast.LENGTH_LONG);
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
            CropImage.activity(ImageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(getContext(), this);
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
                            Toast.makeText(getActivity(),"Profile Image Uploaded Successfully",Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                        else{
                            String message = task.getException().toString();
                            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        }
    }
}
