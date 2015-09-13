package com.tutseries.photoshare;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.tutseries.photoshare.models.Photo;
import com.tutseries.photoshare.models.PhotoTarget;

/**
 * Created by usama on 8/26/15.
 */
public class PhotoShareApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "e8Amcdx1wHmKeUcDPQ8wKluqYFTSfBz0ZhjX4b25", "TX8XBdZ9UN4ezCVscqTmn9G3a8pBkJ9g7PcEWzAj");
        ParseFacebookUtils.initialize(this);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(PhotoTarget.class);
    }
}
