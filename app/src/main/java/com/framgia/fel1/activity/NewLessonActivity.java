package com.framgia.fel1.activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.NewLessonAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Result;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.ReadJson;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class NewLessonActivity extends Activity implements View.OnClickListener,
        NewLessonAdapter.OnListWordsClickItem {
    public static String mReadWord;
    private List<Word> mListWordNewLesson = new ArrayList<>();
    private NewLessonAdapter mNewLessonAdapter;
    private RecyclerView mListViewWordNewLesson;
    private MySqliteHelper mMySqliteHelper;
    private TextToSpeech mTextToSpeech;
    private TextView mTextNameNewLess;
    private Button mButtonSubmit;
    private Button mButtonCancel;
    private String mNameCategory;
    private ReadJson mReadJson;
    private String mAuthToken;
    private int mCountLesson;
    private Lesson mLesson;
    private Result mResult;
    private int mPerPage;
    private int mPage;
    private User mUser;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadWord = this.getString(R.string.no_data_to_speech);
        setContentView(R.layout.new_lesson_layout);
        initView();
        initData();
    }

    private void initView() {
        mMySqliteHelper = new MySqliteHelper(this);
        mButtonSubmit = (Button) findViewById(R.id.button_submit);
        mButtonCancel = (Button) findViewById(R.id.button_cancel);
        mTextNameNewLess = (TextView) findViewById(R.id.text_name_new_lesson);
        mListViewWordNewLesson = (RecyclerView) findViewById(R.id.listview_word_new_lesson);
        mNewLessonAdapter = new NewLessonAdapter(this, mListWordNewLesson);
        mListViewWordNewLesson.setAdapter(mNewLessonAdapter);
        mListViewWordNewLesson.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mButtonSubmit.setOnClickListener(this);
        mButtonCancel.setOnClickListener(this);
        mTextToSpeech = new TextToSpeech(NewLessonActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mTextToSpeech.setLanguage(Locale.US);
                }
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        //mUser = (User) intent.getSerializableExtra(Const.USER);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if(id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else finish();
        mAuthToken = intent.getStringExtra(Const.AUTH_TOKEN);
        mNameCategory = intent.getStringExtra(Const.NAME);
        mPage = Integer.parseInt(intent.getStringExtra(APIService.PAGE));
        mPerPage = Integer.parseInt(intent.getStringExtra(APIService.PER_PAGE));
        List<Lesson> lessonsList = new ArrayList<>();
        try {
            lessonsList = mMySqliteHelper.getListLesson();
        } catch (SQLiteException e) {
            Toast.makeText(this, R.string.err_cannot_read_list_lesson, Toast.LENGTH_SHORT).show();
        }
        mCountLesson = lessonsList.size();
        if (mCountLesson > Const.COUNT_LESSON) {
            Toast.makeText(NewLessonActivity.this, R.string.hetbai, Toast.LENGTH_SHORT).show();
        } else {
            createLesson(mCountLesson);
        }
    }

    private void createLesson(int i) {
        mReadJson = new ReadJson(this);
        if (mCountLesson > Const.COUNT_LESSON) {
            Toast.makeText(NewLessonActivity.this, R.string.hetbai, Toast.LENGTH_SHORT).show();
        } else {
            if (mPage * mPerPage >= Const.MAX_COUNT_WORDS) {
                Toast.makeText(NewLessonActivity.this, R.string.thieu_word, Toast.LENGTH_SHORT).show();
                finish();
//            return;
            } else {
                try {
                    mLesson = mReadJson.createLesson(i, mPage, mPerPage);
                    mTextNameNewLess.setText(mLesson.getName());
                    mListWordNewLesson.addAll(mLesson.getWords());
                    mNewLessonAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                mCountLesson++;
                if(mCountLesson <= Const.COUNT_LESSON){
                    for (Word word : mLesson.getWords()) {
                        Result mResult = new Result(
                                mUser.getId(),
                                word.getLessonId(),
                                word.getId(),
                                word.getResultId());
                        mMySqliteHelper.addResult(mResult);
                    }
                    mMySqliteHelper.addLesson(mLesson);
                    for (Word word : mListWordNewLesson) {
                        mMySqliteHelper.addWord(word);
                        for (Answer answer : word.getAnswers()) {
                            mMySqliteHelper.addAnswer(answer);
                        }
                    }
                    mListWordNewLesson.clear();
                    mPage++;
                    createLesson(mCountLesson);
                }else{
                    Toast.makeText(NewLessonActivity.this, R.string.hetbai, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case R.id.button_cancel:
                finish();
                break;
            case R.id.text_content_word_new:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String utteranceId = this.hashCode() + "";
                    mTextToSpeech.speak(mReadWord, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                } else {
                    mTextToSpeech.speak(mReadWord, TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onListWordsClickItem(int position, Word word) {
        //TODO call activity
    }

}
