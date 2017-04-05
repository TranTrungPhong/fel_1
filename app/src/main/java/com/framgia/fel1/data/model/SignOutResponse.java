package com.framgia.fel1.data.model;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public class SignOutResponse {
    @SerializedName("message") private String mMessage;

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }
}
