package com.framgia.fel1.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.NewLessonAdapter;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Result;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.TaskFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by PhongTran on 05/02/2016.
 */
public class LessonActivity extends AppCompatActivity
        implements View.OnClickListener, TaskFragment.TaskCallbacks {

    public static String mReadWord;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String CREATE_LESSON_TAG = "create_tag";
    private static final String UPDATE_LESSON_TAG = "update_tag";
    private static String GET_TAG = CREATE_LESSON_TAG;
    private TaskFragment mTaskFragment;
    private ArrayList<Word> mListWordNewLesson = new ArrayList<>();
    private NewLessonAdapter mNewLessonAdapter;
    private RecyclerView mListViewWordNewLesson;
    private MySqliteHelper mMySqliteHelper;
    private TextToSpeech mTextToSpeech;
    private TextView mTextNameNewLess;
    private String mNameCategory;
    private String mAuthToken;
    private Lesson mLesson;
    private int mCategoryId;
    private User mUser;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private List<String> mListResuiltId = new ArrayList<>();
    public static boolean isLessonLoad = false;
    private FloatingActionButton mFabSubmit;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadWord = this.getString(R.string.no_data_to_speech);
        setContentView(R.layout.new_lesson_layout);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            mTaskFragment.onAttach((Context) this);
        }
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        mTextToSpeech = new TextToSpeech(LessonActivity.this, new TextToSpeech.OnInitListener() {
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
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isLessonLoad = false;
    }

    private void initView() {
        SharedPreferences mSharedPreferences =
                getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(Const.ANSWER_TAG, 1);
        editor.apply();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mFabSubmit = (FloatingActionButton) findViewById(R.id.fab_submit);
        mFabSubmit.setImageDrawable(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_check)
                                                             .sizeRes(R.dimen.icon_size)
                                                             .color(getResources()
                                                                     .getColor(R.color.colorIcon)));
        progressDialog = new ProgressDialog(LessonActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        mMySqliteHelper = new MySqliteHelper(this);
        mTextNameNewLess = (TextView) findViewById(R.id.text_name_new_lesson);
        mListViewWordNewLesson = (RecyclerView) findViewById(R.id.listview_word_new_lesson);
        mNewLessonAdapter = new NewLessonAdapter(this, mListWordNewLesson);
        mListViewWordNewLesson.setAdapter(mNewLessonAdapter);
        mListViewWordNewLesson.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mFabSubmit.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mCategoryId = intent.getIntExtra(Const.CATEGORY_ID, -1);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if (id != -1) mUser = mMySqliteHelper.getUser(id);
        else finish();
        mAuthToken = intent.getStringExtra(Const.AUTH_TOKEN);
        mNameCategory = intent.getStringExtra(Const.NAME);
        if (!isLessonLoad) {
            GET_TAG = CREATE_LESSON_TAG;
            if (InternetUtils.isInternetConnected(LessonActivity.this)) {
                mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_submit:
                mFabSubmit.setEnabled(false);
                SharedPreferences mSharedPreferences =
                        getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putInt(Const.ANSWER_TAG, 1);
                editor.apply();
                for (Word word : mLesson.getWords()) {
                    Result mResult = new Result(mUser.getId(), word.getLessonId(), word.getId(),
                            word.getResultId());
                    try {
                        mMySqliteHelper.addResult(mResult);
                    } catch (SQLiteConstraintException e) {
                        e.printStackTrace();
                        mMySqliteHelper.updateResult(mResult);
                    }
                }
                try {
                    mMySqliteHelper.addLesson(mLesson);
                } catch (SQLiteConstraintException e) {
                    e.printStackTrace();
                    mMySqliteHelper.updateLesson(mLesson);
                }
                for (Word word : mListWordNewLesson) {
                    try {
                        mMySqliteHelper.addWord(word);
                    } catch (SQLiteConstraintException e) {
                        e.printStackTrace();
                        mMySqliteHelper.updateWord(word);
                    }
                    for (Answer answer : word.getAnswers()) {
                        try {
                            mMySqliteHelper.addAnswer(answer);
                        } catch (SQLiteConstraintException e) {
                            e.printStackTrace();
                            mMySqliteHelper.updateAnswer(answer);
                        }
                    }
                }
                GET_TAG = UPDATE_LESSON_TAG;
                if (InternetUtils.isInternetConnected(LessonActivity.this)) {
                    mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
                }
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
    public void onPreExecute() {
        if (progressDialog == null) return;
        if (!progressDialog.isShowing()) {
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    public String onBackGround(String[] param) {
        String response = null;
        switch (GET_TAG) {
            case CREATE_LESSON_TAG:
                String url =
                        NetwordConst.URL_CREATE_LESSION + mCategoryId + Const.PATH_LESSON + "?" +
                                Const.AUTH_TOKEN + "=" +
                                mUser.getAuthToken();
                try {
                    response = HttpRequest.postJsonRequest(url, null, NetwordConst.METHOD_POST);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            case UPDATE_LESSON_TAG:
                JSONObject jsonArray = new JSONObject();
                for (int i = 0; i < mListWordNewLesson.size(); i++) {
                    JSONObject jsonObjectWord = new JSONObject();
                    try {
                        jsonObjectWord.put(Const.ID, String.valueOf(mListResuiltId.get(i)));
                        jsonObjectWord.put(Const.ANSWER_ID,
                                String.valueOf(mListWordNewLesson.get(i).getResultId()));
                        jsonArray.put(String.valueOf(i), jsonObjectWord);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObjectMid = new JSONObject();
                JSONObject jsonObjectPost = new JSONObject();
                try {
                    jsonObjectMid.put(Const.RESULT_ATTRIBUTES, jsonArray);
                    jsonObjectMid.put(Const.FILTER_LEARNED_WORDS, String.valueOf(true));
                    jsonObjectPost.put(Const.LESSON, jsonObjectMid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String urlPost = Const.URL_UPDATE_LESSON + mLesson.getId() + Const.JSON_TYPE + "?" +
                        Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
                try {
                    response = HttpRequest
                            .postJsonRequest(urlPost, jsonObjectPost, NetwordConst.METHOD_PATCH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            default:
                break;
        }
        return response;
    }

    @Override
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {
        return;
    }

    @Override
    public void onPostExecute(String s) {
        switch (GET_TAG) {
            case CREATE_LESSON_TAG:
                isLessonLoad = true;
                if (s == null) {
                    Toast.makeText(LessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                         .show();
                } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                        (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
                    Toast.makeText(LessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                         .show();
                } else {
                    try {
                        JSONObject jsonObjectUser = new JSONObject(s);
                        JSONObject response = jsonObjectUser.optJSONObject(Const.LESSON);
                        int idLesson = response.optInt(Const.ID);
                        String nameLesson = response.optString(Const.NAME);
                        JSONArray jsonArrayWord = response.optJSONArray(Const.WORDS);
                        List<Word> wordList = new ArrayList<>();
                        for (int i = 0; i < jsonArrayWord.length(); i++) {
                            JSONObject jsonWord = jsonArrayWord.optJSONObject(i);
                            int idWord = jsonWord.optInt(Const.ID);
                            int resultIdWord = jsonWord.optInt(Const.RESULT_ID);
                            mListResuiltId.add(String.valueOf(resultIdWord));
                            String contentWord = jsonWord.optString(Const.CONTENT);
                            JSONArray jsonArrayAnswer = jsonWord.optJSONArray(Const.ANSWERS);
                            List<Answer> answerList = new ArrayList<>();
                            for (int j = 0; j < jsonArrayAnswer.length(); j++) {
                                JSONObject jsonAnswer = jsonArrayAnswer.optJSONObject(j);
                                int idAnswer = jsonAnswer.optInt(Const.ID);
                                String contentAnswer = jsonAnswer.optString(Const.CONTENT);
                                boolean isCorrect = jsonAnswer.optBoolean(Const.IS_CORRECT);
                                Answer answer =
                                        new Answer(idAnswer, idWord, contentAnswer, isCorrect);
                                answerList.add(answer);
                            }
                            Word word = new Word(idWord, idLesson, resultIdWord, contentWord,
                                    answerList);
                            wordList.add(word);
                        }
                        mSharedPreferences =
                                getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
                        mCategoryId = mSharedPreferences.getInt(Const.CATEGORY_ID, -1);
                        mLesson = new Lesson(idLesson, mCategoryId, nameLesson, wordList);
                        mTextNameNewLess.setText(mLesson.getName());
                        mListWordNewLesson.clear();
                        mListWordNewLesson.addAll(mLesson.getWords());
                        mNewLessonAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case UPDATE_LESSON_TAG:
                if (s == null) {
                    Toast.makeText(LessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                         .show();
                } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                        (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
                    Toast.makeText(LessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                         .show();
                } else {
                    Toast.makeText(LessonActivity.this, R.string.update_done, Toast.LENGTH_SHORT)
                         .show();
                }

                //                mListWordNewLesson.clear();
                //                GET_TAG = CREATE_LESSON_TAG;
                //                if (InternetUtils.isInternetConnected(LessonActivity.this)) {
                //                    mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
                //                }
                //                mFabSubmit.setEnabled(true);
                break;
            default:
                break;
        }
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.CONTENT_LOADING, progressDialog.isShowing());
        outState.putBoolean(Const.ISLESSON, isLessonLoad);
        outState.putSerializable(Const.LIST, mListWordNewLesson);
        outState.putSerializable(Const.LESSON, mLesson);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getBoolean(Const.CONTENT_LOADING)) {
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }
        mLesson = (Lesson) savedInstanceState.getSerializable(Const.LESSON);
        mTextNameNewLess.setText(mLesson.getName());
        mListWordNewLesson.clear();
        mListWordNewLesson.addAll((ArrayList<Word>) savedInstanceState.getSerializable(Const.LIST));
    }
}

