package com.example.ieee.proximity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragmemt extends Fragment{

    private TextView tvEmail;
    private Button upLoad;
    private EditText etName,etSurName,etAge;
    private ImageView imProfilePic;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    String name,age,email,surname;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmail = view.findViewById(R.id.profilEmailView);
        etName = view.findViewById(R.id.profileName);
        etSurName = view.findViewById(R.id.profileSurName);
        etAge = view.findViewById(R.id.profileAge);
        imProfilePic = view.findViewById(R.id.profileImage);
        upLoad = view.findViewById(R.id.btnUpload);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference mRef = firebaseDatabase.getReference(firebaseAuth.getUid());

        //tvEmail.setText("Email: "+firebaseUser.getEmail());

        age = etAge.getText().toString();
        email = tvEmail.getText().toString();
        name = etName.getText().toString();
        surname = etSurName.getText().toString();

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                etAge.setText(userProfile.getUserAge());
                etName.setText(userProfile.getUserName());
                etSurName.setText(userProfile.getUserSurName());
                tvEmail.setText("Email:   "+userProfile.userEmail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error in getting stored data",Toast.LENGTH_LONG).show();
            }
        });

        upLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendUserData();
            }
        });

    }
}
