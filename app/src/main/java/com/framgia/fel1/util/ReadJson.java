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
import java.util.Iterator;
import java.util.List;

/**
 * Created by PhongTran on 04/19/2016.
 */
public class ReadJson {
    private static final String TAG = "ReadJson";
    private Context mContext;

    public static String parseErrorJson(String response) throws JSONException {
        StringBuilder message = new StringBuilder();
        JSONObject jsonObject =
                new JSONObject(response).getJSONObject(Const.MESSAGE);
        Iterator<String> iter = jsonObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            message.append(key).append(" : ");
            try {
                JSONArray value = jsonObject.getJSONArray(key);
                for (int i = 0; i < value.length(); i++) {
                    message.append(value.get(i));
                    if ( i < value.length() - 1 ) {
                        message.append(", ");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ( iter.hasNext() )
                message.append("\n");
        }
        return message.toString();
    }

    public ReadJson(Context context) {
        this.mContext = context;
    }

    public Lesson createLesson(int idLes, int page, int perpage) throws IOException, JSONException {
        Lesson lesson = null;
        int idLesson;
        String nameLesson = null;
        String jsonLessonText = readText(mContext, R.raw.lesson2);
        String jsonWordsText = readText(mContext, R.raw.words);
        JSONObject rootLesson = new JSONObject(jsonLessonText);
        JSONObject rootWord = new JSONObject(jsonWordsText);
        JSONArray jsonArrayLesson = rootLesson.getJSONArray(Const.LESSON);
        JSONObject jsonLessonObject = jsonArrayLesson.optJSONObject(idLes);
        idLesson = jsonLessonObject.getInt(Const.ID);
        nameLesson = jsonLessonObject.getString(Const.NAME);

        JSONArray jsonArrayWords = rootWord.getJSONArray(Const.WORDS);
        List<Word> listWords = new ArrayList<>();
        int firstWordIndex = perpage*(page-1)+1;
        int lastWordIndex = perpage*page;
        for (int i = firstWordIndex; i <= lastWordIndex; i++) {
            JSONObject jsonWordObject = jsonArrayWords.optJSONObject(i);
            int idWord = jsonWordObject.optInt(Const.ID);
            int resultId = jsonWordObject.optInt(Const.RESULT_ID);
            String content = jsonWordObject.optString(Const.CONTENT);
            JSONArray jsonArrayAnswer = jsonWordObject.optJSONArray(Const.ANSWERS);
            List<Answer> answersList = new ArrayList<>();
            for (int j = 0; j < jsonArrayAnswer.length(); j++) {
                JSONObject jsonObjectAnswer = jsonArrayAnswer.optJSONObject(j);
                int idAnswer = jsonObjectAnswer.optInt(Const.ID);
                String contentAnswer = jsonObjectAnswer.optString(Const.CONTENT);
                boolean isCorrect = jsonObjectAnswer.optBoolean(Const.IS_CORRECT);
                Answer answer = new Answer(idAnswer, idWord, contentAnswer, isCorrect);
                answersList.add(answer);
            }
            Word word = new Word(idWord, idLesson, resultId, content, answersList);
            listWords.add(word);
        }
        lesson = new Lesson(idLesson,nameLesson,listWords);
        return lesson;
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


