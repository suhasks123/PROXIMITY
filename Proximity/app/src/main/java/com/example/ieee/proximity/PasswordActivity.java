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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import org.w3c.dom.Text;

public class PasswordActivity extends AppCompatActivity {

    private EditText passwordEmail;
    private Button resetPassword;
    private FirebaseAuth firebaseAuth;
    private TextView errorReset, returnmain;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        passwordEmail = (EditText) findViewById(R.id.passwordEmail);
        resetPassword = (Button) findViewById(R.id.btnPasswordReset);
        errorReset = (TextView) findViewById(R.id.errorReset);
        returnmain = (TextView) findViewById(R.id.returnMain);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = passwordEmail.getText().toString().trim();

                if(userEmail.equals("")){
                    Toast.makeText(PasswordActivity.this,"Please enter your Registered Email ID!",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.setMessage("Sending Password Reset mail...");
                            progressDialog.show();
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                errorReset.setVisibility(View.INVISIBLE);
                                Toast.makeText(PasswordActivity.this,"Password reset email sent!",Toast.LENGTH_SHORT).show();
                                Pause pause = new Pause();
                                pause.start();
                                finish();
                                startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                            }else{
                                progressDialog.dismiss();
                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                errorReset.setText(e.getMessage());
                                errorReset.setVisibility(View.VISIBLE);
                                Toast.makeText(PasswordActivity.this,"Error in sending password email!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        returnmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(PasswordActivity.this,MainActivity.class));
            }
        });
    }
    private class Pause extends Thread {
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
