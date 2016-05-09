package com.framgia.fel1.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.adapter.UserActionAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.ArrayCategory;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.util.HttpRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongTran on 05/09/2016.
 */
public class UserActionActivity extends Activity {
    private Button mButtonBack;
    private RecyclerView mRecyclerViewUserAction;
    private TextView mTextViewNameUser;
    private TextView mTextViewEmailUser;
    private MySqliteHelper mMySqliteHelper;
    private User mUser;
    private List<UserActivity> mListActivities = new ArrayList<>();
    private UserActionAdapter mUserActionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activities_layout);
        initView();
        initData();
    }

    private void initData() {
        mListActivities = mUser.getActivities();
        mUserActionAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mUser = mMySqliteHelper.getUser();
        mButtonBack = (Button) findViewById(R.id.button_back);
        mRecyclerViewUserAction = (RecyclerView) findViewById(R.id.recycle_view_activities);
        mRecyclerViewUserAction.setLayoutManager(new LinearLayoutManager(UserActionActivity.this));
        mTextViewNameUser = (TextView) findViewById(R.id.text_name_user);
        mTextViewEmailUser = (TextView) findViewById(R.id.text_email_user);
        mTextViewNameUser.setText(mUser.getName().toString());
        mTextViewEmailUser.setText(mUser.getEmail().toString());
        mUserActionAdapter = new UserActionAdapter(this, mListActivities);
        mRecyclerViewUserAction.setAdapter(mUserActionAdapter);
        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
