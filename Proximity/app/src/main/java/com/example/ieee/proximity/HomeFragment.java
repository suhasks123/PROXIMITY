package com.example.ieee.proximity;

import android.Manifest;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment implements LocationListener {

    LocationManager locationManager;
    String provider;
    private Button find;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private FusedLocationProviderClient client;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private static final String URI_DATA = "http://10.53.77.21:8000/proximity/main/";
    private FirebaseUser firebaseUser;
    private DatabaseReference myRef;
    private StorageReference storageReference;


    private List<UserProfile> userProfiles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
// you can get seconds by adding  "...:ss" to it
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

        final String localTime = date.format(currentLocalTime);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userProfiles = new ArrayList<>();
        find = view.findViewById(R.id.btnFind);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(getActivity());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getUid());

        provider = locationManager.getBestProvider(new Criteria(), false);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {
                            if (location != null) {
//                            textView.setText(Double.toString(location.getLongitude())+","+Double.toString(location.getLatitude()));
                              //  Toast.makeText(getActivity(), Double.toString(location.getLongitude()) + ", " + Double.toString(location.getLatitude())+", "+localTime.substring(0,5), Toast.LENGTH_LONG).show();
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Loading data...");
                                progressDialog.show();



                                final JSONObject jsonObject = new JSONObject();
                                myRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                                        UserProfile userProfile1 = new UserProfile(userProfile.getUserAge(),userProfile.getUserEmail(),userProfile.getUserName()
                                                ,userProfile.getUserSurName(),localTime,userProfile.getUserInterest(),location.getLatitude(),location.getLongitude());
                                        myRef.setValue(userProfile1);
                                        try {
                                            jsonObject.put("uid",FirebaseAuth.getInstance().getUid());
                                            jsonObject.put("interest",userProfile.getUserInterest());
                                            jsonObject.put("x",Double.toString(location.getLongitude()));
                                            jsonObject.put("y",Double.toString(location.getLatitude()));
                                            jsonObject.put("time",localTime.substring(0,2)+"."+localTime.substring(3,5));

                                            //Toast.makeText(getActivity(),jsonObject.toString(),Toast.LENGTH_LONG).show();

                                            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(Request.Method.POST,
                                                    URI_DATA, jsonObject , new Response.Listener<JSONArray>() {
                                                @Override
                                                public void onResponse(JSONArray response) {
                                                    progressDialog.dismiss();
                                                    try {
                                                        for(int i=0; i<response.length(); i++){
                                                            JSONObject arrayObject = response.getJSONObject(i);
                                                            UserProfile userProfile = new UserProfile(arrayObject.getString("uid"));
                                                            userProfiles.add(userProfile);
                                                        }

                                                        adapter = new MyAdapter(getActivity().getApplicationContext(),userProfiles);
                                                        recyclerView.setAdapter(adapter);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity().getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                            requestQueue.add(customJsonArrayRequest);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                                            }

                        }
                    });
                }
            }
        });

    }



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Required!")
                        .setMessage("The Location is required to find people around you")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) getActivity());
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
