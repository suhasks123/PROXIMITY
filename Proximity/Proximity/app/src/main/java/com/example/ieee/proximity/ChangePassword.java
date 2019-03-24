package com.example.ieee.proximity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

public class ChangePassword extends Fragment{

    private Button btnCp;
    private EditText etCp;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCp = view.findViewById(R.id.btnCp);
        etCp = view.findViewById(R.id.etCp);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnCp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cPassword = etCp.getText().toString();
                firebaseUser.updatePassword(cPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),"Password Updated!",Toast.LENGTH_SHORT).show();
                            etCp.getText().clear();
                        }else{
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(getActivity(), "Update Failed! "+e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}