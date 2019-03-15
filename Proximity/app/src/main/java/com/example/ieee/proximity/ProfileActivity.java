package com.example.ieee.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private Button btnUpload1;
    private TextView profileEmail1;
    private EditText profileName1, profileSurName1, profileAge1;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Update Your Profile");

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnUpload1 = findViewById(R.id.btnUpload1);
        profileAge1 = findViewById(R.id.profileAge);
        profileName1 = findViewById(R.id.profileName1);
        profileSurName1 = findViewById(R.id.profileSurName1);
        profileEmail1 = findViewById(R.id.profilEmailView1);

        final DatabaseReference myRef = firebaseDatabase.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                profileEmail1.setText("Email:   " + firebaseAuth.getUid());
                profileAge1.setText(userProfile.getUserAge());
//                profileName1.setText(userProfile.getUserName());
//                profileSurName1.setText(userProfile.getUserSurName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Error in getting stored data", Toast.LENGTH_LONG).show();
            }
        });


        btnUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    progressDialog.setMessage("Please Wait...");
                    progressDialog.show();
                    String name = profileName1.getText().toString().trim();
                    String surname = profileSurName1.getText().toString().trim();
                    String age = profileAge1.getText().toString().trim();

                    UserProfile userProfile = new UserProfile(age, firebaseUser.getEmail(), name, surname);
                    myRef.child(firebaseAuth.getUid()).setValue(userProfile)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("Profile", "DOne");
                                }
                            });
                    progressDialog.dismiss();
                    finish();

                }
            }
        });

    }

    private boolean validate() {
        if (profileName1.getText().toString().isEmpty() || profileSurName1.getText().toString()
                .isEmpty() || profileAge1.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fill the details", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
