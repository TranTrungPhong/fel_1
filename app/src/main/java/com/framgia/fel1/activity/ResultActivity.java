package com.framgia.fel1.activity;

import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.ResultAdapter;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.DividerItemDecoration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity
        implements ResultAdapter.OnListFragmentInteractionListener {
    public static final String TAG = "ResultActivity";
    private TextView mTextResult;
    private RecyclerView mRecyclerView;
    private ResultAdapter mResultAdapter;
    private List<Word> mWordList;
    private Toolbar mToolbar;
    private Intent mData;
    private Lesson mLesson;
    private MySqliteHelper mSqliteHelper;
    private TextToSpeech mTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        setView();
        setData();
        setEvent();
    }

    @Override
    protected void onResume() {
        mTextToSpeech = new TextToSpeech(ResultActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.US);
                }
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void setView() {
        mTextResult = (TextView) findViewById(R.id.text_result);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mData = getIntent();
        mLesson = (Lesson) mData.getSerializableExtra(Const.LESSON);
        mWordList = new ArrayList<>();
        mResultAdapter = new ResultAdapter(ResultActivity.this, mLesson, mWordList);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        mRecyclerView.setAdapter(mResultAdapter);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
//                                                                  DividerItemDecoration.VERTICAL_LIST,
//                                                                  R.drawable.divider_word_list));
    }

    private void setData() {
        if ( mLesson != null ) {
            mWordList.clear();
            mSqliteHelper = new MySqliteHelper(ResultActivity.this);
            mWordList.addAll(mSqliteHelper.getListWordByLesson(mLesson.getId()));
            mResultAdapter.notifyDataSetChanged();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getResources().getString(R.string.corrected))
                    .append(mResultAdapter.getCountCorrect())
                    .append(getResources().getString(R.string.words));
            mTextResult.setText(stringBuilder.toString());
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
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String utteranceId = this.hashCode() + "";
            mTextToSpeech.speak(word.getContent(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            mTextToSpeech.speak(word.getContent(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
