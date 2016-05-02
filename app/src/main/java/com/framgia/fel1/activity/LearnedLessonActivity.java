package com.framgia.fel1.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.framgia.fel1.R;
import com.framgia.fel1.adapter.LessonLearnedAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Lesson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongTran on 04/18/2016.
 */
public class LearnedLessonActivity extends Activity implements View.OnClickListener,
        LessonLearnedAdapter.OnClickItemLessonLearned {
    private RecyclerView mRecyclerLessonLearned;
    private LessonLearnedAdapter mLessonLearnedAdapter;
    private Button mButtonCreateLesson;
    private Button mButtonWordList;
    private List<Lesson> mLearnedLessonsList = new ArrayList<>();
    private String mAuthToken;
    private int mCategorId;
    private String mNameCategory;
    private MySqliteHelper mMySqliteHelper;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog mAlertDialog;

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
        mLessonLearnedAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mButtonCreateLesson = (Button) findViewById(R.id.button_create_lesson);
        mButtonWordList = (Button) findViewById(R.id.button_word_list);
        mButtonCreateLesson.setOnClickListener(this);
        mButtonWordList.setOnClickListener(this);
        mRecyclerLessonLearned = (RecyclerView) findViewById(R.id.listview_words);
        mRecyclerLessonLearned.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void initData() {
        Intent receiveIntent = getIntent();
        mAuthToken = receiveIntent.getStringExtra(Const.AUTH_TOKEN);
        mNameCategory = receiveIntent.getStringExtra(Const.NAME);
        mCategorId = receiveIntent.getIntExtra(Const.ID, -1);
        mMySqliteHelper = new MySqliteHelper(LearnedLessonActivity.this);
        mLearnedLessonsList.clear();
        mLearnedLessonsList = mMySqliteHelper.getListLesson();
        mLessonLearnedAdapter = new LessonLearnedAdapter(this, mLearnedLessonsList);
        mRecyclerLessonLearned.setAdapter(mLessonLearnedAdapter);
    }

    private void createNewLesson() {
        LayoutInflater li = LayoutInflater.from(LearnedLessonActivity.this);
        View promptsView = li.inflate(R.layout.create_lesson, null);
        alertDialogBuilder = new AlertDialog.Builder(LearnedLessonActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText mEditTextPage =
                (EditText) promptsView.findViewById(R.id.edittext_page);
        final EditText mEditTextPerPage =
                (EditText) promptsView.findViewById(R.id.edittext_per_page);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String page = mEditTextPage.getText().toString();
                                String perPage = mEditTextPerPage.getText().toString();
                                if (page.equals("") || perPage.equals("")) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.misspage,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    dialog.dismiss();
                                    Intent intent = new Intent(LearnedLessonActivity.this,
                                            NewLessonActivity.class);
                                    intent.putExtra(Const.AUTH_TOKEN, mAuthToken);
                                    intent.putExtra(Const.NAME, mNameCategory);
                                    intent.putExtra(APIService.PAGE, page);
                                    intent.putExtra(APIService.PER_PAGE, perPage);
                                    startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton(R.string.huy,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        mAlertDialog = alertDialogBuilder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_create_lesson:
                mAlertDialog.show();
                break;
            case R.id.button_word_list:
                //Todo call activity wordlist
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickItemLessonLearned(int position, Lesson lesson) {
        // TODO call lesson or word list
        Intent intent = new Intent(LearnedLessonActivity.this, ResultActivity.class);
        intent.putExtra(Const.LESSON, lesson);
        startActivity(intent);
    }
}
