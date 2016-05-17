package com.framgia.fel1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.util.BitmapUtil;
import com.framgia.fel1.util.CheckRequire;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ReadJson;
import com.framgia.fel1.util.ShowImage;
import com.framgia.fel1.util.TaskFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class UpdateProfileActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {
    public static final String TAG = "UpdateProfileActivity";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private TaskFragment mTaskFragment;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private EditText mEditEmail;
    private EditText mEditNewPassword;
    private EditText mEditPasswordConfirmation;
    private EditText mEditName;
    private ImageView mImageAvatar;
    private User mUser;
    private Intent mData;
    private Bitmap mBitmapAvatar = null;
    private MySqliteHelper mMySqliteHelper;
    private SharedPreferences mSharedPreferences;
    private boolean isChangedAvatar = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mData = getIntent();
        mMySqliteHelper = new MySqliteHelper(this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if(id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else finish();
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if ( mTaskFragment == null ) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        setView();
        setEvent();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.ACTION_PICK_IMAGE:
                if (data != null && resultCode == RESULT_OK) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor =
                            getContentResolver().query(selectedImage, filePathColumn, null, null,
                                    null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    mBitmapAvatar = BitmapUtil.decodeSampledBitmapFromFile(picturePath, 100, 100);
                    mImageAvatar.setImageBitmap(mBitmapAvatar);
                    isChangedAvatar = true;
                } else {
                    isChangedAvatar = false;
                    mBitmapAvatar = null;
                    Toast.makeText(getApplicationContext(), R.string.not_changed_image,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditNewPassword = (EditText) findViewById(R.id.edit_new_password);
        mEditPasswordConfirmation = (EditText) findViewById(R.id.edit_password_confirmation);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mImageAvatar = (ImageView) findViewById(R.id.image_avatar);
        if(InternetUtils.isInternetConnected(UpdateProfileActivity.this, false))
            new ShowImage(mImageAvatar).execute(mUser.getAvatar());
        mFab.setImageDrawable(new IconicsDrawable(UpdateProfileActivity.this)
                                      .icon(FontAwesome.Icon.faw_check)
                                      .color(Color.GREEN));
        mEditName.setCompoundDrawables(new IconicsDrawable(UpdateProfileActivity.this)
                                               .icon(FontAwesome.Icon.faw_user)
                                               .color(Color.GRAY)
                                               .sizeRes(R.dimen.icon_size), null, null, null);
        mEditEmail.setCompoundDrawables(new IconicsDrawable(UpdateProfileActivity.this)
                                                .icon(FontAwesome.Icon.faw_envelope)
                                                .color(Color.GRAY)
                                                .sizeRes(R.dimen.icon_size), null, null, null);
        mEditNewPassword.setCompoundDrawables(new IconicsDrawable(UpdateProfileActivity.this)
                                                   .icon(FontAwesome.Icon.faw_lock)
                                                   .color(Color.GRAY)
                                                   .sizeRes(R.dimen.icon_size), null, null, null);
        mEditPasswordConfirmation
                .setCompoundDrawables(new IconicsDrawable(UpdateProfileActivity.this)
                                                               .icon(FontAwesome.Icon.faw_lock)
                                                               .color(Color.GRAY)
                                                               .sizeRes(R.dimen.icon_size),
                                                               null, null, null);


        if (mUser != null) {
            mEditEmail.setText(mUser.getEmail());
            mEditName.setText(mUser.getName());
        } else {
            Toast.makeText(getApplicationContext(), R.string.error_get_data,
                    Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        mProgressDialog = new ProgressDialog(UpdateProfileActivity.this);
    }

    private void setEvent() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckRequire.checkEmail(getApplicationContext(), mEditEmail) &&
                        CheckRequire.checkPassword(getApplicationContext(), mEditNewPassword,
                                mEditPasswordConfirmation)) {
                    if (InternetUtils.isInternetConnected(UpdateProfileActivity.this)) {
//                        new UpdateRequest().execute();
                        mTaskFragment.startInBackground(new String[]{mEditName.getText().toString(),
                                                    mEditEmail.getText().toString(),
                                                    mEditNewPassword.getText().toString(),
                                                    mEditPasswordConfirmation.getText().toString()});
                    }
                }
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickImage();
            }
        });
    }

    private void onPickImage() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType(Const.IMAGE_TYPE);
        Intent chooserIntent =
                Intent.createChooser(pickIntent, getResources().getString(R.string.select_image));
        startActivityForResult(chooserIntent, Const.ACTION_PICK_IMAGE);
    }

    @Override
    public void onPreExecute() {
        mProgressDialog.setMessage(getResources().getString(R.string.loading));
        mProgressDialog.show();
        mBitmapAvatar = ((BitmapDrawable) mImageAvatar.getDrawable()).getBitmap();
    }

    @Override
    public String onBackGround(String[] param) {
        JSONObject jsonObject = new JSONObject();
        String response = null;
        try {
            // change bitmap Image to base64 string
            if(mBitmapAvatar != null && isChangedAvatar) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mBitmapAvatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String bitmapEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                jsonObject.put(Const.AVATAR, bitmapEncoded);
            }
            //make JsonObject
            jsonObject.put(Const.NAME, param[0]);
            jsonObject.put(Const.EMAIL, param[1]);
            jsonObject.put(Const.PASSWORD, param[2]);
            jsonObject.put(Const.PASSWORD_CONFIRMATION, param[3]);
            JSONObject jsonObjectPost = new JSONObject();
            jsonObjectPost.put(Const.USER, jsonObject);
            try {
                String url = APIService.URL_UPDATE_PROFILE + mUser.getId() + Const.JSON_TYPE
                        + "?" + Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
                response = HttpRequest.postJsonRequest(url, jsonObjectPost,
                                                       "PATCH");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String response) {
        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (response == null) {
            Toast.makeText(UpdateProfileActivity.this, R.string.error_update_profile,
                           Toast.LENGTH_SHORT).show();
        } else {
            try {
                User user = new User(response);
                if ( user.getId() != 0 ) {
                    mMySqliteHelper.updateUser(user);
                    List<UserActivity> userActivityList = user.getActivities();
                    for (UserActivity userActivity : userActivityList) {
                        try {
                            mMySqliteHelper.addUserActivity(userActivity, user.getId());
                        } catch (SQLiteConstraintException e){
                            e.printStackTrace();
                            mMySqliteHelper.updateUserActivity(userActivity);
                        }
                    }
                    Toast.makeText(UpdateProfileActivity.this, R.string.update_successfully,
                                   Toast.LENGTH_SHORT).show();
                    Intent homeItent =
                            new Intent(UpdateProfileActivity.this, HomeActivity.class);
                    homeItent.putExtra(Const.USER, user);
                    startActivity(homeItent);
                    finish();
                } else {
                    String message = ReadJson.parseErrorJson(response);
                    Toast.makeText(UpdateProfileActivity.this, message,
                                   Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(UpdateProfileActivity.this, R.string.error_update_profile,
                               Toast.LENGTH_SHORT).show();
                Log.d(TAG, response.toString());
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.CONTENT_LOADING, mProgressDialog.isShowing());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(Const.CONTENT_LOADING)) {
            mProgressDialog.setMessage(getResources().getString(R.string.loading));
            mProgressDialog.show();
        }
    }

    private class UpdateRequest extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditNewPassword.getText().toString();
        String passwordConfirm = mEditPasswordConfirmation.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
            mBitmapAvatar = ((BitmapDrawable) mImageAvatar.getDrawable()).getBitmap();
        }

        @Override
        protected String doInBackground(String... params) {
            if (isCancelled()) {
                return null;
            }
            JSONObject jsonObject = new JSONObject();
            String response = null;
            try {
                // change bitmap Image to base64 string
                if(mBitmapAvatar != null && isChangedAvatar) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    mBitmapAvatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String bitmapEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    jsonObject.put(Const.AVATAR, bitmapEncoded);
                }
                //make JsonObject
                jsonObject.put(Const.NAME, name);
                jsonObject.put(Const.EMAIL, email);
                jsonObject.put(Const.PASSWORD, password);
                jsonObject.put(Const.PASSWORD_CONFIRMATION, passwordConfirm);
                JSONObject jsonObjectPost = new JSONObject();
                jsonObjectPost.put(Const.USER, jsonObject);
                try {
                    String url = APIService.URL_UPDATE_PROFILE + mUser.getId() + Const.JSON_TYPE
                            + "?" + Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
                    response = HttpRequest.postJsonRequest(url, jsonObjectPost,
                            "PATCH");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            if (response == null) {
                Toast.makeText(UpdateProfileActivity.this, R.string.error_update_profile,
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    User user = new User(response);
                    if ( user.getId() != 0 ) {
                        mMySqliteHelper.updateUser(user);
                        List<UserActivity> userActivityList = user.getActivities();
                        for (UserActivity userActivity : userActivityList) {
                            try {
                                mMySqliteHelper.addUserActivity(userActivity, user.getId());
                            } catch (SQLiteConstraintException e){
                                e.printStackTrace();
                                mMySqliteHelper.updateUserActivity(userActivity);
                            }
                        }
                        Toast.makeText(UpdateProfileActivity.this, R.string.update_successfully,
                                Toast.LENGTH_SHORT).show();
                        Intent homeItent =
                                new Intent(UpdateProfileActivity.this, HomeActivity.class);
                        homeItent.putExtra(Const.USER, user);
                        startActivity(homeItent);
                        finish();
                    } else {
                        String message = ReadJson.parseErrorJson(response);
                        Toast.makeText(UpdateProfileActivity.this, message,
                                       Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(UpdateProfileActivity.this, R.string.error_update_profile,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, response.toString());
                }
            }
        }
    }
}