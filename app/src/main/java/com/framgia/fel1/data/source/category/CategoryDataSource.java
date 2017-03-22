package com.framgia.fel1.data.source.category;
import android.support.annotation.NonNull;
import com.framgia.fel1.model.Category;
import java.util.List;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public interface CategoryDataSource {
    interface Callback {

        void onCategoryLoaded(List<Category> categoryList);

        void onDataNotAvailable();
    }

    void getCategories(String authToken, @NonNull Callback callback);

}
