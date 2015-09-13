package com.tutseries.photoshare.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by usama on 9/12/15.
 */
@ParseClassName("PhotoTarget")
public class PhotoTarget extends ParseObject {
    public static ParseQuery<PhotoTarget> getQuery() {
        return new ParseQuery<PhotoTarget>("PhotoTarget");
    }

    public ParseUser getTarget() {
        return getParseUser("target");
    }

    public void setTarget(ParseUser target) {
        put("target", target);
    }

    public Photo getPhoto() {
        return (Photo) get("photo");
    }

    public void setPhoto(Photo photo) {
        put("photo", photo);
    }
}
