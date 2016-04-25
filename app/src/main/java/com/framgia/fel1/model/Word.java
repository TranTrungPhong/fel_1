package com.framgia.fel1.model;

import com.framgia.fel1.constant.Const;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vuduychuong1994 on 4/14/16.
 */
public class Word implements Serializable{
    private int mId;
    private int mLessonId;
    private int mResultId;
    private String mContent;
    private List<Answer> mAnswers;

    public Word() {
    }

    public Word(int id, int lessonId, int resultId, String content, List<Answer> answers) {
        mId = id;
        mLessonId = lessonId;
        mResultId = resultId;
        mContent = content;
        mAnswers = answers;
    }

    public Word(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(Const.ID)) {
            mId = jsonObject.getInt(Const.ID);
        }
        if (jsonObject.has(Const.RESULT_ID)) {
            mResultId = jsonObject.getInt(Const.RESULT_ID);
        }
        if (jsonObject.has(Const.CONTENT)) {
            mContent = jsonObject.getString(Const.CONTENT);
        }
        if (jsonObject.has(Const.ANSWERS)) {
            mAnswers = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray(Const.ANSWERS);
            for (int i = 0; i < jsonArray.length(); i++) {
                Answer answers = new Answer(jsonArray.getJSONObject(i));
                mAnswers.add(answers);
            }
        }

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getLessonId() {
        return mLessonId;
    }

    public void setLessonId(int lessonId) {
        mLessonId = lessonId;
    }

    public int getResultId() {
        return mResultId;
    }

    public void setResultId(int resultId) {
        mResultId = resultId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public List<Answer> getAnswers() {
        return mAnswers;
    }

    public void setAnswers(List<Answer> answers) {
        mAnswers = answers;
    }
}
