package com.example.ieee.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class SignUp_Activity extends AppCompatActivity {

    private TextView loginPageAgain;
    private ProgressDialog progressDialog;
    private Button signUp;
    private EditText email,password,cpassword;
    private FirebaseAuth firebaseAuth;
    private String upass,ucpass,uemail,uname,uage,usurname;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.Password);
        cpassword = (EditText) findViewById(R.id.confirmPassword) ;
        signUp = (Button) findViewById(R.id.SignUp);
        progressDialog = new ProgressDialog(this);
        loginPageAgain = (TextView) findViewById(R.id.LoginPageAgain);


        Toast.makeText(this, "Please fill all the details", Toast.LENGTH_LONG).show();

        loginPageAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp_Activity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    progressDialog.setMessage("Registering.....");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(uemail, upass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        sendEmailVerification();
                                        LogoLaucher logoLaucher = new LogoLaucher();
                                        logoLaucher.start();
                                    } else {
                                        FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                        Toast.makeText(SignUp_Activity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                }
            }
        });
    }
    private Boolean validate(){
        Boolean result=false;

        uemail = email.getText().toString().trim();
        upass = password.getText().toString().trim();
        ucpass = cpassword.getText().toString().trim();
        if(uemail.isEmpty()||upass.isEmpty()||ucpass.isEmpty()){
            Toast.makeText(SignUp_Activity.this,"Please fill all the details!",Toast.LENGTH_SHORT).show();
        }else {
            if(!(upass.equals(ucpass))){
                Toast.makeText(SignUp_Activity.this, "Both passwords are not matching", Toast.LENGTH_SHORT).show();
            }else {
                result = true;
            }
        }
        return result;
    }
    private class LogoLaucher extends Thread {
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendEmailVerification(){
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(SignUp_Activity.this, "Successfully Registered, A verification mail is sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUp_Activity.this,MainActivity.class));
                    }else{
                        Toast.makeText(SignUp_Activity.this, "Verification mail hasn't been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void sendUserData(){
        imagePath = Uri.parse("android.resource://com.example.ieee.proximity/drawable/ic_action_defaultimg");
        uage="";
        uname="";
        usurname="";
        storageReference = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        UploadTask uploadTask = storageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp_Activity.this,"Upload Failed!",Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignUp_Activity.this,"Upload Successful!",Toast.LENGTH_SHORT).show();

            }
        });
        UserProfile userProfile = new UserProfile(uage,firebaseAuth.getCurrentUser().getEmail(),uname,usurname,"","",0.0,0.0);
        myRef.setValue(userProfile);
    }
}
