package com.example.a02;

import java.io.Serializable;

public class commentData implements Serializable {
    private int Rating;
    private String user_ID;
    private String Comment;

    public int getRating() {
        return Rating;
    }
    public void setRating(int rating) {
        Rating = rating;
    }
    public String getUser_ID() {
        return user_ID;
    }
    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }
    public String getComment() {
        return Comment;
    }
    public void setComment(String comment) {
        Comment = comment;
    }
}
