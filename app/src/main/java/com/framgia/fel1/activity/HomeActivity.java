package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.adapter.HomeAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.ArrayCategory;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import com.framgia.fel1.util.BitmapUtil;
import com.framgia.fel1.util.DividerItemDecoration;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ShowImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by PhongTran on 04/15/2016.
 */
public class HomeActivity extends Activity implements View.OnClickListener,
        HomeAdapter.OnListCategoryClickItem {
    private static final String TAG = "HomeActivity";
    private Button mButtonUpdate;
    private Button mButtonSignUp;
    private Button mButtonShowWordList;
    private Button mButtonShowActivity;
    private ImageView mImageViewAvatar;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private RecyclerView mRecyclerViewCategory;
    private HomeAdapter mHomeAdapter;
    private List<Category> mListCategory = new ArrayList<>();
    private String mAuthToken;
    private User mUser;
    private MySqliteHelper mMySqliteHelper;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        initView();
    }

    @Override
    protected void onResume() {
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(mUser.getAvatar(), 100, 100);
        if(bitmap != null)
            mImageViewAvatar.setImageBitmap(bitmap);
        super.onResume();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mButtonSignUp = (Button) findViewById(R.id.button_sign_out_show_user);
        mButtonUpdate = (Button) findViewById(R.id.button_update_show_user);
        mButtonShowWordList = (Button) findViewById(R.id.button_wordlist_show_user);
        mButtonShowActivity = (Button) findViewById(R.id.button_show_activities);
        mImageViewAvatar = (ImageView) findViewById(R.id.image_show_user_avatar);
        mTextViewName = (TextView) findViewById(R.id.text_show_user_name);
        mTextViewEmail = (TextView) findViewById(R.id.text_show_user_email);
        mRecyclerViewCategory = (RecyclerView) findViewById(R.id.listview_lesson_learned);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mHomeAdapter = new HomeAdapter(this, mListCategory);
        mRecyclerViewCategory.setAdapter(mHomeAdapter);
        mRecyclerViewCategory.addItemDecoration(new DividerItemDecoration(this,
                                                        DividerItemDecoration.VERTICAL_LIST,
                                                        R.drawable.divider_category_list));
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if(id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else
            finish();
        //Intent intent = getIntent();
        //mUser = (User) intent.getSerializableExtra(Const.USER);
        new ShowImage(mImageViewAvatar).execute(mUser.getAvatar());
        mTextViewName.setText(mUser.getName());
        mTextViewEmail.setText(mUser.getEmail());
        mAuthToken = mUser.getAuthToken();
        new LoadCategory().execute();
        mButtonUpdate.setOnClickListener(this);
        mButtonSignUp.setOnClickListener(this);
        mButtonShowWordList.setOnClickListener(this);
        mButtonShowActivity.setOnClickListener(this);
        mImageViewAvatar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update_show_user:
                Intent intentUpdate = new Intent(HomeActivity.this, UpdateProfileActivity.class);
                startActivity(intentUpdate);
                break;
            case R.id.button_sign_out_show_user:
                showSignOutDialog();
                break;
            case R.id.button_wordlist_show_user:
                Intent intentWordList = new Intent(HomeActivity.this, WordListActivity.class);
                startActivity(intentWordList);
                break;
            case R.id.button_show_activities:
                Intent intentActivities = new Intent(HomeActivity.this, UserActionActivity.class);
                startActivity(intentActivities);
                break;
            case R.id.image_show_user_avatar:
                Intent intent = new Intent(HomeActivity.this, UpdateProfileActivity.class);
                startActivity(intent);
            default:
                break;
        }

    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.infor)
                .setMessage(R.string.confirrn_signout)
                .setPositiveButton(R.string.thoat, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSignOut();

                    }
                })
                .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void onSignOut() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Const.REMEMBER, false);
        editor.remove(Const.ID);
        editor.apply();
        if( InternetUtils.isInternetConnected(HomeActivity.this))
            new SignOutRequest().execute();
        else {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onListCategoryClickItem(int position, Category category) {
        Intent intentLessonLearned = new Intent(HomeActivity.this, LearnedLessonActivity.class);
        intentLessonLearned.putExtra(Const.AUTH_TOKEN, mAuthToken);
        intentLessonLearned.putExtra(Const.ID, mListCategory.get(position).getId());
        intentLessonLearned.putExtra(Const.NAME, mListCategory.get(position).getName());
        intentLessonLearned.putExtra(Const.USER, mUser);
        startActivity(intentLessonLearned);
    }

    @Override
    public void onBackPressed() {
        if (mToast == null)
            mToast = Toast.makeText(HomeActivity.this, R.string.press_back_again_to_exit,
                    Toast.LENGTH_SHORT);
        if (mToast.getView().isShown()) {
            super.onBackPressed();
        } else {
            mToast.show();
        }
    }

    private class LoadCategory extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = APIService.URL_GET_CATEGORY + "?" + Const.AUTH_TOKEN + "=" + mAuthToken;
            String response = HttpRequest.getJSON(url);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            //Log.d(TAG, response);
            if (response == null) {
                Toast.makeText(HomeActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                        .show();
            } else if ((response.substring(0, response.indexOf(":")))
                    .contains(String.valueOf(R.string.Exception))
                    || (response.substring(0, response.indexOf(":")))
                    .contains(String.valueOf(R.string.StackTrace))) {
                Toast.makeText(HomeActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(HomeActivity.this, R.string.response_done, Toast.LENGTH_SHORT).show();
                try {
                    ArrayCategory arrayCategory = new ArrayCategory(response);
                    mListCategory.clear();
                    mListCategory.addAll(arrayCategory.getCategoryList());
                    mHomeAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
     private class SignOutRequest extends AsyncTask<String, String, String> {
         ProgressDialog progressDialog;
         @Override
         protected void onPreExecute() {
             progressDialog = new ProgressDialog(HomeActivity.this);
             progressDialog.setTitle(getResources().getString(R.string.sign_out));
             progressDialog.show();
         }

         @Override
            protected String doInBackground(String... params) {
                String url = APIService.URL_API_SIGNOUT + "?" +
                        Const.AUTH_TOKEN + "=" +
                        mUser.getAuthToken();
             String response = null;
             try {
                 response = HttpRequest.postJsonRequest(url, null, APIService.METHOD_DELETE);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return response;
            }

            @Override
            protected void onPostExecute(String response) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                if ( response == null ) {
                    Toast.makeText(HomeActivity.this, R.string.response_null, Toast.LENGTH_SHORT).show();
                } else if ( (response.substring(0, response.indexOf(":"))).contains(
                        String.valueOf(R.string.Exception)) || (response.substring(0, response.indexOf(":"))).contains(
                        String.valueOf(R.string.StackTrace)) ) {
                    Toast.makeText(HomeActivity.this, R.string.response_error, Toast.LENGTH_SHORT).show();
                } else {
                    String responseInvalid = null;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        responseInvalid = jsonObject.optString(getString(R.string.message_invalid));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ( ! responseInvalid.equals("") ) {
                        Toast.makeText(HomeActivity.this, responseInvalid, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(HomeActivity.this, R.string.error_sign_out, Toast
                                .LENGTH_SHORT)
                                .show();
                    }
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }

}
