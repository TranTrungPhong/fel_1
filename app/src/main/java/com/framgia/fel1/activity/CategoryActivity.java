package com.framgia.fel1.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.LessonLearnedAdapter;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Result;
import com.framgia.fel1.model.User;
import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity
        implements View.OnClickListener, LessonLearnedAdapter.OnClickItemLessonLearned {
    private RecyclerView mRecyclerLessonLearned;
    private LessonLearnedAdapter mLessonLearnedAdapter;
    private List<Lesson> mLearnedLessonsList = new ArrayList<>();
    private String mAuthToken;
    private int mCategorId;
    private String mNameCategory;
    private MySqliteHelper mMySqliteHelper;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog mAlertDialog;
    private User mUser;
    private SharedPreferences mSharedPreferences;
    private List<Result> mListResult;
    private List<Lesson> mLearnedLessonsListResume = new ArrayList<>();
    private Toolbar mToolbar;
    private ProgressDialog progressDialog;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_learned_layout);
        initView();
        initData();
        createNewLesson();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.dismiss();
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if (id != -1) {
            mUser = mMySqliteHelper.getUser(id);
            mListResult = mMySqliteHelper.getListResultByUser(mUser.getId());
        } else {
            finish();
        }

        mLearnedLessonsList.clear();
        mLearnedLessonsListResume.clear();
        for (Result result : mListResult) {
            mLearnedLessonsListResume
                    .addAll(mMySqliteHelper.getListLesson(result.getIdLesson(), mCategorId));
        }
        mLearnedLessonsList.addAll(mLearnedLessonsListResume);
        if (mLessonLearnedAdapter != null) {
            mLessonLearnedAdapter.notifyDataSetChanged();
        }
    }

    private void initView() {
        progressDialog = new ProgressDialog(CategoryActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.info_lesson));
        mToolbar = (Toolbar) findViewById(R.id.toolbar_lesson);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerLessonLearned = (RecyclerView) findViewById(R.id.listview_words);
        mRecyclerLessonLearned.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initData() {
        Intent receiveIntent = getIntent();
        mAuthToken = receiveIntent.getStringExtra(Const.AUTH_TOKEN);
        mNameCategory = receiveIntent.getStringExtra(Const.NAME);
        mCategorId = receiveIntent.getIntExtra(Const.ID, -1);
        mMySqliteHelper = new MySqliteHelper(CategoryActivity.this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);

        mToolbar.setTitle(mNameCategory);

        int id = mSharedPreferences.getInt(Const.ID, -1);
        if (id != -1) {
            mUser = mMySqliteHelper.getUser(id);
            mListResult = mMySqliteHelper.getListResultByUser(mUser.getId());
        } else {
            finish();
        }
        mLearnedLessonsList.clear();
        for (Result result : mListResult) {
            mLearnedLessonsList
                    .addAll(mMySqliteHelper.getListLesson(result.getIdLesson(), mCategorId));
        }
        mLessonLearnedAdapter = new LessonLearnedAdapter(this, mLearnedLessonsList);
        mRecyclerLessonLearned.setAdapter(mLessonLearnedAdapter);
        mLessonLearnedAdapter.notifyDataSetChanged();
    }

    private void createNewLesson() {
        LayoutInflater li = LayoutInflater.from(CategoryActivity.this);
        View promptsView = li.inflate(R.layout.create_lesson, null);
        alertDialogBuilder = new AlertDialog.Builder(CategoryActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText mEditTextPage = (EditText) promptsView.findViewById(R.id.edittext_page);
        final EditText mEditTextPerPage =
                (EditText) promptsView.findViewById(R.id.edittext_per_page);
        alertDialogBuilder.setCancelable(false)
                          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  String page = mEditTextPage.getText().toString();
                                  String perPage = mEditTextPerPage.getText().toString();
                                  if (page.equals("") || perPage.equals("")) {
                                      Toast.makeText(getApplicationContext(), R.string.misspage,
                                              Toast.LENGTH_SHORT).show();
                                  } else {
                                      dialog.dismiss();
                                      Intent intent = new Intent(CategoryActivity.this,
                                              LessonActivity.class);
                                      intent.putExtra(Const.AUTH_TOKEN, mAuthToken);
                                      intent.putExtra(Const.NAME, mNameCategory);
                                      intent.putExtra(NetwordConst.PAGE, page);
                                      intent.putExtra(NetwordConst.PER_PAGE, perPage);
                                      startActivity(intent);
                                  }
                              }
                          }).setNegativeButton(R.string.huy, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        mAlertDialog = alertDialogBuilder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                LessonActivity.isLessonLoad = false;
                final Intent intent =
                        new Intent(CategoryActivity.this, LessonActivity.class);
                intent.putExtra(Const.AUTH_TOKEN, mAuthToken);
                intent.putExtra(Const.NAME, mNameCategory);
                intent.putExtra(Const.CATEGORY_ID, mCategorId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickItemLessonLearned(int position, Lesson lesson) {
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.show();
        Intent intent = new Intent(CategoryActivity.this, ResultActivity.class);
        intent.putExtra(Const.LESSON, lesson);
        startActivity(intent);
    }
}
