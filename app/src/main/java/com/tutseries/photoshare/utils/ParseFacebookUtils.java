package com.tutseries.photoshare.utils;

import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by usama on 9/13/15.
 */
public class ParseFacebookUtils {
    public static Task<List<ParseUser>> fetchFriendForId(String[] ids) {
        List<ParseQuery<ParseUser>> friendsQueries = new ArrayList<>();
        for (String id : ids) {
            friendsQueries.add(ParseUser.getQuery()
                    .whereEqualTo("facebookId", id));
        }

        final Task<List<ParseUser>>.TaskCompletionSource friendTask = Task.create();
        ParseQuery.or(friendsQueries).findInBackground()
                .continueWith(new Continuation<List<ParseUser>, Void>() {
                    @Override
                    public Void then(Task<List<ParseUser>> task) throws Exception {
                        if (task.getError() == null)
                            friendTask.setResult(task.getResult());
                        else
                            friendTask.setError(new Exception("Couldn't fetch Facebook friends"));
                        return null;
                    }
                });

        return friendTask.getTask();
    }
}
