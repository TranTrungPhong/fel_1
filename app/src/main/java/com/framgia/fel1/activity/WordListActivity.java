package com.framgia.fel1.activity;

import android.content.Intent;
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
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.ItemList2;
import com.framgia.fel1.model.Word;
import com.framgia.fel1.util.ExportFile;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ReadData;
import com.itextpdf.text.DocumentException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends AppCompatActivity
        implements MyItemRecyclerViewAdapter.OnListFragmentInteractionListener {
    public static final String TAG = "WordListActivity";
    private RecyclerView mRecyclerView;
    private MyItemRecyclerViewAdapter mMyItemRecyclerViewAdapter;
    private List<ItemList2> mListItem;
    private LinearLayout mLayoutLoad;
    private Intent mData;
    private int mCategoryId;
    private Toolbar mToolbar;
    private Spinner mSpinner;
    private ArrayList<String> mListSpinner;

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
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
        mLayoutLoad.setVisibility(View.VISIBLE);
        new WordListRequest().execute(mCategoryId);
        setListSpinner();

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
                if (mMyItemRecyclerViewAdapter.getListFiltered().size() > 0) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSpinner.setSelection(0);
                if (mMyItemRecyclerViewAdapter.getListFiltered().size() > 0) {
                    mRecyclerView.smoothScrollToPosition(0);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTsv() throws IOException {
        String fileName;
        if(mData.getStringExtra(Const.CATEGORY_ID) != null) {
            fileName = Const.CATEGORY + "_" + mData.getStringExtra(Const.CATEGORY_ID) + "_" +
                    mListSpinner.get(mSpinner.getSelectedItemPosition());
        } else {
            fileName = Const.CATEGORY + "_" + mListSpinner.get(mSpinner.getSelectedItemPosition());
        }
        StringBuilder builder = new StringBuilder();
        String columnStr = "\"" + Const.WORD + "\"\t " + "\"" + Const.ANSWER + "\"";
        builder.append(columnStr);
        for (ItemList2 item : mMyItemRecyclerViewAdapter.getListFiltered()) {
            builder.append("\n").append("\"").append(item.getContent()).append("\"\t ")
                    .append("\"").append(item.getDetail()).append("\"");
        }
        if (ExportFile.exportTsv(WordListActivity.this, fileName, builder.toString())) {
            Toast.makeText(WordListActivity.this, R.string.export_tsv_success,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WordListActivity.this, R.string.error_export_tsv,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void exportCsv() throws IOException {
        String fileName;
        if(mData.getStringExtra(Const.CATEGORY_ID) != null) {
            fileName = Const.CATEGORY + "_" + mData.getStringExtra(Const.CATEGORY_ID) + "_" +
                    mListSpinner.get(mSpinner.getSelectedItemPosition());
        } else {
            fileName = Const.CATEGORY + "_" + mListSpinner.get(mSpinner.getSelectedItemPosition());
        }
        StringBuilder stringBuilder = new StringBuilder();
        String columnString = Const.WORD + ", " + Const.ANSWER;
        stringBuilder.append(columnString);
        for (ItemList2 item : mMyItemRecyclerViewAdapter.getListFiltered()) {
            stringBuilder.append("\n").append(item.getContent()).append(", ")
                    .append(item.getDetail());
        }
        if (ExportFile.exportCsv(WordListActivity.this, fileName, stringBuilder.toString())) {
            Toast.makeText(WordListActivity.this, R.string.export_csv_success,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(WordListActivity.this, R.string.error_export_csv,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void exportPdf() throws IOException, DocumentException {
        String fileName;
        if(mData.getStringExtra(Const.CATEGORY_ID) != null) {
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
        if (ExportFile.exportPdf(WordListActivity.this, fileName, arrayList,
                Const.COLUMN_WORD_LIST)) {
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
            if (!InternetUtils.isInternetConnected(WordListActivity.this)) {
                cancel(true);
            }
            mLayoutLoad.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... params) {
            if (isCancelled()) {
                return null;
            }
            return getWordListByCategory(params[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            mListItem.clear();
            if (response == null) {
                Toast.makeText(WordListActivity.this, R.string.error_get_word_list,
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject wordListObject = new JSONObject(response);
                    JSONArray wordListArray = wordListObject.getJSONArray(Const.WORDS);
                    for (int i = 0; i < wordListArray.length(); i++) {
                        Word word = new Word(wordListArray.getJSONObject(i));
                        if (word != null) {
                            for (int j = 0; j < word.getAnswers().size(); j++) {
                                if (word.getAnswers().get(j).getCorrect()) {
                                    mListItem.add(new ItemList2(String.valueOf(word.getId()),
                                            word.getContent(),
                                            word.getAnswers().get(j).getContent()));
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
            mLayoutLoad.setVisibility(View.GONE);
        }
    }

    private String getWordListByCategory(Integer param) {
        InputStream jsonFileInputStream = getResources().openRawResource(R.raw.words);
        return ReadData.readTextFile(jsonFileInputStream);
    }
}