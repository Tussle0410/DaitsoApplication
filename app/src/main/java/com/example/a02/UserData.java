package com.example.a02;

import java.io.Serializable;

public class UserData implements Serializable {
    private String userNo;
    private String userPoint;
    private String userID;
    private String userPW;
    private String userName;
    private String userBirth;
    private String userSex;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBirth() {
        return userBirth;
    }

    public void setUserBirth(String userBirth) {
        this.userBirth = userBirth;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }
    public String getUserNo() {
        return userNo;
    }
    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }
    public String getUserPoint() {
        return userPoint;
    }
    public void setUserPoint(String userPoint) {
        this.userPoint = userPoint;
    }
}
