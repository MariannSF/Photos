package com.example.photos;

import android.net.Uri;

public class Photo {

    private Uri uri;

    public Photo(Uri uri) {
        this.uri = uri;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
