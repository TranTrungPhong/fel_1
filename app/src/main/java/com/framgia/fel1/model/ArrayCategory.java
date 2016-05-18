package com.framgia.fel1.model;

import com.framgia.fel1.constant.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vuduychuong1994 on 4/18/16.
 */
public class ArrayCategory implements Serializable {
    public static int sTotalPage = 0;
    private List<Category> mCategoryList;

    public ArrayCategory() {
    }

    public ArrayCategory(List<Category> categoryList) {
        mCategoryList = categoryList;
    }

    public ArrayCategory(String responseString) throws JSONException {
        mCategoryList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(responseString);
        if (jsonObject.has(Const.CATEGORIES)) {
            String categories = jsonObject.getString(Const.CATEGORIES);
            JSONArray jsonArray = new JSONArray(categories);
            for (int i = 0; i < jsonArray.length(); i++) {
                Category category = new Category(jsonArray.getJSONObject(i));
                mCategoryList.add(category);
            }
        }
        if (jsonObject.has(Const.TOTAL_PAGES)) {
            sTotalPage = jsonObject.getInt(Const.TOTAL_PAGES);
        }

    }

    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
    }
}
