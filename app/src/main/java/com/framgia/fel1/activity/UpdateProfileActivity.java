package com.framgia.fel1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
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
import com.framgia.fel1.util.BitmapUtil;
import com.framgia.fel1.util.CheckRequire;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ReadJson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateProfileActivity extends AppCompatActivity {
    public static final String TAG = "UpdateProfileActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        mData = getIntent();
        //mUser = (User) mData.getSerializableExtra(Const.USER);
        mMySqliteHelper = new MySqliteHelper(this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if(id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else finish();
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
                    mUser.setAvatar(picturePath);
                    MySqliteHelper mySqliteHelper = new MySqliteHelper(UpdateProfileActivity.this);
                    mySqliteHelper.updateUser(mUser);
                } else {
                    mBitmapAvatar = null;
                    Toast.makeText(getApplicationContext(), R.string.error_get_image,
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
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(mUser.getAvatar(), 100, 100);
        if(bitmap != null)
            mImageAvatar.setImageBitmap(bitmap);
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
    }

    private void setEvent() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckRequire.checkEmail(getApplicationContext(), mEditEmail) &&
                        CheckRequire.checkPassword(getApplicationContext(), mEditNewPassword,
                                mEditPasswordConfirmation)) {
                    new UpdateRequest().execute();
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

    private class UpdateRequest extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditNewPassword.getText().toString();
        String passwordConfirm = mEditPasswordConfirmation.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!InternetUtils.isInternetConnected(UpdateProfileActivity.this)) {
                cancel(true);
            }
            progressDialog = new ProgressDialog(UpdateProfileActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
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
                if(mBitmapAvatar != null) {
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
                jsonObject.put(Const.AUTH_TOKEN, mUser.getAuthToken());
                JSONObject jsonObjectPost = new JSONObject();
                jsonObjectPost.put(Const.USER, jsonObject);
                try {
                    response = HttpRequest.postJsonRequest(APIService.URL_UPDATE_PROFILE, jsonObjectPost,
                            APIService.METHOD_POST);
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
                        Toast.makeText(UpdateProfileActivity.this, R.string.update_successfully,
                                Toast.LENGTH_SHORT).show();
                        Intent homeItent =
                                new Intent(UpdateProfileActivity.this, HomeActivity.class);
                        homeItent.putExtra(Const.USER, mUser);
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