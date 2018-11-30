package com.example.tech.coinz;

public class Friend {

    private String uID;
    private String DisplayName;
    private String email;

    public Friend(String uID, String displayName, String email) {
        this.uID = uID;
        DisplayName = displayName;
        this.email = email;
    }

    public String getuID() {
        return uID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public String getEmail() {
        return email;
    }
}
