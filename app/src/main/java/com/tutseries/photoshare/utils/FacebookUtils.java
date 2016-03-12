package com.tutseries.photoshare.utils;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import bolts.Task;

/**
 * Created by usama on 9/13/15.
 */
public class FacebookUtils {
    public interface FriendsReadyListener {
        void onFriendsReady(String[] ids);
    }

    public static Task<String[]> fetchFriends() {
        final Task<String[]>.TaskCompletionSource fetchFriendsTask = Task.create();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject responseJson = response.getJSONObject();
                        if (responseJson != null) {
                            String[] ids = extractIdsForJSON(responseJson);
                            if (ids != null && ids.length > 0) {
                                fetchFriendsTask.setResult(ids);
                            } else {
                                fetchFriendsTask.setError(new Exception("Not enough ids for friends"));
                            }
                        } else {
                            fetchFriendsTask.setError(new Exception("Graph response is null"));
                        }
                    }
                }
        ).executeAsync();
        return fetchFriendsTask.getTask();
    }

    private static String[] extractIdsForJSON(JSONObject responseJson) {
        JSONArray data = responseJson.optJSONArray("data");
        if (data != null) {
            String[] ids = new String[data.length()];
            for (int i = 0; i < data.length(); i++) {
                ids[i] = data.optJSONObject(i).optString("id");
            }

            return ids;
        }

        return null;
    }

}
