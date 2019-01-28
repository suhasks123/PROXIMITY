package com.example.ieee.proximity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    private TextView signUp;
    private Button login;
    private EditText userName, password;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        signUp = (TextView) findViewById(R.id.signUp);
        login = (Button) findViewById(R.id.Login);
        userName = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SignUp_Activity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString().trim().isEmpty()){ //trim() eliminates the leading or trailing spaces
                    Toast.makeText(MainActivity.this, "Please enter the UserName", Toast.LENGTH_SHORT).show();
                }else if(password.getText().toString().trim().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter the Password", Toast.LENGTH_SHORT).show();
                }else{
                    validate(userName.getText().toString().trim(),password.getText().toString().trim());


                }
            }
        });
    }
    private void validate(String user, String pass){
        progressDialog.setMessage("Logging In....");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(user,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,DashBoardActivity.class));
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
