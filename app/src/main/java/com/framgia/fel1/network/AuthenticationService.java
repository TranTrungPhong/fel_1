package com.framgia.fel1.network;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.model.SignOutResponse;
import com.framgia.fel1.data.source.category.CategoryResponse;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public interface AuthenticationService {

    @DELETE("/logout.json")
    Call<SignOutResponse> signOut(@Query(Const.AUTH_TOKEN) String authToken);
}
