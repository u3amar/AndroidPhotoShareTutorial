package com.tutseries.photoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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
        ArrayList<String> permissions = new ArrayList<>(Arrays.asList(new String[]{"email"}));
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            onLoginSuccess();
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

    private void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
