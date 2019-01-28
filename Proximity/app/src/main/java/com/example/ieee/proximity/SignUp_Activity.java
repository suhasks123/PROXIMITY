package com.example.ieee.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class SignUp_Activity extends AppCompatActivity {

    private TextView loginPageAgain;
    private ProgressDialog progressDialog;
    private Button signUp;
    private EditText firstName,email,username,password,cpassword;
    private FirebaseAuth firebaseAuth;
    private String ufirstName,uname,upass,ucpass,uemail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_);

        firstName = (EditText) findViewById(R.id.firstName);
        firebaseAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.Password);
        cpassword = (EditText) findViewById(R.id.confirmPassword) ;
        username = (EditText) findViewById(R.id.UserName);
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
                                        Toast.makeText(SignUp_Activity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
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

        ufirstName = firstName.getText().toString().trim();
        uname = username.getText().toString().trim();
        uemail = email.getText().toString().trim();
        upass = password.getText().toString().trim();
        ucpass = cpassword.getText().toString().trim();
        if(ufirstName.isEmpty()||uname.isEmpty()||uemail.isEmpty()||upass.isEmpty()||ucpass.isEmpty()){
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
            Intent intent = new Intent(SignUp_Activity.this, MainActivity.class);
            startActivity(intent);
            SignUp_Activity.this.finish();

        }
    }
}
