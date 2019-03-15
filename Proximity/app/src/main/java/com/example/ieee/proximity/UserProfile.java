package com.example.ieee.proximity;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserProfile {
    public String userAge,userEmail,userName,userSurName;

    public UserProfile(String userAge, String userEmail, String userName, String userSurName) {
        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userSurName = userSurName;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userAge", userAge);
        result.put("userName", userName);
        result.put("userSurName", userSurName);
        return result;
    }
}
