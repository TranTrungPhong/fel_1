package com.framgia.fel1.activity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.NewLessonAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Result;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.UserActivity;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ReadJson;
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
public class NewLessonActivity extends AppCompatActivity implements View.OnClickListener,
        NewLessonAdapter.OnListWordsClickItem, TaskFragment.TaskCallbacks {
    public static String mReadWord;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String CREATE_LESSON_TAG = "create_tag";
    private static final String UPDATE_LESSON_TAG = "update_tag";
    private static String GET_TAG = CREATE_LESSON_TAG;
    private TaskFragment mTaskFragment;
    private ArrayList<Word> mListWordNewLesson = new ArrayList<>();
    private ArrayList<Word> mListWordAns = new ArrayList<>();
    private NewLessonAdapter mNewLessonAdapter;
    private RecyclerView mListViewWordNewLesson;
    private MySqliteHelper mMySqliteHelper;
    private TextToSpeech mTextToSpeech;
    private TextView mTextNameNewLess;
//    private Button mButtonSubmit;
//    private Button mButtonCancel;
    private String mNameCategory;
    private ReadJson mReadJson;
    private String mAuthToken;
    private int mCountLesson;
    private Lesson mLesson;
    private Result mResult;
    private int mPerPage;
    private int mPage;
    private int mCategoryId;
    private User mUser;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog progressDialog;
    private List<String> mListResuiltId = new ArrayList<>();
    private static final String ISLESSON = "ISLESSON";
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
            if ( mTaskFragment == null ) {
                mTaskFragment = new TaskFragment();
                fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
                mTaskFragment.onAttach((Context) this);
            }
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        mTextToSpeech = new TextToSpeech(NewLessonActivity.this, new TextToSpeech.OnInitListener() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isLessonLoad = false;
    }

    private void initView() {
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
                                            .color(getResources().getColor(R.color.colorIcon)));
        progressDialog = new ProgressDialog(NewLessonActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        mMySqliteHelper = new MySqliteHelper(this);
//        mButtonSubmit = (Button) findViewById(R.id.button_submit);
//        mButtonCancel = (Button) findViewById(R.id.button_cancel);
        mTextNameNewLess = (TextView) findViewById(R.id.text_name_new_lesson);
        mListViewWordNewLesson = (RecyclerView) findViewById(R.id.listview_word_new_lesson);
        mNewLessonAdapter = new NewLessonAdapter(this, mListWordNewLesson);
        mListViewWordNewLesson.setAdapter(mNewLessonAdapter);
        mListViewWordNewLesson.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        mButtonSubmit.setOnClickListener(this);
//        mButtonCancel.setOnClickListener(this);
        mFabSubmit.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mCategoryId = intent.getIntExtra(Const.CATEGORY_ID,-1);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if(id != -1)
            mUser = mMySqliteHelper.getUser(id);
        else finish();
        mAuthToken = intent.getStringExtra(Const.AUTH_TOKEN);
        mNameCategory = intent.getStringExtra(Const.NAME);
//        mPage = Integer.parseInt(intent.getStringExtra(APIService.PAGE));
//        mPerPage = Integer.parseInt(intent.getStringExtra(APIService.PER_PAGE));
//        List<Lesson> lessonsList = new ArrayList<>();
//        try {
//            lessonsList = mMySqliteHelper.getListLesson();
//        } catch (SQLiteException e) {
//            Toast.makeText(this, R.string.err_cannot_read_list_lesson, Toast.LENGTH_SHORT).show();
//        }
//        mCountLesson = lessonsList.size();
//        if (mCountLesson > Const.COUNT_LESSON) {
//            Toast.makeText(NewLessonActivity.this, R.string.hetbai, Toast.LENGTH_SHORT).show();
//        } else {
//            //createLesson(mCountLesson);
//        }
//        new CreateNewLesson().execute();
        if(!isLessonLoad){
            GET_TAG = CREATE_LESSON_TAG;
            if (InternetUtils.isInternetConnected(NewLessonActivity.this)) {
                mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
            }
        }
    }

//    private void createLesson(int i) {
//        mReadJson = new ReadJson(this);
//        if (mCountLesson > Const.COUNT_LESSON) {
//            Toast.makeText(NewLessonActivity.this, R.string.hetbai, Toast.LENGTH_SHORT).show();
//        } else {
//            if (mPage * mPerPage >= Const.MAX_COUNT_WORDS) {
//                Toast.makeText(NewLessonActivity.this, R.string.thieu_word, Toast.LENGTH_SHORT).show();
//                finish();
////            return;
//            } else {
//                try {
//                    mLesson = mReadJson.createLesson(i, mPage, mPerPage);
//                    mTextNameNewLess.setText(mLesson.getName());
//                    mListWordNewLesson.addAll(mLesson.getWords());
//                    mNewLessonAdapter.notifyDataSetChanged();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_submit:
//                mCountLesson++;
//                if(mCountLesson <= Const.COUNT_LESSON){
                Log.i("FFFFFFFFFFFFFFF","Category ID : "+mLesson.getmIdCategory());
                    for (Word word : mLesson.getWords()) {
                        Result mResult = new Result(
                                mUser.getId(),
                                word.getLessonId(),
                                word.getId(),
                                word.getResultId());
                        try {
                            mMySqliteHelper.addResult(mResult);
                        } catch (SQLiteConstraintException e){
                            e.printStackTrace();
                            mMySqliteHelper.updateResult(mResult);
                        }
                    }
                    try {
                        mMySqliteHelper.addLesson(mLesson);
                    } catch (SQLiteConstraintException e){
                        e.printStackTrace();
                        mMySqliteHelper.updateLesson(mLesson);
                    }
                    for (Word word : mListWordNewLesson) {
                        try {
                            mMySqliteHelper.addWord(word);
                        } catch (SQLiteConstraintException e){
                            e.printStackTrace();
                            mMySqliteHelper.updateWord(word);
                        }
                        for (Answer answer : word.getAnswers()) {
                            try {
                                mMySqliteHelper.addAnswer(answer);
                            } catch (SQLiteConstraintException e){
                                e.printStackTrace();
                                mMySqliteHelper.updateAnswer(answer);
                            }
                        }
                    }
                GET_TAG = UPDATE_LESSON_TAG;
                if (InternetUtils.isInternetConnected(NewLessonActivity.this)) {
                    mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
                }
                break;
//            case R.id.button_cancel:
//                isLessonLoad = false;
//                finish();
//                onCancelled();
//                break;
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

    @Override
    public void onPreExecute() {
        switch (GET_TAG){
            case CREATE_LESSON_TAG:
                if(!progressDialog.isShowing()){
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
                break;
            case UPDATE_LESSON_TAG:
                progressDialog.setCancelable(false);
                progressDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public String onBackGround(String[] param) {
        String response = null;
        switch (GET_TAG){
            case CREATE_LESSON_TAG:
                String url = "https://manh-nt.herokuapp.com/categories/" +mCategoryId+"/lessons.json"+ "?" +
                        Const.AUTH_TOKEN + "=" +
                        mUser.getAuthToken();
                try {
                    response = HttpRequest.postJsonRequest(url, null, APIService.METHOD_POST);
                    Log.i("Response create : " , response);
                    Log.i("Response create url: " , url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            case UPDATE_LESSON_TAG:
                mListWordAns = mNewLessonAdapter.getListWordAnswer();
                JSONObject jsonArray = new JSONObject();
                for (int i = 0; i < mListWordNewLesson.size(); i++) {
                    JSONObject jsonObjectWord = new JSONObject();
                    try {
                        jsonObjectWord.put(Const.ID, String.valueOf(mListResuiltId.get(i)));
                        Log.i("AAAAAAAAA",mListResuiltId.get(i)+"");
                        jsonObjectWord.put(Const.ANSWER_ID, String.valueOf(mListWordAns.get(i).getResultId()));
                        Log.i("AAAAAAAAA","B : "+mListWordAns.get(i).getResultId()+"");
                        jsonArray.put(String.valueOf(i), jsonObjectWord);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObjectMid = new JSONObject();
                JSONObject jsonObjectPost = new JSONObject();
                try {
                    jsonObjectMid.put(Const.RESULT_ATTRIBUTES, jsonArray);
                    jsonObjectMid.put(Const.LEARNED, String.valueOf(true));
                    jsonObjectPost.put(Const.LESSON, jsonObjectMid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String urlPost = Const.URL_UPDATE_LESSON + mLesson.getId() + Const.JSON_TYPE + "?" +
                        Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
//                String response = null;
                Log.i("FFFFFFFFFFF",jsonObjectPost.toString());
                Log.i("FFFFFFFFFFF","url : "+urlPost);
                try {
                    response = HttpRequest.postJsonRequest(urlPost, jsonObjectPost, APIService.METHOD_PATCH);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
//                break;
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
        return ;
    }

    @Override
    public void onPostExecute(String s) {
        switch (GET_TAG){
            case CREATE_LESSON_TAG:
                isLessonLoad = true;
                if (s == null) {
                    Toast.makeText(NewLessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                            .show();
                } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                        (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
                    Toast.makeText(NewLessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(NewLessonActivity.this, R.string.response_done, Toast.LENGTH_SHORT).show();
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
                            Log.i("AAAAAAAAA",resultIdWord+"");
                            mListResuiltId.add(String.valueOf(resultIdWord));
                            String contentWord = jsonWord.optString(Const.CONTENT);
                            JSONArray jsonArrayAnswer = jsonWord.optJSONArray(Const.ANSWERS);
                            List<Answer> answerList = new ArrayList<>();
                            for (int j = 0; j < jsonArrayAnswer.length(); j++) {
                                JSONObject jsonAnswer = jsonArrayAnswer.optJSONObject(j);
                                int idAnswer = jsonAnswer.optInt(Const.ID);
                                String contentAnswer = jsonAnswer.optString(Const.CONTENT);
                                boolean isCorrect = jsonAnswer.optBoolean(Const.IS_CORRECT);
                                Answer answer = new Answer(idAnswer,idWord,contentAnswer,isCorrect);
                                answerList.add(answer);
                            }
                            Word word = new Word(idWord,idLesson,resultIdWord,contentWord,answerList);
                            wordList.add(word);
                        }
                        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
                        mCategoryId = mSharedPreferences.getInt(Const.CATEGORY_ID, -1);
                        mLesson = new Lesson(idLesson,mCategoryId,nameLesson,wordList);
                        mTextNameNewLess.setText(mLesson.getName());
                        mListWordNewLesson.clear();
                        mListWordNewLesson.addAll(mLesson.getWords());
                        mNewLessonAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                }
                }
                break;
            case UPDATE_LESSON_TAG:
                if (s == null) {
                    Toast.makeText(NewLessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
                            .show();
                } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
                        (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
                    Toast.makeText(NewLessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
                            .show();
                } else {
//                Toast.makeText(NewLessonActivity.this, s, Toast.LENGTH_LONG).show();
                    Toast.makeText(NewLessonActivity.this, R.string.update_done, Toast.LENGTH_SHORT)
                            .show();
                }
                mListWordNewLesson.clear();
                GET_TAG = CREATE_LESSON_TAG;
                if (InternetUtils.isInternetConnected(NewLessonActivity.this)) {
                    mTaskFragment.startInBackground(new String[]{TAG_TASK_FRAGMENT});
                }
                break;
            default:
                break;
        }
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Const.CONTENT_LOADING, progressDialog.isShowing());
        outState.putBoolean(ISLESSON,isLessonLoad);
        outState.putSerializable("list",mListWordNewLesson);
        outState.putSerializable("lesson",mLesson);
        outState.putSerializable("lesson_ans",mListWordAns);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean(Const.CONTENT_LOADING)) {
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.show();
        }
        mLesson = (Lesson)savedInstanceState.getSerializable("lesson");
        mTextNameNewLess.setText(mLesson.getName());
        mListWordNewLesson.clear();
        mListWordNewLesson.addAll((ArrayList<Word>)savedInstanceState.getSerializable("list"));
        mListWordAns.clear();
        mListWordAns.addAll((ArrayList<Word>)savedInstanceState.get("lesson_ans"));

    }

//    private class CreateNewLesson extends AsyncTask<String, String, String> {
////        String email = mEditTextEmail.getText().toString();
////        String password = mEditTextPassword.getText().toString();
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (!InternetUtils.isInternetConnected(NewLessonActivity.this)) {
//                cancel(true);
//            }
////            progressDialog = new ProgressDialog(NewLessonActivity.this);
////            progressDialog.setMessage(getResources().getString(R.string.loading));
//            if(!progressDialog.isShowing()){
//                progressDialog.show();
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            if (isCancelled()) {
//                return null;
//            }
//            String url = "https://manh-nt.herokuapp.com/categories/" +mCategoryId+"/lessons.json"+ "?" +
//                    Const.AUTH_TOKEN + "=" +
//                    mUser.getAuthToken();
//            String response = null;
//            try {
//                response = HttpRequest.postJsonRequest(url, null, APIService.METHOD_POST);
//                Log.i("Response create : " , response);
//                Log.i("Response create url: " , url);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if(progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
//            if (s == null) {
//                Toast.makeText(NewLessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
//                        .show();
//            } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
//                    (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
//                Toast.makeText(NewLessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
//                        .show();
//            } else {
//                    Toast.makeText(NewLessonActivity.this, R.string.response_done, Toast.LENGTH_SHORT).show();
//                    try {
//                        JSONObject jsonObjectUser = new JSONObject(s);
//                        JSONObject response = jsonObjectUser.optJSONObject(Const.LESSON);
//                        int idLesson = response.optInt(Const.ID);
//                        String nameLesson = response.optString(Const.NAME);
//                        JSONArray jsonArrayWord = response.optJSONArray(Const.WORDS);
//                        List<Word> wordList = new ArrayList<>();
//                        for (int i = 0; i < jsonArrayWord.length(); i++) {
//                            JSONObject jsonWord = jsonArrayWord.optJSONObject(i);
//                            int idWord = jsonWord.optInt(Const.ID);
//                            int resultIdWord = jsonWord.optInt(Const.RESULT_ID);
//                            Log.i("AAAAAAAAA",resultIdWord+"");
//                            mListResuiltId.add(String.valueOf(resultIdWord));
//                            String contentWord = jsonWord.optString(Const.CONTENT);
//                            JSONArray jsonArrayAnswer = jsonWord.optJSONArray(Const.ANSWERS);
//                            List<Answer> answerList = new ArrayList<>();
//                            for (int j = 0; j < jsonArrayAnswer.length(); j++) {
//                                JSONObject jsonAnswer = jsonArrayAnswer.optJSONObject(j);
//                                int idAnswer = jsonAnswer.optInt(Const.ID);
//                                String contentAnswer = jsonAnswer.optString(Const.CONTENT);
//                                boolean isCorrect = jsonAnswer.optBoolean(Const.IS_CORRECT);
//                                Answer answer = new Answer(idAnswer,idWord,contentAnswer,isCorrect);
//                                answerList.add(answer);
//                            }
//                            Word word = new Word(idWord,idLesson,resultIdWord,contentWord,answerList);
//                            wordList.add(word);
//                        }
//                        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
//                        mCategoryId = mSharedPreferences.getInt(Const.CATEGORY_ID, -1);
//                        mLesson = new Lesson(idLesson,mCategoryId,nameLesson,wordList);
//                        mTextNameNewLess.setText(mLesson.getName());
//                        mListWordNewLesson.clear();
//                        mListWordNewLesson.addAll(mLesson.getWords());
//                        mNewLessonAdapter.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
////                }
//            }
//        }
//    }
//    private class UpdateNewLesson extends AsyncTask<String, String, String> {
////        String email = mEditTextEmail.getText().toString();
////        String password = mEditTextPassword.getText().toString();
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (!InternetUtils.isInternetConnected(NewLessonActivity.this)) {
//                cancel(true);
//            }
////            progressDialog = new ProgressDialog(NewLessonActivity.this);
////            progressDialog.setMessage(getResources().getString(R.string.loading));
//            progressDialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            if (isCancelled()) {
//                return null;
//            }
//            mListWordAns = mNewLessonAdapter.getListWordAnswer();
//            JSONObject jsonArray = new JSONObject();
//            for (int i = 0; i < mListWordNewLesson.size(); i++) {
//                JSONObject jsonObjectWord = new JSONObject();
//                try {
//                    jsonObjectWord.put(Const.ID, String.valueOf(mListResuiltId.get(i)));
//                    Log.i("AAAAAAAAA",mListResuiltId.get(i)+"");
//                    jsonObjectWord.put(Const.ANSWER_ID, String.valueOf(mListWordAns.get(i).getResultId()));
//                    Log.i("AAAAAAAAA","B : "+mListWordAns.get(i).getResultId()+"");
//                    jsonArray.put(String.valueOf(i), jsonObjectWord);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            JSONObject jsonObjectMid = new JSONObject();
//            JSONObject jsonObjectPost = new JSONObject();
//            try {
//                jsonObjectMid.put(Const.RESULT_ATTRIBUTES, jsonArray);
//                jsonObjectMid.put(Const.LEARNED, String.valueOf(true));
//                jsonObjectPost.put(Const.LESSON, jsonObjectMid);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            String url = Const.URL_UPDATE_LESSON + mLesson.getId() + Const.JSON_TYPE + "?" +
//                    Const.AUTH_TOKEN + "=" + mUser.getAuthToken();
//            String response = null;
//            Log.i("FFFFFFFFFFF",jsonObjectPost.toString());
//            Log.i("FFFFFFFFFFF","url : "+url);
//            try {
//                response = HttpRequest.postJsonRequest(url, jsonObjectPost, APIService.METHOD_PATCH);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return response;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
////            progressDialog.dismiss();
//            if (s == null) {
//                Toast.makeText(NewLessonActivity.this, R.string.response_null, Toast.LENGTH_SHORT)
//                        .show();
//            } else if ((s.substring(0, s.indexOf(":"))).contains(R.string.Exception + "") ||
//                    (s.substring(0, s.indexOf(":"))).contains(R.string.StackTrace + "")) {
//                Toast.makeText(NewLessonActivity.this, R.string.response_error, Toast.LENGTH_SHORT)
//                        .show();
//            } else {
////                Toast.makeText(NewLessonActivity.this, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(NewLessonActivity.this, R.string.update_done, Toast.LENGTH_SHORT)
//                        .show();
//                }
//                mListWordNewLesson.clear();
//                new CreateNewLesson().execute();
//            }
//        }
    }

