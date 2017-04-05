package com.framgia.fel1.presentation.home;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.framgia.fel1.activity.LoginActivity;
import com.framgia.fel1.base.BaseActivity;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.data.model.SignOutResponse;
import com.framgia.fel1.data.source.category.CategoryDataSource;
import com.framgia.fel1.data.source.category.CategoryRepository;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import com.framgia.fel1.network.AuthenticationService;
import com.framgia.fel1.network.ServiceGenerator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public class HomePresenter implements HomeContract.Presenter {

    private final CategoryRepository mCategoryRepository;

    private final HomeContract.View mView;

    private BaseActivity mActivity;

    private SharedPreferences mSharedPreferences;
    private MySqliteHelper mMySqliteHelper;

    public HomePresenter(
            CategoryRepository categoryRepository, HomeContract.View view, BaseActivity activity) {
        mCategoryRepository = categoryRepository;
        mView = view;
        mActivity = activity;
        mSharedPreferences =
                activity.getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        mMySqliteHelper = new MySqliteHelper(activity);
    }

    @Override
    public void getListCategory(String authToken) {
        mCategoryRepository.getCategories(authToken, new CategoryDataSource.Callback() {
            @Override
            public void onCategoryLoaded(List<Category> categoryList) {
                mView.showListCategory(categoryList);
            }

            @Override
            public void onDataNotAvailable() {
                mView.hideDialog();
            }
        });
    }

    @Override
    public void logOut(String authToken) {
        ServiceGenerator.createService(AuthenticationService.class).signOut(authToken)
                        .enqueue(new Callback<SignOutResponse>() {
                            @Override
                            public void onResponse(
                                    Call<SignOutResponse> call,
                                    Response<SignOutResponse> response) {
                                mView.hideDialog();
                                if (response.body() == null) return;
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putBoolean(Const.REMEMBER, false);
                                editor.remove(Const.ID);
                                editor.apply();
                                startLoginActivity();
                            }

                            @Override
                            public void onFailure(Call<SignOutResponse> call, Throwable t) {
                                mView.hideDialog();
                            }
                        });
    }

    @Override
    public void startLoginActivity() {
        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
        mActivity.finish();
    }

    @Override
    public User getUser() {
        int id = mSharedPreferences.getInt(Const.ID, -1);
        if (id == -1) return null;
        return mMySqliteHelper.getUser(id);
    }

    @Override
    public void putPreference(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
}
