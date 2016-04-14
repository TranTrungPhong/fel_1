package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private CheckBox mCheckBoxRememberMe;
    private Button mButtonLogin;
    private TextView mTextviewSignup;
    private ProgressDialog progressDialog;
    private int mCheckBoxRememMe = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initView();
    }

    private void initView() {
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
                //TODO call Activity SignUp
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
            progressDialog.setMessage(R.string.loading + "");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if(isCancelled()){
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
            }
        }
    }

}
