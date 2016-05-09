package com.framgia.fel1.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.User;
import com.framgia.fel1.util.CheckRequire;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ReadJson;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Toolbar mToolbar;
    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPasswordConfirmation;
    private Button mButtonRegister;
    private LinearLayout mLayoutLoading;
    private LinearLayout mLayoutContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        setEvent();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(null);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditPasswordConfirmation = (EditText) findViewById(R.id.edit_password_confirmation);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mLayoutLoading = (LinearLayout) findViewById(R.id.layout_loading);
        mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
        mLayoutContent.setVisibility(View.VISIBLE);
        mLayoutLoading.setVisibility(View.GONE);
        mEditName.setCompoundDrawables(new IconicsDrawable(RegisterActivity.this)
                                                .icon(FontAwesome.Icon.faw_user)
                                                .color(Color.GRAY)
                                                .sizeRes(R.dimen.icon_size), null, null, null);
        mEditEmail.setCompoundDrawables(new IconicsDrawable(RegisterActivity.this)
                                                .icon(FontAwesome.Icon.faw_envelope)
                                                .color(Color.GRAY)
                                                .sizeRes(R.dimen.icon_size), null, null, null);
        mEditPassword.setCompoundDrawables(new IconicsDrawable(RegisterActivity.this)
                                                .icon(FontAwesome.Icon.faw_lock)
                                                .color(Color.GRAY)
                                                .sizeRes(R.dimen.icon_size), null, null, null);
        mEditPasswordConfirmation.setCompoundDrawables(new IconicsDrawable(RegisterActivity.this)
                                                .icon(FontAwesome.Icon.faw_lock)
                                                .color(Color.GRAY)
                                                .sizeRes(R.dimen.icon_size), null, null, null);
    }

    private void setEvent() {
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( CheckRequire.checkEmail(getApplicationContext(), mEditEmail) &&
                        CheckRequire.checkPassword(getApplicationContext(), mEditPassword,
                                mEditPasswordConfirmation)) {
                    mEditEmail.setError(null);
                    mEditPassword.setError(null);
                    mEditPasswordConfirmation.setError(null);
                    new RegisterRequest().execute();
                }
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class RegisterRequest extends AsyncTask<String, String, String> {
        String name = mEditName.getText().toString();
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        String passwordConfirmation = mEditPasswordConfirmation.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLayoutLoading.setVisibility(View.VISIBLE);
            mLayoutContent.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
                JSONObject jsonObject = new JSONObject();
                JSONObject object = new JSONObject();
                try {
                    jsonObject.put(Const.NAME, name);
                    jsonObject.put(Const.EMAIL, email);
                    jsonObject.put(Const.PASSWORD, password);
                    jsonObject.put(Const.PASSWORD_CONFIRMATION, passwordConfirmation);
                    object.put(Const.USER, jsonObject);
                    String response = null;
                    try {
                        response = HttpRequest.postJsonRequest(APIService.URL_API_SIGNUP, object,
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
            mLayoutLoading.setVisibility(View.GONE);
            mLayoutContent.setVisibility(View.VISIBLE);
            if ( response != null ) {
                try {
                    User user = new User(response);
                    if ( user.getId() != 0 ) {

                        Toast.makeText(RegisterActivity.this, R.string.register_successfully,
                                       Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        String message = ReadJson.parseErrorJson(response);
                        Toast.makeText(RegisterActivity.this, message,
                                       Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, R.string.register_error,
                                   Toast.LENGTH_SHORT).show();
                    Log.d(TAG, response.toString());
                }
            }
        }
    }
}