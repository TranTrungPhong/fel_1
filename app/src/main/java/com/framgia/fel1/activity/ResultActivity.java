package com.framgia.fel1.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.ResultAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Word;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity
        implements ResultAdapter.OnListFragmentInteractionListener {
    public static final String TAG = "ResultActivity";
    private RecyclerView mRecyclerView;
    private ResultAdapter mResultAdapter;
    private List<Word> mWordList;
    private Toolbar mToolbar;
    private Intent mData;
    private Lesson mLesson;
    private MySqliteHelper mSqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setView();
        setData();
        setEvent();
    }

    private void setView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWordList = new ArrayList<>();
        mResultAdapter = new ResultAdapter(ResultActivity.this, mWordList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        mRecyclerView.setAdapter(mResultAdapter);
    }

    private void setData() {
        mData = getIntent();
        mLesson = (Lesson) mData.getSerializableExtra(Const.LESSON);
        if ( mLesson != null ) {
            mWordList.clear();
            mSqliteHelper = new MySqliteHelper(ResultActivity.this);
            mWordList.addAll(mSqliteHelper.getListWordByLesson(mLesson.getId()));
            mResultAdapter.notifyDataSetChanged();
        }

    }

    private void setEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onClickSpeakListener(int position, Word word) {
        //TODO: Speaking word
    }
}
