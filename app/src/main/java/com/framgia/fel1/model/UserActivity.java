package com.framgia.fel1.model;

import com.framgia.fel1.constant.Const;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

/**
 * Created by vuduychuong1994 on 4/14/16.
 */
public class UserActivity implements Serializable {
    private int mId;
    private String mContent;
    private String mCreatedAt;

    public UserActivity() {
    }

    public UserActivity(int id, String content, String createdAt) {
        mId = id;
        mContent = content;
        mCreatedAt = createdAt;
    }

    public UserActivity(String responseString) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.has(Const.ID)) {
            mId = jsonObject.getInt(Const.ID);
        }
        if (jsonObject.has(Const.CONTENT)) {
            mContent = jsonObject.getString(Const.CONTENT);
        }
        if (jsonObject.has(Const.CREATED_AT)) {
            mCreatedAt = jsonObject.getString(Const.CREATED_AT);
        }
    }

    public UserActivity(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(Const.ID)) {
            mId = jsonObject.getInt(Const.ID);
        }
        if (jsonObject.has(Const.CONTENT)) {
            mContent = jsonObject.getString(Const.CONTENT);
        }
        if (jsonObject.has(Const.CREATED_AT)) {
            mCreatedAt = jsonObject.getString(Const.CREATED_AT);
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCreated_at() {
        return mCreatedAt;
    }

    public void setCreated_at(String created_at) {
        mCreatedAt = created_at;
    }
}
