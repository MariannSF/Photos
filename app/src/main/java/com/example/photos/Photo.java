package com.example.photos;

import com.google.firebase.auth.FirebaseUser;

public class Photo {
    private String docId;
    private String photoOwner;
    private String uri;
    private String uid;

    public Photo() {
    }

    public Photo(String docId,String photoOwner, String uri, String getPhotoOwnerId) {
        this.docId = docId;
        this.photoOwner = photoOwner;
        this.uri = uri;
        this.uid = getPhotoOwnerId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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