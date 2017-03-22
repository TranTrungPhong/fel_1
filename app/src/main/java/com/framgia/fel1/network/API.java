package com.framgia.fel1.network;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public abstract class API {
    private static final String BASE_URL = "https://manh-nt.herokuapp.com/";
    private static OkHttpClient.Builder sHttpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder sBuilder =
            new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit sRetrofit = sBuilder.build();

    public static <S> S createService(
            Class<S> serviceClass) {
        sBuilder.client(sHttpClient.build());
        sRetrofit = sBuilder.build();
        return sRetrofit.create(serviceClass);
    }
}
