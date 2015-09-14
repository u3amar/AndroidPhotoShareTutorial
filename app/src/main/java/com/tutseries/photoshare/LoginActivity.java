package com.tutseries.photoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by usama on 8/26/15.
 */
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            onLoginSuccess();
        } else {
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.login_button_facebook)
    public void onFacebookButtonClicked() {
        ArrayList<String> permissions = new ArrayList<>(Arrays.asList(new String[]{"email", "user_friends"}));
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            initFacebookData();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void initFacebookData() {
        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                String facebookId = jsonObject.optString("id");
                ParseUser.getCurrentUser().put("facebookId", facebookId);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            updatePushData();
                        else
                            e.printStackTrace();
                    }
                });
            }
        }).executeAsync();
    }

    private void updatePushData() {
        ParseInstallation.getCurrentInstallation()
                .put("user", ParseUser.getCurrentUser());

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                    onLoginSuccess();
                else
                    e.printStackTrace();
            }
        });
    }

    private void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
