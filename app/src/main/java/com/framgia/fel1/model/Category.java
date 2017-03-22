package com.framgia.fel1.model;
import com.framgia.fel1.constant.Const;
import com.google.gson.annotations.SerializedName;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by vuduychuong1994 on 4/14/16.
 */
public class Category implements Serializable {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;
    @SerializedName("photo") private String mPhoto;
    @SerializedName("learned_words") private String mLearnWords;

    public Category() {
    }

    public Category(int id, String name, String photo, String learnWords) {
        mId = id;
        mName = name;
        mPhoto = photo;
        mLearnWords = learnWords;
    }

    public Category(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(Const.ID)) {
            mId = jsonObject.getInt(Const.ID);
        }
        if (jsonObject.has(Const.NAME)) {
            mName = jsonObject.getString(Const.NAME);
        }
        if (jsonObject.has(Const.PHOTO)) {
            mPhoto = jsonObject.getString(Const.PHOTO);
        }
        if (jsonObject.has(Const.LEARNED_WORDS)) {
            mLearnWords = jsonObject.getString(Const.LEARNED_WORDS);
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

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

    public String getLearnWords() {
        return mLearnWords;
    }

    public void setLearnWords(String learnWords) {
        mLearnWords = learnWords;
    }
}
