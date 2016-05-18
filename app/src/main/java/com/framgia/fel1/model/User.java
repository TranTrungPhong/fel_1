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
public class User implements Serializable {
    private int mId;
    private String mName;
    private String mEmail;
    private String mAvatar;
    private Boolean mAdmin;
    private String mAuthToken;
    private String mCreatedAt;
    private String mUpdatedAt;
    private int mLearnedWords;
    private List<UserActivity> mActivities;

    public User() {
    }

    public User(int id, String name, String email, String avatar, Boolean admin, String authToken,
                String createdAt, String updatedAt, int learnedWords,
                List<UserActivity> activities) {
        mId = id;
        mName = name;
        mEmail = email;
        mAvatar = avatar;
        mAdmin = admin;
        mAuthToken = authToken;
        mCreatedAt = createdAt;
        mUpdatedAt = updatedAt;
        mLearnedWords = learnedWords;
        mActivities = activities;
    }

    public User(String responseString) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.has(Const.USER)) {
            JSONObject object = jsonObject.getJSONObject(Const.USER);
            if (object.has(Const.ID)) {
                mId = object.getInt(Const.ID);
            }
            if (object.has(Const.NAME)) {
                mName = object.getString(Const.NAME);
            }
            if (object.has(Const.EMAIL)) {
                mEmail = object.getString(Const.EMAIL);
            }
            if (object.has(Const.AVATAR)) {
                mAvatar = object.getString(Const.AVATAR);
            }
            if (object.has(Const.ADMIN)) {
                mAdmin = object.getBoolean(Const.ADMIN);
            }
            if (object.has(Const.AUTH_TOKEN)) {
                mAuthToken = object.getString(Const.AUTH_TOKEN);
            }
            if (object.has(Const.CREATED_AT)) {
                mCreatedAt = object.getString(Const.CREATED_AT);
            }
            if (object.has(Const.UPDATED_AT)) {
                mUpdatedAt = object.getString(Const.UPDATED_AT);
            }
            if (object.has(Const.LEARNED_WORDS)) {
                mLearnedWords = object.getInt(Const.LEARNED_WORDS);
            }
            if (object.has(Const.ACTIVITIES)) {
                mActivities = new ArrayList<>();
                for (int i = 0; i < object.getJSONArray(Const.ACTIVITIES).length(); i++) {
                    UserActivity activities = new UserActivity(
                            object.getJSONArray(Const.ACTIVITIES).getJSONObject(i));
                    mActivities.add(activities);
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

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public Boolean getAdmin() {
        return mAdmin;
    }

    public void setAdmin(Boolean admin) {
        mAdmin = admin;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    public String getCreatedAt() {
        return mCreatedAt;
    }

    public void setCreatedAt(String createdAt) {
        mCreatedAt = createdAt;
    }

    public String getUpdatedAt() {
        return mUpdatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        mUpdatedAt = updatedAt;
    }

    public int getLearnedWords() {
        return mLearnedWords;
    }

    public void setLearnedWords(int learnedWords) {
        mLearnedWords = learnedWords;
    }

    public List<UserActivity> getActivities() {
        return mActivities;
    }

    public void setActivities(List<UserActivity> activities) {
        mActivities = activities;
    }

}
