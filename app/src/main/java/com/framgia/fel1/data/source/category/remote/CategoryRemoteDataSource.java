package com.framgia.fel1.data.source.category.remote;
import android.support.annotation.NonNull;
import com.framgia.fel1.data.source.category.CategoryDataSource;
import com.framgia.fel1.data.source.category.CategoryResponse;
import com.framgia.fel1.network.ServiceGenerator;
import com.framgia.fel1.network.CategoryService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */
public class CategoryRemoteDataSource implements CategoryDataSource {

    private static CategoryRemoteDataSource sInstance;

    public static CategoryRemoteDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new CategoryRemoteDataSource();
        }
        return sInstance;
    }

    private CategoryRemoteDataSource() {
    }

    @Override
    public void getCategories(
            final String authToken, @NonNull final Callback callback) {
        ServiceGenerator.createService(CategoryService.class)
                        .getCategories(authToken)
                        .enqueue(new retrofit2.Callback<CategoryResponse>() {
               @Override
               public void onResponse(
                       Call<CategoryResponse> call, Response<CategoryResponse> response) {
                   if (response == null) {
                       callback.onDataNotAvailable();
                       return;
                   }
                   callback.onCategoryLoaded(response.body().getCategoryList());
               }

               @Override
               public void onFailure(Call<CategoryResponse> call, Throwable t) {
                   callback.onDataNotAvailable();
               }
           });
    }
}
