package com.example.ieee.proximity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileFragmemt extends Fragment{

    private TextView tvEmail;
    private Button upLoad;
    private EditText etName,etSurName,etAge,etInterest;
    private ImageView imProfilePic;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    String name,age,email,surname,interest;
    private static int PICK_IMAGE = 960;
    private Uri imagePath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData()!=null){
            imagePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imagePath);
                imProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getActivity(), "You haven't picked Image",Toast.LENGTH_LONG).show();

        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmail = view.findViewById(R.id.profileEmailView);
        etName = view.findViewById(R.id.profileName);
        etSurName = view.findViewById(R.id.profileSurName);
        etInterest = view.findViewById(R.id.profileInterest);
        etAge = view.findViewById(R.id.profileAge);
        imProfilePic = view.findViewById(R.id.profileImage);
        upLoad = view.findViewById(R.id.btnUpload);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        final DatabaseReference mRef = firebaseDatabase.getReference(firebaseAuth.getUid());


        imProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }

        });

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                etAge.setText(userProfile.getUserAge());
                etName.setText(userProfile.getUserName());
                etSurName.setText(userProfile.getUserSurName());
                tvEmail.setText("Email:   "+userProfile.getUserEmail());
                etInterest.setText(userProfile.getUserInterest());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error in getting stored data",Toast.LENGTH_LONG).show();
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(imProfilePic);
            }
        });

        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                age = etAge.getText().toString();
                email = tvEmail.getText().toString();
                name = etName.getText().toString();
                surname = etSurName.getText().toString();
                interest = etInterest.getText().toString();

                if(name.isEmpty()||surname.isEmpty()||age.isEmpty()||interest.isEmpty()) {
                    Toast.makeText(getActivity(),"Fill all the details",Toast.LENGTH_SHORT).show();
                }else{
                    UserProfile userProfile = new UserProfile(age, firebaseUser.getEmail(), name, surname, "", interest, 0.0,0.0);
                    mRef.setValue(userProfile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(),"Upload Successful!",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(),"Upload Unsuccessful!",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                storageReference = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child("Profile Pic");
                UploadTask uploadTask = storageReference.putFile(imagePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Upload Failed!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Upload Successful!", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }
}
