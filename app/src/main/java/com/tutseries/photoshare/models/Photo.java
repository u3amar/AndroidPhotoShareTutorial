package com.tutseries.photoshare.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by usama on 8/27/15.
 */
@ParseClassName("Photo")
public class Photo extends ParseObject {
    public ParseUser getPhotographer() {
        return getParseUser("photographer");
    }

    public void setPhotographer(ParseUser photographer) {
        put("photographer", photographer);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }
}
