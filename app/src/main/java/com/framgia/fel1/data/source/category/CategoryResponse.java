package com.framgia.fel1.data.source.category;
import com.framgia.fel1.model.Category;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public class CategoryResponse {
    @SerializedName("total_pages") private int mTotalPage;
    @SerializedName("categories") private List<Category> mCategoryList;

    public int getTotalPage() {
        return mTotalPage;
    }

    public void setTotalPage(int totalPage) {
        mTotalPage = totalPage;
    }

    public List<Category> getCategoryList() {
        return mCategoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        mCategoryList = categoryList;
    }
}
