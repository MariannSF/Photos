package com.example.photos;

import com.google.firebase.auth.FirebaseUser;

public class Photo {
    private String photoOwner;
    private String uri;
    private String uid;

    public Photo() {
    }

    public Photo(String photoOwner, String uri, String getPhotoOwnerId) {
        this.photoOwner = photoOwner;
        this.uri = uri;
        this.uid = getPhotoOwnerId;
    }

    public String getPhotoOwner() {
        return photoOwner;
    }

    public void setPhotoOwner(String photoOwner) {
        this.photoOwner = photoOwner;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getGetPhotoOwnerId() {
        return uid;
    }

    public void setGetPhotoOwnerId(String getPhotoOwnerId) {
        this.uid = getPhotoOwnerId;
    }
}