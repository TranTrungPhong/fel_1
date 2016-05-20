package com.framgia.fel1.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.adapter.CustomSpinnerAdapter;
import com.framgia.fel1.adapter.MyItemRecyclerViewAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.ItemList2;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.DividerItemDecoration;
import com.framgia.fel1.util.ExportFile;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.TaskFragment;
import com.itextpdf.text.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity
        implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener,
        TaskFragment.TaskCallbacks {
    public static final String TAG = "WordListActivity";
    private static final String TAG_TASK_FRAGMENT = "task_fragment";
    private static final String LIST_POSITION = "list_position";
    private static final int EXPORT_PDF = 1;
    private static final int EXPORT_CSV = 2;
    private static final int EXPORT_TSV = 3;
    private TaskFragment mTaskFragment;
    private static final int THRESHOLD_ITEM_COUNT = 15;
    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mMyItemRecyclerViewAdapter;
    private List<ItemList2> mListItem;
    private LinearLayout mLayoutLoad;
    private Intent mData;
    private int mCategoryId;
    private Toolbar mToolbar;
    private Spinner mSpinner;
    private ArrayList<String> mListSpinner;
    private SharedPreferences mSharedPreferences;
    private MySqliteHelper mMySqliteHelper;
    private User mUser;
    private boolean mIsLoadingMore = true;
    private boolean mIsLoading = false;
    private int mPastVisiblesItems;
    private int mVisibleItemCount;
    private LinearLayoutManager mLayoutManager;
    private int mPage = 1;
    private int mFormatExport = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if ( mTaskFragment == null ) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
            mTaskFragment.onAttach((Context) this);
        }
        setView();
        setData();
        setEvent();
    }

    private void setView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutLoad = (LinearLayout) findViewById(R.id.layout_load);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mListItem = new ArrayList<>();
        mMyItemRecyclerViewAdapter =
                new MyItemRecyclerViewAdapter(WordListActivity.this, mListItem);
        mRecyclerView.setAdapter(mMyItemRecyclerViewAdapter);
        //        mRecyclerView.addItemDecoration(new DividerItemDecoration(WordListActivity.this,
        //                                                                  DividerItemDecoration.VERTICAL_LIST,
        //                                                                  R.drawable.divider_word_list));
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSpinner = (Spinner) findViewById(R.id.spinner);

    }

    private void setData() {
        mData = getIntent();
        mCategoryId = mData.getIntExtra(Const.CATEGORY_ID, 0);
        mMySqliteHelper = new MySqliteHelper(this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        int id = mSharedPreferences.getInt(Const.ID, - 1);
        if ( id != - 1 )
            mUser = mMySqliteHelper.getUser(id);
        else
            finish();
        mLayoutLoad.setVisibility(View.VISIBLE);
        if ( mMySqliteHelper.countTable(MySqliteHelper.TABLE_WORD) == 0 &&
                InternetUtils.isInternetConnected(WordListActivity.this) )
            mTaskFragment.startInBackground(
                    new String[]{String.valueOf(mCategoryId), String.valueOf(mPage)});
        else {
            mLayoutLoad.setVisibility(View.GONE);
            mIsLoadingMore = false;
            getWordListFromDatabase();
        }
        setListSpinner();

    }

    private void getWordListFromDatabase() {
        List<Word> wordList = mMySqliteHelper.getListWord();
        mListItem.clear();
        for (Word word : wordList) {
            List<Answer> answerList = mMySqliteHelper.getListAnswerByWord(word.getId());
            for (Answer answer : answerList) {
                if ( answer.getCorrect() )
                    mListItem.add(new ItemList2(String.valueOf(word.getId()), word.getContent(),
                                                answer.getContent()));
            }
        }
    }

    private void setListSpinner() {
        mListSpinner = new ArrayList<>();
        mListSpinner.add(Const.ALL_WORD);
        mListSpinner.add(Const.LEARNED);
        mListSpinner.add(Const.NO_LEARN);
        ArrayList<String> mSpinnerItems = new ArrayList<>();
        mSpinnerItems.add("All word");
        mSpinnerItems.add("Learned");
        mSpinnerItems.add("No learn");
//        ArrayAdapter<String> adapterSpinner =
//                new ArrayAdapter(WordListActivity.this, android.R.layout.simple_list_item_1,
//                                 mSpinnerItems);
        CustomSpinnerAdapter customSpinnerAdapter =
                new CustomSpinnerAdapter(WordListActivity.this, mSpinnerItems);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(customSpinnerAdapter);
    }

    private void setEvent() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterString = mListSpinner.get(position);
                mMyItemRecyclerViewAdapter.getFilter().filter(filterString);
                if ( mMyItemRecyclerViewAdapter.getListFiltered().size() > 0 ) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(0);
                if ( mMyItemRecyclerViewAdapter.getListFiltered().size() > 0 ) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ( dy > 0 ) {
                    mVisibleItemCount = mLayoutManager.getChildCount();
                    mPastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ( mIsLoadingMore ) {
                        if ( (mVisibleItemCount + mPastVisiblesItems + THRESHOLD_ITEM_COUNT) >=
                                mListItem.size() ) {
                            if ( ! mIsLoading &&
                                    InternetUtils.isInternetConnected(WordListActivity.this) )
                                mTaskFragment.startInBackground(
                                        new String[]{String.valueOf(mCategoryId),
                                                String.valueOf(mPage)});
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export_pdf:
                mFormatExport = EXPORT_PDF;
                try {
                    boolean exportAble = true;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        exportAble = checkWritePermission();
                    }
                    if(exportAble)
                        exportPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_pdf,
                                   Toast.LENGTH_SHORT).show();
                } catch (DocumentException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_pdf,
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_export_csv:
                mFormatExport = EXPORT_CSV;
                try {
                    boolean exportAble = true;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        exportAble = checkWritePermission();
                    }
                    if(exportAble)
                        exportCsv();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_csv,
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_export_tsv:
                mFormatExport = EXPORT_TSV;
                try {
                    boolean exportAble = true;
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        exportAble = checkWritePermission();
                    }
                    if(exportAble)
                        exportTsv();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_tsv,
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_sync:
                if ( InternetUtils.isInternetConnected(WordListActivity.this) ) {
                    mLayoutLoad.setVisibility(View.VISIBLE);
                    mListItem.clear();
                    mIsLoadingMore = true;
                    mPage = 1;
                    mTaskFragment.startInBackground(
                            new String[]{String.valueOf(mCategoryId), String.valueOf(mPage)});
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTsv() throws IOException {
        String fileName;
        if ( mData.getStringExtra(Const.CATEGORY_ID) != null ) {
            fileName = Const.CATEGORY + "_" + mData.getStringExtra(Const.CATEGORY_ID) + "_" +
                    mListSpinner.get(mSpinner.getSelectedItemPosition());
        } else {
            fileName = Const.CATEGORY + "_" + mListSpinner.get(mSpinner.getSelectedItemPosition());
        }
        StringBuilder builder = new StringBuilder();
        String columnStr = "\"" + Const.WORD + "\"\t " + "\"" + Const.ANSWER + "\"";
        builder.append(columnStr);
        for (ItemList2 item : mMyItemRecyclerViewAdapter.getListFiltered()) {
            builder.append("\n").append("\"").append(item.getContent()).append("\"\t ").append(
                    "\"").append(item.getDetail()).append("\"");
        }
        if ( ExportFile.exportTsv(WordListActivity.this, fileName, builder.toString()) ) {
            Toast.makeText(WordListActivity.this, R.string.export_tsv_success,
                           Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WordListActivity.this, R.string.error_export_tsv,
                           Toast.LENGTH_SHORT).show();
        }
    }

    private void exportCsv() throws IOException {
        String fileName;
        if ( mData.getStringExtra(Const.CATEGORY_ID) != null ) {
            fileName = Const.CATEGORY + "_" + mData.getStringExtra(Const.CATEGORY_ID) + "_" +
                    mListSpinner.get(mSpinner.getSelectedItemPosition());
        } else {
            fileName = Const.CATEGORY + "_" + mListSpinner.get(mSpinner.getSelectedItemPosition());
        }
        StringBuilder stringBuilder = new StringBuilder();
        String columnString = Const.WORD + ", " + Const.ANSWER;
        stringBuilder.append(columnString);
        for (ItemList2 item : mMyItemRecyclerViewAdapter.getListFiltered()) {
            stringBuilder.append("\n").append(item.getContent()).append(", ").append(
                    item.getDetail());
        }
        if ( ExportFile.exportCsv(WordListActivity.this, fileName, stringBuilder.toString()) ) {
            Toast.makeText(WordListActivity.this, R.string.export_csv_success,
                           Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WordListActivity.this, R.string.error_export_csv,
                           Toast.LENGTH_SHORT).show();
        }
    }

    private void exportPdf() throws IOException, DocumentException {
        int permissionCheck = ContextCompat.checkSelfPermission(WordListActivity.this,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        String fileName;
        if ( mData.getStringExtra(Const.CATEGORY_ID) != null ) {
            fileName = Const.CATEGORY + "_" + mData.getStringExtra(Const.CATEGORY_ID) + "_" +
                    mListSpinner.get(mSpinner.getSelectedItemPosition());
        } else {
            fileName = Const.CATEGORY + "_" + mListSpinner.get(mSpinner.getSelectedItemPosition());
        }
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(Const.WORD);
        arrayList.add(Const.ANSWER);
        for (ItemList2 item : mMyItemRecyclerViewAdapter.getListFiltered()) {
            arrayList.add(item.getContent());
            arrayList.add(item.getDetail());
        }
        if ( ExportFile.exportPdf(WordListActivity.this, fileName, arrayList,
                                  Const.COLUMN_WORD_LIST) ) {
            Toast.makeText(WordListActivity.this, R.string.export_pdf_success,
                           Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WordListActivity.this, R.string.error_export_pdf,
                           Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkWritePermission() {
        // Here, thisActivity is the current activity
        if (android.support.v4.app.ActivityCompat.checkSelfPermission(WordListActivity.this,
                                              Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(WordListActivity.this,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Log.d(TAG, "checking");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                ActivityCompat.requestPermissions(WordListActivity.this,
                                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                  Const.MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Const.MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission OK");
                    try {
                    switch (mFormatExport){
                        case EXPORT_PDF:
                            exportPdf();
                            break;
                        case EXPORT_CSV:
                            exportCsv();
                            break;
                        case EXPORT_TSV:
                            exportTsv();
                            break;
                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.d(TAG, "permission failed");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
               return;

        }
    }

    @Override
    public void onListFragmentInteraction(int position, ItemList2 item) {
        Log.d(TAG, String.valueOf(position));
    }

    @Override
    public void onPreExecute() {
        mIsLoading = true;
        if ( mPage == 1 )
            mLayoutLoad.setVisibility(View.VISIBLE);
    }

    @Override
    public String onBackGround(String[] param) {
        return getWordListByCategory(Integer.parseInt(param[0]), Integer.parseInt(param[1]));
    }

    @Override
    public void onProgressUpdate(String response) {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(String response) {
        if ( mPage == 1 )
            mListItem.clear();
        if ( response == null ) {
            Toast.makeText(WordListActivity.this, R.string.error_get_word_list,
                           Toast.LENGTH_SHORT).show();
        } else {
            mPage++;
            try {
                JSONObject wordListObject = new JSONObject(response);
                JSONArray wordListArray = wordListObject.getJSONArray(Const.WORDS);
                if ( wordListArray.length() < Const.DEFAULT_PERPAGE_WORDLIST )
                    mIsLoadingMore = false;
                for (int i = 0; i < wordListArray.length(); i++) {
                    Word word = new Word(wordListArray.getJSONObject(i));
                    try {
                        mMySqliteHelper.addWord(word);
                    } catch (SQLiteConstraintException e) {
                        e.printStackTrace();
                        //                        mMySqliteHelper.updateWord(word);
                    }
                    if ( word != null ) {
                        for (int j = 0; j < word.getAnswers().size(); j++) {
                            Answer answer = word.getAnswers().get(j);
                            answer.setWordId(word.getId());
                            try {
                                mMySqliteHelper.addAnswer(answer);
                            } catch (SQLiteConstraintException e) {
                                e.printStackTrace();
                                mMySqliteHelper.updateAnswer(answer);
                            }
                            if ( answer.getCorrect() ) {
                                mListItem.add(new ItemList2(String.valueOf(word.getId()),
                                                            word.getContent(),
                                                            answer.getContent()));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(WordListActivity.this, R.string.register_error,
                               Toast.LENGTH_SHORT).show();
                Log.d(TAG, response.toString());
            }

        }
        mMyItemRecyclerViewAdapter.notifyDataSetChanged();
        mMyItemRecyclerViewAdapter.getFilter().filter(Const.ALL_WORD);
        mLayoutLoad.setVisibility(View.GONE);
        mIsLoading = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIST_POSITION, mLayoutManager.findFirstVisibleItemPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int position = savedInstanceState.getInt(LIST_POSITION, - 1);
        if ( position != - 1 )
            mRecyclerView.smoothScrollToPosition(position);
    }

    private String getWordListByCategory(Integer categoryId, Integer page) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(APIService.URL_WORD_LIST).append("?").append(
                Const.AUTH_TOKEN + "=").append(mUser.getAuthToken()).append("&").append(
                Const.CATEGORY_ID + "=").append(categoryId).append("&").append(
                Const.PAGE + "=").append(page).append("&").append(Const.PER_PAGE + "=").append(
                Const.DEFAULT_PERPAGE_WORDLIST);
        Log.d(TAG, stringBuilder.toString());
        return HttpRequest.getJSON(stringBuilder.toString());
    }
}