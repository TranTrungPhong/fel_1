package com.framgia.fel1.util;

import android.content.Context;
import android.util.Log;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.model.Answer;
import com.framgia.fel1.model.Lesson;
import com.framgia.fel1.model.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhongTran on 04/19/2016.
 */
public class ReadJson {
    private static final String TAG = "ReadJson";
    private Context context;

    public ReadJson(Context context) {
        this.context = context;
    }

    public List<Lesson> readLessonJSONFile() throws IOException, JSONException {
        String jsonText = readText(context, R.raw.lesson);
        JSONObject jsonRoot = new JSONObject(jsonText);
        JSONArray jsonArrayLesson = jsonRoot.getJSONArray(Const.LESSON);
        List<Lesson> mLessonsList = new ArrayList<>();
        for (int i = 0; i < jsonArrayLesson.length(); i++) {
            JSONObject jsonObjectLesson = jsonArrayLesson.getJSONObject(i);
            int id = jsonObjectLesson.getInt(Const.ID);
            String name = jsonObjectLesson.getString(Const.NAME);
            JSONArray jsonArray = jsonObjectLesson.getJSONArray(Const.WORDS);
            List<Word> wordsList = new ArrayList<>();
            for (int h = 0; h < jsonArray.length(); h++) {
                JSONObject jsonObjectWord = jsonArray.getJSONObject(h);
                int idWord = jsonObjectWord.getInt(Const.ID);
                int resultId = jsonObjectWord.getInt(Const.RESULT_ID);
                String content = jsonObjectWord.getString(Const.CONTENT);
                JSONArray jsonArrayAnswer = jsonObjectWord.getJSONArray(Const.ANSWERS);
                List<Answer> answersList = new ArrayList<>();
                for (int j = 0; j < jsonArrayAnswer.length(); j++) {
                    JSONObject jsonObjectAnswer = jsonArrayAnswer.optJSONObject(j);
                    int idAnswer = jsonObjectAnswer.optInt(Const.ID);
                    String contentAnswer = jsonObjectAnswer.optString(Const.CONTENT);
                    boolean isCorrect = jsonObjectAnswer.optBoolean(Const.IS_CORRECT);
                    Answer answer = new Answer(idAnswer, idWord, contentAnswer, isCorrect);
                    answersList.add(answer);
                }
                Word word = new Word(idWord, id, resultId, content, answersList);
                wordsList.add(word);
            }
            Lesson lesson = new Lesson(id, name, wordsList);
            mLessonsList.add(lesson);
        }
        return mLessonsList;
    }

    private String readText(Context context, int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String s = null;
        while ((s = br.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }

}


