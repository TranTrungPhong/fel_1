package com.framgia.fel1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.presentation.home.HomeActivity;
import com.framgia.fel1.util.CheckRequire;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.TaskFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,TaskFragment.TaskCallbacks {
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private TaskFragment mTaskFragment;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxRememberMe;
    private Button mButtonLogin;
    private TextView mTextviewSignup;
    private ProgressDialog progressDialog;
    private int mCheckBoxRememMe = 0;
    private MySqliteHelper mMySqliteHelper;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        progressDialog = new ProgressDialog(LoginActivity.this);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if ( mTaskFragment == null ) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            mTaskFragment.onAttach((Context) this);
        }
        initView();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mEditTextEmail = (EditText) findViewById(R.id.edit_username_login);
        mEditTextPassword = (EditText) findViewById(R.id.edit_password_login);
        mEditTextEmail.setCompoundDrawables(
                new IconicsDrawable(LoginActivity.this).icon(FontAwesome.Icon.faw_envelope)
                        .color(Color.GRAY).sizeRes(R.dimen.icon_size), null, null, null);
        mEditTextPassword.setCompoundDrawables(
                new IconicsDrawable(LoginActivity.this).icon(FontAwesome.Icon.faw_lock)
                        .color(Color.GRAY).sizeRes(R.dimen.icon_size), null, null, null);
        mCheckBoxRememberMe = (CheckBox) findViewById(R.id.chechbox_remember);
        mTextviewSignup = (TextView) findViewById(R.id.text_sign_up);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        mTextviewSignup.setOnClickListener(this);
        mCheckBoxRememberMe.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if ( isChecked ) {
                            mCheckBoxRememMe = 1;
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        mEditTextEmail.setText(mSharedPreferences.getString(Const.EMAIL, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                if ( mEditTextPassword.getText().toString().equals("") ) {
                    Toast.makeText(LoginActivity.this, R.string.pass_not_blank,
                                   Toast.LENGTH_SHORT).show();
                } else if ( CheckRequire.checkEmail(LoginActivity.this, mEditTextEmail) )
                    if ( InternetUtils.isInternetConnected(LoginActivity.this) ) {
                        String[] param = new String[]{mEditTextEmail.getText().toString(),
                                mEditTextPassword.getText().toString()};
                        mTaskFragment.startInBackground(param);
                    }
                break;
            case R.id.text_sign_up:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPreExecute() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public String onBackGround(String[] param) {
        JSONObject jsonObject = new JSONObject();
        String response = null;
        try {
            jsonObject.put(NetwordConst.SESSION_EMAIL, param[0]);
            jsonObject.put(NetwordConst.SESSION_PASWORD, param[1]);
            jsonObject.put(NetwordConst.SESSION_REMEMBER_ME, mCheckBoxRememMe);
            JSONObject jsonObjectPost = new JSONObject();
            jsonObjectPost.put(NetwordConst.SESSION, jsonObject);
            try {
                response =
                        HttpRequest.postJsonRequest(NetwordConst.URL_API_SIGNIN, jsonObjectPost,
                                NetwordConst.METHOD_POST);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
            return response;
        }
    }

    @Override
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String s) {
        progressDialog.dismiss();
        if ( s == null ) {
            Toast.makeText(LoginActivity.this, R.string.tk_khongTonTai,
                    Toast.LENGTH_SHORT).show();
        } else if ( (s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "") ) {
            Toast.makeText(LoginActivity.this, R.string.error_login, Toast.LENGTH_SHORT).show();
        } else {
            String responseInvalid = null;
            try {
                JSONObject jsonObject = new JSONObject(s);
                responseInvalid = jsonObject.optString(getString(R.string.message_invalid));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ( ! responseInvalid.equals("") ) {
                Toast.makeText(LoginActivity.this, responseInvalid, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.login_done,
                        Toast.LENGTH_SHORT).show();
                try {
                    JSONObject jsonObjectUser = new JSONObject(s);
                    JSONObject response = jsonObjectUser.optJSONObject(Const.USER);
                    JSONArray jsonArrayAct = response.optJSONArray(Const.ACTIVITIES);
                    List<UserActivity> listUserActivity = new ArrayList<>();
                    for (int i = 0; i < jsonArrayAct.length(); i++) {
                        JSONObject joActivity = jsonArrayAct.optJSONObject(i);
                        UserActivity userActivity = new UserActivity(
                                Integer.parseInt(joActivity.optString(Const.ID)),
                                joActivity.optString(Const.CONTENT),
                                joActivity.optString(Const.CREATED_AT));
                        listUserActivity.add(userActivity);
                        try {
                            mMySqliteHelper.addUserActivity(userActivity, Integer.parseInt(
                                    response.getString(Const.ID)));
                        } catch (SQLiteConstraintException e) {
                            e.printStackTrace();
                            mMySqliteHelper.updateUserActivity(userActivity);
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    User user = new User(Integer.parseInt(response.getString(Const.ID)),
                            response.optString(Const.NAME),
                            response.optString(Const.EMAIL),
                            response.optString(Const.AVATAR),
                            Boolean.parseBoolean(response.optString(Const.ADMIN)),
                            response.optString(Const.AUTH_TOKEN),
                            response.optString(Const.CREATED_AT),
                            response.optString(Const.UPDATED_AT),
                            Integer.parseInt(response.optString(Const.LEARNED_WORDS)),
                            listUserActivity);
                    try {
                        mMySqliteHelper.addUser(user);
                    } catch (SQLiteConstraintException e) {
                        e.printStackTrace();
                        mMySqliteHelper.updateUser(user);
                    }
                    mSharedPreferences =
                            getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(Const.REMEMBER, mCheckBoxRememberMe.isChecked());
                    editor.putInt(Const.ID, user.getId());
                    editor.apply();
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.CONTENT_LOADING, progressDialog.isShowing());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(Const.CONTENT_LOADING)) {
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }
    }
}
