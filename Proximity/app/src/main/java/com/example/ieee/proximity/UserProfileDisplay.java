package com.example.ieee.proximity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserProfileDisplay extends AppCompatActivity {

    private ImageView image;
    private TextView email,fullName,age,interest;
    private Button location;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_display);
        setTitle("User Info");

        Bundle bundle = getIntent().getExtras();
        String uid = bundle.getString("uid");

        fullName = findViewById(R.id.profileFullName2);
        email = findViewById(R.id.profileEmailView2);
        age = findViewById(R.id.profileAge2);
        interest = findViewById(R.id.profileInterest2);
        location = findViewById(R.id.btnLocation);
        image = findViewById(R.id.profileImage2);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference(uid);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                age.setText("Age: "+userProfile.getUserAge());
                fullName.setText("Name: "+ userProfile.getUserName()+" "+userProfile.getUserSurName());
                email.setText("Email: "+userProfile.getUserEmail());
                interest.setText("Interest: "+userProfile.getUserInterest());

                location.setMovementMethod(LinkMovementMethod.getInstance());
                String text = "http://www.google.com/maps/place/"+Double.toString(userProfile.userLatitude)+","+Double.toString(userProfile.getUserLongitude());
                String text1 = "<a href="+text+"> Location </a>";
                location.setText(Html.fromHtml(text1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserProfileDisplay.this,"Error in getting stored data",Toast.LENGTH_LONG).show();
            }
        });

        storageReference.child(uid).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(image);
            }
        });



    }
}
