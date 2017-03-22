package com.framgia.fel1.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.UserActionAdapter;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.util.HttpRequest;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by PhongTran on 05/09/2016.
 */
public class UserActionActivity extends AppCompatActivity {
    private RecyclerView mRecyclerViewUserAction;
    private MySqliteHelper mMySqliteHelper;
    private User mUser;
    private List<UserActivity> mListActivities = new ArrayList<>();
    private UserActionAdapter mUserActionAdapter;
    private SharedPreferences mSharedPreferences;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activities_layout);
        initView();
        initData();
        new LoadActionUser().execute();
    }

    private void initData() {
        mListActivities.clear();
        mListActivities.addAll(mMySqliteHelper.getListUserActivity(mUser.getId()));
        Collections.reverse(mListActivities);
        mUserActionAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_lesson);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.activities);
        mMySqliteHelper = new MySqliteHelper(this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if (id == -1) {
            finish();
            return;
        }
        mUser = mMySqliteHelper.getUser(id);
        mRecyclerViewUserAction = (RecyclerView) findViewById(R.id.recycle_view_activities);
        mRecyclerViewUserAction.setLayoutManager(new LinearLayoutManager(UserActionActivity.this));
        mUserActionAdapter = new UserActionAdapter(this, mListActivities);
        mRecyclerViewUserAction.setAdapter(mUserActionAdapter);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class LoadActionUser extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = NetwordConst.URL_SHOW_USER + mUser.getId() + Const.JSON_TYPE + "?" +
                    Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
            String response = HttpRequest.getJSON(url);
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Toast.makeText(UserActionActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                     .show();
            } else if ((response.substring(0, response.indexOf(":")))
                    .contains(String.valueOf(R.string.Exception)) ||
                    (response.substring(0, response.indexOf(":")))
                            .contains(String.valueOf(R.string.StackTrace))) {
                Toast.makeText(UserActionActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                     .show();
            } else {
                try {
                    User user = new User(response);
                    mListActivities.clear();
                    mListActivities.addAll(user.getActivities());
                    Collections.reverse(mListActivities);
                    mUserActionAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
