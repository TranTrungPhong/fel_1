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
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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
import com.framgia.fel1.util.TaskFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by PhongTran on 04/15/2016.
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener,
        HomeAdapter.OnListCategoryClickItem, TaskFragment.TaskCallbacks {
    private static final String TAG = "HomeActivity";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String LOADCATEGORY_TAG = "load_category_tag";
    private static final String SIGOUT_TAG = "sig_out_tag";
    private static final String SHOWIMAGE_TAG = "show_image_tag";
    private static final String ISCATEGORY = "ISCATEGORY";
    private static final String CONTENT_BITMAP = "bitmap";
    private static String GET_TAG = LOADCATEGORY_TAG;
    private TaskFragment mTaskFragment;
    private Button mButtonUpdate;
    private Button mButtonSignUp;
    private Button mButtonShowWordList;
    private Button mButtonShowActivity;
    private ImageView mImageViewAvatar;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private RecyclerView mRecyclerViewCategory;
    private HomeAdapter mHomeAdapter;
    private ArrayList<Category> mListCategory = new ArrayList<>();
    private String mAuthToken;
    private User mUser;
    private MySqliteHelper mMySqliteHelper;
    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private ProgressDialog progressDialog;
    private static boolean isCategoryLoad = false;
    private static boolean isAvatar = false;
    private Bundle mBundle = new Bundle();
    private boolean isLoadImage = false;
    private Bitmap mBitmapAvatar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mBundle = savedInstanceState;
        setContentView(R.layout.home_screen_layout);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            mTaskFragment.onAttach((Context) this);
        }
        initView();
    }

    @Override
    protected void onResume() {
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(mUser.getAvatar(), 100, 100);
        if (bitmap != null)
            mImageViewAvatar.setImageBitmap(bitmap);
        super.onResume();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mProgressDialog = new ProgressDialog(HomeActivity.this);
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
        if (id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else
            finish();
        if (!isLoadImage)
            if (InternetUtils.isInternetConnected(HomeActivity.this, false)) {
                isLoadImage = !isLoadImage;
                if (mUser.getAvatar() != null || !mUser.getAvatar().equals("")) {
                    new ShowImage(mImageViewAvatar).execute(mUser.getAvatar());
                }
            }
        mTextViewName.setText(mUser.getName());
        mTextViewEmail.setText(mUser.getEmail());
        mAuthToken = mUser.getAuthToken();
        GET_TAG = LOADCATEGORY_TAG;
        if (!isCategoryLoad) {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.loading));
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }).start();
            mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
        }
        if (!mBundle.getBoolean(ISCATEGORY, false)) {
        }
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
                isCategoryLoad = false;
                startActivity(intentUpdate);
                isCategoryLoad = false;
                break;
            case R.id.button_sign_out_show_user:
                showSignOutDialog();
                break;
            case R.id.button_wordlist_show_user:
                Intent intentWordList = new Intent(HomeActivity.this, WordListActivity.class);
                isCategoryLoad = false;
                startActivity(intentWordList);
                isCategoryLoad = false;
                break;
            case R.id.button_show_activities:
                Intent intentActivities = new Intent(HomeActivity.this, UserActionActivity.class);
                isCategoryLoad = false;
                startActivity(intentActivities);
                isCategoryLoad = false;
                break;
            case R.id.image_show_user_avatar:
                Intent intent = new Intent(HomeActivity.this, UpdateProfileActivity.class);
                isCategoryLoad = false;
                startActivity(intent);
                isCategoryLoad = false;
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
        if (InternetUtils.isInternetConnected(HomeActivity.this)) {
            GET_TAG = SIGOUT_TAG;
            mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
        } else {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            isCategoryLoad = false;
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
        mSharedPreferences =
                getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Const.CATEGORY_ID, mListCategory.get(position).getId());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        if (mToast == null)
            mToast = Toast.makeText(HomeActivity.this, R.string.press_back_again_to_exit,
                    Toast.LENGTH_SHORT);
        if (mToast.getView().isShown()) {
            super.onBackPressed();
            isCategoryLoad = false;
        } else {
            mToast.show();
        }
    }

    @Override
    public void onPreExecute() {
        if (GET_TAG == SIGOUT_TAG) {
            mProgressDialog.setTitle(getResources().getString(R.string.sign_out));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

    }

    @Override
    public String onBackGround(String[] param) {
        String response = null;
        if (GET_TAG == LOADCATEGORY_TAG) {
            String url = APIService.URL_GET_CATEGORY + "?" + Const.AUTH_TOKEN + "=" + mAuthToken;
            response = HttpRequest.getJSON(url);
            return response;
        } else if (GET_TAG == SIGOUT_TAG) {
            String url = APIService.URL_API_SIGNOUT + "?" +
                    Const.AUTH_TOKEN + "=" +
                    mUser.getAuthToken();
            try {
                response = HttpRequest.postJsonRequest(url, null, APIService.METHOD_DELETE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        return response;
    }

    @Override
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String response) {
        if (GET_TAG == LOADCATEGORY_TAG) {
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
                isCategoryLoad = true;
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
        } else if (GET_TAG == SIGOUT_TAG) {
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            if (response == null) {
                Toast.makeText(HomeActivity.this, R.string.response_null, Toast.LENGTH_SHORT).show();
            } else if ((response.substring(0, response.indexOf(":"))).contains(
                    String.valueOf(R.string.Exception)) || (response.substring(0, response.indexOf(":"))).contains(
                    String.valueOf(R.string.StackTrace))) {
                Toast.makeText(HomeActivity.this, R.string.response_error, Toast.LENGTH_SHORT).show();
            } else {
                String responseInvalid = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    responseInvalid = jsonObject.optString(getString(R.string.message_invalid));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!responseInvalid.equals("")) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.CONTENT_LOADING, mProgressDialog.isShowing());
        outState.putBoolean(ISCATEGORY, isCategoryLoad);
        outState.putSerializable(Const.LIST, mListCategory);
        if (isLoadImage)
            mBitmapAvatar = ((BitmapDrawable) mImageViewAvatar.getDrawable()).getBitmap();
        if (mBitmapAvatar != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mBitmapAvatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            outState.putByteArray(CONTENT_BITMAP, byteArray);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(Const.CONTENT_LOADING)) {
            mProgressDialog.setMessage(getResources().getString(R.string.loading));
            mProgressDialog.show();
        }
        mListCategory.clear();
        mListCategory.addAll((ArrayList<Category>) savedInstanceState.getSerializable(Const.LIST));
        byte[] byteArray = savedInstanceState.getByteArray(CONTENT_BITMAP);
        if (byteArray != null)
            mBitmapAvatar = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (mBitmapAvatar != null)
            mImageViewAvatar.setImageBitmap(mBitmapAvatar);
    }

}
