package com.framgia.fel1.activity;

import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.framgia.fel1.util.TaskFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks {
    private static final String TAG = "RegisterActivity";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private TaskFragment mTaskFragment;
    private Toolbar mToolbar;
    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPasswordConfirmation;
    private Button mButtonRegister;
    private LinearLayout mLayoutLoading;
    private LinearLayout mLayoutContent;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if ( mTaskFragment == null ) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
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
        mEditName.setCompoundDrawables(
                new IconicsDrawable(RegisterActivity.this).icon(FontAwesome.Icon.faw_user).color(
                        Color.GRAY).sizeRes(R.dimen.icon_size), null, null, null);
        mEditEmail.setCompoundDrawables(new IconicsDrawable(RegisterActivity.this).icon(
                FontAwesome.Icon.faw_envelope).color(Color.GRAY).sizeRes(R.dimen.icon_size), null,
                                        null, null);
        mEditPassword.setCompoundDrawables(
                new IconicsDrawable(RegisterActivity.this).icon(FontAwesome.Icon.faw_lock).color(
                        Color.GRAY).sizeRes(R.dimen.icon_size), null, null, null);
        mEditPasswordConfirmation.setCompoundDrawables(
                new IconicsDrawable(RegisterActivity.this).icon(FontAwesome.Icon.faw_lock).color(
                        Color.GRAY).sizeRes(R.dimen.icon_size), null, null, null);
    }

    private void setEvent() {
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( CheckRequire.checkEmail(getApplicationContext(), mEditEmail) &&
                        CheckRequire.checkPassword(getApplicationContext(), mEditPassword,
                                                   mEditPasswordConfirmation) ) {
                    mEditEmail.setError(null);
                    mEditPassword.setError(null);
                    mEditPasswordConfirmation.setError(null);
                    if ( InternetUtils.isInternetConnected(RegisterActivity.this) ) {
                        String[] param = new String[]{mEditName.getText().toString(),
                                mEditEmail.getText().toString(), mEditPassword.getText().toString(),
                                mEditPasswordConfirmation.getText().toString()};
                        mTaskFragment.startInBackground(param);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Const.CONTENT_LOADING, mLayoutLoading.getVisibility());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getInt(Const.CONTENT_LOADING, View.INVISIBLE) == View.VISIBLE) {
            mLayoutLoading.setVisibility(View.VISIBLE);
            mLayoutContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPreExecute() {
        mLayoutLoading.setVisibility(View.VISIBLE);
        mLayoutContent.setVisibility(View.GONE);
    }

    @Override
    public String onBackGround(String[] param) {
        JSONObject jsonObject = new JSONObject();
        JSONObject object = new JSONObject();
        try {
            jsonObject.put(Const.NAME, param[0]);
            jsonObject.put(Const.EMAIL, param[1]);
            jsonObject.put(Const.PASSWORD, param[2]);
            jsonObject.put(Const.PASSWORD_CONFIRMATION, param[3]);
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
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String response) {
        mLayoutLoading.setVisibility(View.GONE);
        mLayoutContent.setVisibility(View.VISIBLE);
        if ( response != null ) {
            try {
                User user = new User(response);
                if ( user.getId() != 0 ) {

                    Toast.makeText(RegisterActivity.this, R.string.register_successfully,
                                   Toast.LENGTH_SHORT).show();
                    mSharedPreferences =
                            getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Const.EMAIL, user.getEmail());
                    //                        editor.putString(Const.PASSWORD, password);
                    editor.apply();
                    onBackPressed();
                } else {
                    String message = ReadJson.parseErrorJson(response);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
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