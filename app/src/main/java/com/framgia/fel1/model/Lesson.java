package com.framgia.fel1.model;

import com.framgia.fel1.constant.Const;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vuduychuong1994 on 4/14/16.
 */
public class Lesson implements Serializable{
    private int mId;
    private int mIdCategory;
    private String mName;
    private List<Word> mWords;

    public Lesson() {
    }

    public Lesson(int id, String name, List<Word> words) {
        mId = id;
        mName = name;
        mWords = words;
    }
    public Lesson(int id, int idCategory, String name, List<Word> words) {
        mId = id;
        mIdCategory = idCategory;
        mName = name;
        mWords = words;
    }

    public Lesson(String responseString) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.has(Const.LESSON)) {
            JSONObject object = jsonObject.getJSONObject(Const.LESSON);
            if (jsonObject.has(Const.ID)) {
                mId = jsonObject.getInt(Const.ID);
            }
            if (jsonObject.has(Const.NAME)) {
                mName = jsonObject.getString(Const.NAME);
            }
            if (jsonObject.has(Const.WORDS)) {
                mWords = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray(Const.WORDS).length(); i++) {
                    Word words = new Word(jsonObject.getJSONArray(Const.WORDS).getJSONObject(i));
                    mWords.add(words);
                }
            }
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<Word> getWords() {
        return mWords;
    }

    public void setWords(List<Word> words) {
        mWords = words;
    }

    public int getmIdCategory() {
        return mIdCategory;
    }

    public void setmIdCategory(int mIdCategory) {
        this.mIdCategory = mIdCategory;
    }
}
