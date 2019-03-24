package com.example.ieee.proximity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    private List<UserProfile> userProfiles;


    public MyAdapter(Context context, List<UserProfile> userProfiles) {
        this.context = context;
        this.userProfiles = userProfiles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item,viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final UserProfile userProfile = userProfiles.get(i);
        viewHolder.myref.child(userProfile.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                viewHolder.tvFullName.setText(profile.getUserName()+" "+profile.getUserSurName());
                viewHolder.tvAge.setText(profile.getUserAge());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error","Error in retrieving data");
            }
        });

        viewHolder.storageReference.child(userProfile.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(viewHolder.imageView);
            }
        });

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,UserProfileDisplay.class);
                intent.putExtra("uid",userProfile.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userProfiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvFullName,tvAge;
        public ImageView imageView;
        private FirebaseStorage firebaseStorage;
        private StorageReference storageReference;
        private FirebaseDatabase firebaseDatabase;
        private DatabaseReference myref;
        private LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.listItem);
            firebaseDatabase = FirebaseDatabase.getInstance();
            myref = firebaseDatabase.getReference();
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            tvFullName = itemView.findViewById(R.id.textViewFullName);
            tvAge = itemView.findViewById(R.id.textViewAge);
            imageView = itemView.findViewById(R.id.itemImage);
        }
    }
}
