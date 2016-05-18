package com.framgia.fel1.model;

import com.framgia.fel1.constant.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by vuduychuong1994 on 4/14/16.
 */
public class Answer implements Serializable {
    private int mId;
    private int mWordId;
    private String mContent;
    private Boolean mIsCorrect;

    public Answer() {
    }

    public Answer(int id, int wordId, String content, Boolean isCorrect) {
        mId = id;
        mWordId = wordId;
        mContent = content;
        mIsCorrect = isCorrect;
    }

    public Answer(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(Const.ID)) {
            mId = jsonObject.getInt(Const.ID);
        }
        if (jsonObject.has(Const.CONTENT)) {
            mContent = jsonObject.getString(Const.CONTENT);
        }
        if (jsonObject.has(Const.IS_CORRECT)) {
            mIsCorrect = jsonObject.getBoolean(Const.IS_CORRECT);
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getWordId() {
        return mWordId;
    }

    public void setWordId(int wordId) {
        mWordId = wordId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Boolean getCorrect() {
        return mIsCorrect;
    }

    public void setCorrect(Boolean correct) {
        mIsCorrect = correct;
    }
}
