package com.example.ieee.proximity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    public String userAge,userEmail,userName,userSurName,userTime, userInterest;
    public Double userLatitude,userLongitude;
    public String uid;

    public UserProfile(String userAge, String userEmail, String userName, String userSurName
                        , String userTime, String userInterest, Double userLatitude, Double userLongitude) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userSurName = userSurName;
        this.userTime = userTime;
        this.userInterest = userInterest;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    public UserProfile(String uid){
        this.uid = uid;
    }

    public UserProfile(){

    }

    public String getUserAge() {
        return userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserSurName() {
        return userSurName;
    }

    public String getUserTime() {
        return userTime;
    }

    public String getUid() {
        return uid;
    }

    public String getUserInterest() {
        return userInterest;
    }

    public Double getUserLatitude() {
        return userLatitude;
    }

    public Double getUserLongitude() {
        return userLongitude;
    }

}
