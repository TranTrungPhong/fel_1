package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.framgia.fel1.model.ArrayCategory;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.ShowImage;

import org.json.JSONException;

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
    private ImageView mImageViewAvatar;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private RecyclerView mRecyclerViewCategory;
    private HomeAdapter mHomeAdapter;
    private List<Category> mListCategory = new ArrayList<>();
    private String mAuthToken;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        initView();
    }

    private void initView() {
        mButtonSignUp = (Button) findViewById(R.id.button_sign_out_show_user);
        mButtonUpdate = (Button) findViewById(R.id.button_update_show_user);
        mImageViewAvatar = (ImageView) findViewById(R.id.image_show_user_avatar);
        mTextViewName = (TextView) findViewById(R.id.text_show_user_name);
        mTextViewEmail = (TextView) findViewById(R.id.text_show_user_email);
        mRecyclerViewCategory = (RecyclerView) findViewById(R.id.listview_lesson_learned);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mHomeAdapter = new HomeAdapter(this, mListCategory);
        mRecyclerViewCategory.setAdapter(mHomeAdapter);
        Intent intent = getIntent();
        mUser = (User) intent.getSerializableExtra(Const.USER);
        new ShowImage(mImageViewAvatar).execute(mUser.getAvatar());
        mTextViewName.setText(mUser.getName());
        mTextViewEmail.setText(mUser.getEmail());
        mAuthToken = mUser.getAuthToken();
        new LoadCategory().execute();
        mButtonUpdate.setOnClickListener(this);
        mButtonSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_update_show_user:
                //TODO call activity aupdate user
                break;
            case R.id.button_sign_out_show_user:
                showSignOutDialog();
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
                        finish();
                    }
                })
                .setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onListCategoryClickItem(int position, Category category) {
        Intent intentLessonLearned = new Intent(HomeActivity.this, LearnedLessonActivity.class);
        intentLessonLearned.putExtra(Const.AUTH_TOKEN, mAuthToken);
        intentLessonLearned.putExtra(Const.ID, mListCategory.get(position).getId());
        intentLessonLearned.putExtra(Const.NAME, mListCategory.get(position).getName());
        startActivity(intentLessonLearned);
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
            Log.d(TAG, response);
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

}
