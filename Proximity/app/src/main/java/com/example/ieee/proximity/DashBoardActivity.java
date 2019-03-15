package com.example.ieee.proximity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DashBoardActivity extends AppCompatActivity {

    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private SwipeRefreshLayout swipeRefreshLayout;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbarMain);
        //toolbar.setTitle("Dashboard");
        user = firebaseAuth.getCurrentUser();
        swipeRefreshLayout = findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                navigationDrawer();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        navigationDrawer();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.screen_area, new HomeFragment());
        tx.commit();
        if (ActivityCompat.checkSelfPermission(DashBoardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashBoardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }else{
            // Write you code here if permission already given.
        }
    }
    public void navigationDrawer(){
        // Create the AccountHeader
        final AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("XYZ").withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.logo))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Home");
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("Profile");
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("Change Password");
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName("Sign Out");
//create the drawer and remember the `Drawer` result object
        final Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item4
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :Dr
                        Fragment fragment = null;
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        switch (position){
                            case 1:
                                swipeRefreshLayout.setRefreshing(true);
                                toolbar.setTitle("Home");
                                fragment = new HomeFragment();
                                fragmentTransaction.replace(R.id.screen_area, fragment);
                                fragmentTransaction.commit();
                                swipeRefreshLayout.setRefreshing(false);
                                return false;
                            case 2:
                                swipeRefreshLayout.setRefreshing(true);
                                toolbar.setTitle("Profile");
                                fragment = new ProfileFragmemt();

                                fragmentTransaction.replace(R.id.screen_area, fragment);
                                fragmentTransaction.commit();
                                swipeRefreshLayout.setRefreshing(false);
                                return false;
                            case 3:swipeRefreshLayout.setRefreshing(true);
                                toolbar.setTitle("Change Password");
                                fragment = new ChangePassword();

                                fragmentTransaction.replace(R.id.screen_area, fragment);
                                fragmentTransaction.commit();
                                swipeRefreshLayout.setRefreshing(false);
                                return false;
                            case 4:firebaseAuth.signOut();
                                DashBoardActivity.this.finish();
                                startActivity(new Intent(DashBoardActivity.this,MainActivity.class));
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
