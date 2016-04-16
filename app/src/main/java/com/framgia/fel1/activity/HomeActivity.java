package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.adapter.CategoryAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.ArrayCategory;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import com.framgia.fel1.util.HttpRequest;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by PhongTran on 04/15/2016.
 */
public class HomeActivity extends Activity implements View.OnClickListener {
    private Button mButtonUpdate;
    private Button mButtonSignUp;
    private ImageView mImageViewAvatar;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private ListView mListViewCategory;
    private CategoryAdapter mCategoryAdapter;
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
        mListViewCategory = (ListView) findViewById(R.id.listview_lesson_learned);
        mCategoryAdapter = new CategoryAdapter(this, mListCategory);
        mListViewCategory.setAdapter(mCategoryAdapter);

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

                    }
                }).show();
    }

    public class ShowImage extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;
        boolean hasError = false;

        public ShowImage(ImageView imageView) {
            this.mImageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                hasError = true;
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (!hasError) {
                mImageView.setImageBitmap(result);
            }
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
            Log.d("PPPPPPPPP", response);
            if (response == null) {
                Toast.makeText(HomeActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                        .show();
            } else if ((response.substring(0, response.indexOf(":"))).contains(String.valueOf(R.string.Exception)) ||
                    (response.substring(0, response.indexOf(":"))).contains(String.valueOf(R.string.StackTrace))) {
                Toast.makeText(HomeActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(HomeActivity.this, R.string.response_done, Toast.LENGTH_SHORT).show();
                try {
                    ArrayCategory arrayCategory = new ArrayCategory(response);
                    mListCategory.clear();
                    mListCategory.addAll(arrayCategory.getCategoryList());
                    mCategoryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
