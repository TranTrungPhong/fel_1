package com.framgia.fel1.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.framgia.fel1.adapter.MyItemRecyclerViewAdapter;
import com.framgia.fel1.constant.APIService;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.ItemList2;
import com.framgia.fel1.model.User;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.ExportFile;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.itextpdf.text.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity
        implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener {
    public static final String TAG = "WordListActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_list);
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
        if ( mMySqliteHelper.countTable(MySqliteHelper.TABLE_WORD) == 0 )
            new WordListRequest().execute(mCategoryId, mPage);
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
        ArrayAdapter<String> adapterSpinner =
                new ArrayAdapter(WordListActivity.this, android.R.layout.simple_list_item_1,
                                 mListSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(adapterSpinner);
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
                String filterString = parent.getItemAtPosition(position).toString();
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
                            if ( ! mIsLoading )
                                new WordListRequest().execute(mCategoryId, mPage);
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
                try {
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
                try {
                    exportCsv();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_csv,
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_export_tsv:
                try {
                    exportTsv();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(WordListActivity.this, R.string.error_export_tsv,
                                   Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_sync:
                mLayoutLoad.setVisibility(View.VISIBLE);
                mListItem.clear();
                mIsLoadingMore = true;
                mPage = 1;
                new WordListRequest().execute(mCategoryId, mPage);
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

    @Override
    public void onListFragmentInteraction(int position, ItemList2 item) {
        Log.d(TAG, String.valueOf(position));
    }

    private class WordListRequest extends AsyncTask<Integer, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( ! InternetUtils.isInternetConnected(WordListActivity.this) ) {
                cancel(true);
            }
            mIsLoading = true;
            if ( mPage == 1 )
                mLayoutLoad.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            if ( isCancelled() ) {
                return null;
            }
            return getWordListByCategory(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String response) {
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
                        mMySqliteHelper.addWord(word);
                        if ( word != null ) {
                            for (int j = 0; j < word.getAnswers().size(); j++) {
                                Answer answer = word.getAnswers().get(j);
                                answer.setWordId(word.getId());
                                mMySqliteHelper.addAnswer(answer);
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