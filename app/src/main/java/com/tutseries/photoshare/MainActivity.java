package com.tutseries.photoshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.tutseries.photoshare.models.Photo;
import com.tutseries.photoshare.models.PhotoTarget;
import com.tutseries.photoshare.utils.FacebookUtils;
import com.tutseries.photoshare.utils.FileUtils;
import com.tutseries.photoshare.utils.ParseFacebookUtils;

import java.util.ArrayList;
import java.util.List;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private int GALLERY_REQUEST_CODE = 2312;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_take_photo)
    public void onTakePhotoButtonClicked() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_button_view_photos)
    public void onViewPhotosButtonClicked() {
        startActivity(new Intent(this, PhotosActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                onPhotoReady(data.getData());
            }
        }
    }

    private void onPhotoReady(final Uri pathToImage) {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.please_wait));
        FacebookUtils.fetchFriends()
                .onSuccessTask(new Continuation<String[], Task<List<ParseUser>>>() {
                    @Override
                    public Task<List<ParseUser>> then(Task<String[]> task) throws Exception {
                        return ParseFacebookUtils.fetchFriendForId(task.getResult());
                    }
                })
                .onSuccessTask(new Continuation<List<ParseUser>, Task<Void>>() {
                    @Override
                    public Task<Void> then(Task<List<ParseUser>> task) throws Exception {
                        savePhoto(pathToImage, task.getResult());
                        return null;
                    }
                })
                .continueWith(new Continuation<Void, Void>() {
                    @Override
                    public Void then(Task<Void> task) throws Exception {
                        if (task.getError() != null)
                            task.getError().printStackTrace();

                        if (mProgressDialog != null)
                            mProgressDialog.cancel();

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void savePhoto(Uri pathToImage, final List<ParseUser> targets) {
        byte[] pictureContents = FileUtils.loadImage(pathToImage, this);
        if (pictureContents != null) {
            final Photo photo = new Photo();
            photo.setPhoto(new ParseFile(pictureContents));
            photo.setPhotographer(ParseUser.getCurrentUser());
            photo.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(MainActivity.this, "Successfully saved photo", Toast.LENGTH_SHORT).show();
                        createPhotoTargets(photo, targets);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void createPhotoTargets(Photo photo, final List<ParseUser> targets) {
        List<PhotoTarget> targetsToSave = new ArrayList<>();
        for (ParseUser userTarget : targets) {
            PhotoTarget target = new PhotoTarget();
            target.setPhoto(photo);
            target.setTarget(userTarget);
            targetsToSave.add(target);
        }

        ParseObject.saveAllInBackground(targets, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    onTargetsSaved(targets);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onTargetsSaved(List<ParseUser> users) {
        List<ParseQuery<ParseInstallation>> pushQueries = new ArrayList<>();
        for (ParseUser user : users) {
            ParseQuery<ParseInstallation> pushNotifQuery = ParseInstallation.getQuery()
                    .whereEqualTo("user", user);
            pushQueries.add(pushNotifQuery);
        }

        pushQueries.add(ParseInstallation.getQuery()
                .whereEqualTo("user", ParseUser.getCurrentUser()));
        ParsePush.sendMessageInBackground("Hey there, you have a new message", ParseQuery.or(pushQueries),
                new SendCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(MainActivity.this, "Sent notifications to friends!", Toast.LENGTH_SHORT).show();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
