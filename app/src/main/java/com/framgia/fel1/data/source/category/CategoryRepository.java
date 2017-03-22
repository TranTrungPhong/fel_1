package com.framgia.fel1.data.source.category;
import android.support.annotation.NonNull;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */
public class CategoryRepository implements CategoryDataSource {

    private final CategoryDataSource mDataSource;

    private static CategoryRepository sInstance;

    public static CategoryRepository getInstance(
            CategoryDataSource categoryRemoteDataSource) {
        if (sInstance == null) {
            sInstance = new CategoryRepository(categoryRemoteDataSource);
        }
        return sInstance;
    }

    private CategoryRepository(CategoryDataSource categoryRemoteDataSource) {
        mDataSource = categoryRemoteDataSource;
    }

    @Override
    public void getCategories(
            String authToken, @NonNull Callback callback) {
        mDataSource.getCategories(authToken, callback);
    }
}
