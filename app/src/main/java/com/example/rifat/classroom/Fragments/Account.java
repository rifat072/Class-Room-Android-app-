package com.example.rifat.classroom.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rifat.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


import static android.app.Activity.RESULT_OK;


public class Account extends Fragment {


    FirebaseAuth mAth;
    FirebaseUser user;

    TextView fullname,email;
    EditText f_name,l_name,newpass,school;
    Button update,imagebtn;
    ImageView userimage;
    Uri imageurl;
    Map<String,Object> dataToSave = new HashMap<String, Object>();
    private DocumentReference mDocRef;
    private StorageReference mStorageRef;
    private static final int PICK_IMAGE = 100;


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageurl = data.getData();
            userimage.setImageURI(imageurl);
            StorageReference ref = mStorageRef.child("UserImages/"+user.getEmail()+".jpg");
            ref.putFile(imageurl);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mylaout =  inflater.inflate(R.layout.account,container,false);
        findViewByIdList(mylaout);
        mAth = FirebaseAuth.getInstance();
        user = mAth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDocRef = FirebaseFirestore.getInstance().document("UserTable/" + user.getEmail());

        try {
            LoadResult(mylaout);
        } catch (IOException e) {
            e.printStackTrace();
        }


        email.setText(user.getEmail());

        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Gallary = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(Gallary,PICK_IMAGE);
            }
        });



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = newpass.getText().toString().trim();
                if(str != null && !str.equals("")){
                    user.updatePassword(newpass.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),"Updated",Toast.LENGTH_SHORT);
                        }
                    });
                }
                dataToSave.put("f_name",f_name.getText().toString().trim());
                dataToSave.put("l_name",l_name.getText().toString().trim());
                dataToSave.put("school",school.getText().toString().trim());
                mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Updated",Toast.LENGTH_LONG);
                    }
                });

            }
        });

        return mylaout;
    }


    private void LoadResult(View mylaout) throws IOException {
        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String fn = documentSnapshot.getString("f_name");
                String ln = documentSnapshot.getString("l_name");
                String sc = documentSnapshot.getString("school");
                f_name.setText(fn == null ? "" : fn);
                l_name.setText(ln == null ? "" : ln);
                school.setText(sc == null ? "" : sc);
                fullname.setText((fn != null && ln != null)? fn +" " + ln : "" );
                dataToSave.put("f_name",f_name.getText().toString().trim());
                dataToSave.put("l_name",l_name.getText().toString().trim());
                dataToSave.put("school",school.getText().toString().trim());
                dataToSave.put("image",documentSnapshot.getString("image"));
            }
        });

        StorageReference ref = mStorageRef.child("UserImages/"+user.getEmail()+".jpg");
        File localFile = File.createTempFile("images", "jpg");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.e("URI",uri.toString());
                Picasso.get().load(uri.toString()).resize(150,160).into(userimage);
            }
        });



    }

    private void findViewByIdList(View mylaout) {
        fullname = (TextView)mylaout.findViewById(R.id.full_name);
        f_name = (EditText)mylaout.findViewById(R.id.f_name);
        l_name = (EditText)mylaout.findViewById(R.id.l_name);
        newpass = (EditText)mylaout.findViewById(R.id.newpass);
        school = (EditText)mylaout.findViewById(R.id.school);
        email = (TextView)mylaout.findViewById(R.id.email);
        update = (Button)mylaout.findViewById(R.id.update);
        imagebtn = (Button)mylaout.findViewById(R.id.imgbtn);
        userimage = (ImageView)mylaout.findViewById(R.id.userimage);
    }

}
