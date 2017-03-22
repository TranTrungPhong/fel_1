package com.framgia.fel1.network;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.source.category.CategoryResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public interface APIService {

    @GET("/categories.json")
    Call<CategoryResponse> getCategories(@Query(Const.AUTH_TOKEN) String authToken);
}
