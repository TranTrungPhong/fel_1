package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxRememberMe;
    private Button mButtonLogin;
    private TextView mTextviewSignup;
    private ProgressDialog progressDialog;
    private int mCheckBoxRememMe = 0;
    private MySqliteHelper mMySqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mEditTextEmail = (EditText) findViewById(R.id.edit_username_login);
        mEditTextPassword = (EditText) findViewById(R.id.edit_password_login);
        mCheckBoxRememberMe = (CheckBox) findViewById(R.id.chechbox_remember);
        mTextviewSignup = (TextView) findViewById(R.id.text_sign_up);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonLogin.setOnClickListener(this);
        mTextviewSignup.setOnClickListener(this);
        mCheckBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCheckBoxRememMe = 1;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                new LoginToServer().execute();
                break;
            case R.id.text_sign_up:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private class LoginToServer extends AsyncTask<String, String, String> {
        String email = mEditTextEmail.getText().toString();
        String password = mEditTextPassword.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!InternetUtils.isInternetConnected(LoginActivity.this)) {
                cancel(true);
            }
            progressDialog = new ProgressDialog(LoginActivity.this);
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
                jsonObject.put(APIService.SESSION_EMAIL, email);
                jsonObject.put(APIService.SESSION_PASWORD, password);
                jsonObject.put(APIService.SESSION_REMEMBER_ME, mCheckBoxRememMe);
                JSONObject jsonObjectPost = new JSONObject();
                jsonObjectPost.put(APIService.SESSION, jsonObject);
                response = HttpRequest.postJSON(APIService.URL_API_SIGNIN,
                        jsonObjectPost,
                        APIService.METHOD_POST);
                return response;
            } catch (JSONException e) {
                e.printStackTrace();
                return response;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s == null) {
                Toast.makeText(LoginActivity.this, R.string.tk_khongTonTai, Toast.LENGTH_SHORT)
                        .show();
            } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                    (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
                Toast.makeText(LoginActivity.this, R.string.error_login, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.login_done, Toast.LENGTH_SHORT).show();
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
                                joActivity.optString(Const.CREATED_AT)
                        );
                        listUserActivity.add(userActivity);
                    }
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    User user = new User(
                            Integer.parseInt(response.getString(Const.ID)),
                            response.optString(Const.NAME),
                            response.optString(Const.EMAIL),
                            response.optString(Const.AVATAR),
                            Boolean.parseBoolean(response.optString(Const.ADMIN)),
                            response.optString(Const.AUTH_TOKEN),
                            response.optString(Const.CREATED_AT),
                            response.optString(Const.UPDATED_AT),
                            Integer.parseInt(response.optString(Const.LEARNED_WORDS)),
                            listUserActivity
                    );
                    //intent.putExtra(Const.USER, user);
                    mMySqliteHelper.addUser(user);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
