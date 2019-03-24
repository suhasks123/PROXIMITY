package com.example.ieee.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Button btnUpload1;
    private ImageView profileImage1;
    private TextView profileEmail1;
    private EditText profileName1, profileSurName1, profileAge1,profileInterest1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static int PICK_IMAGE = 961;
    private Uri imagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData()!=null){
            imagePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                profileImage1.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(ProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Update Your Profile");

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnUpload1 = findViewById(R.id.btnUpload1);
        profileInterest1 = findViewById(R.id.profileInterest1);
        profileAge1 = findViewById(R.id.profileAge1);
        profileName1 = findViewById(R.id.profileName1);
        profileSurName1 = findViewById(R.id.profileSurName1);
        profileEmail1 = findViewById(R.id.profileEmailView1);
        profileImage1 = findViewById(R.id.profileImage1);

        final DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());

        profileImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }

        });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profileEmail1.setText("Email:   " + userProfile.getUserEmail());
                profileAge1.setText(userProfile.getUserAge());
                profileName1.setText(userProfile.getUserName());
                profileSurName1.setText(userProfile.getUserSurName());
                profileInterest1.setText(userProfile.getUserInterest());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error in getting stored data", Toast.LENGTH_LONG).show();
            }
        });

        storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage1);
            }
        });


        btnUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                String name = profileName1.getText().toString();
                String surname = profileSurName1.getText().toString();
                String age = profileAge1.getText().toString();
                String interest = profileInterest1.getText().toString();
                if(name.isEmpty()||surname.isEmpty()||age.isEmpty()||interest.isEmpty()) {
                    Toast.makeText(ProfileActivity.this,"Fill all the details",Toast.LENGTH_SHORT).show();
                }else{
                    UserProfile userProfile = new UserProfile(age,firebaseUser.getEmail(), name, surname,"",interest,0.0,0.0);
                    myRef.setValue(userProfile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this,"Upload Successful!",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProfileActivity.this,"Upload Unsuccessful!",Toast.LENGTH_SHORT).show();;
                                    }
                                }
                            });
                }
                storageReference = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child("Profile Pic");
                UploadTask uploadTask = storageReference.putFile(imagePath);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this,"Upload Failed!",Toast.LENGTH_SHORT).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this,"Upload Successful!",Toast.LENGTH_SHORT).show();

                    }
                });
                progressDialog.dismiss();
                startActivity(new Intent(ProfileActivity.this,DashBoardActivity.class));
                finish();

            }
        });

    }
}
