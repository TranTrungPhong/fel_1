package com.framgia.fel1.presentation.home;
import com.framgia.fel1.data.source.category.CategoryDataSource;
import com.framgia.fel1.data.source.category.CategoryRepository;
import com.framgia.fel1.model.Category;
import java.util.List;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public class HomePresenter implements HomeContract.Presenter {

    private final CategoryRepository mCategoryRepository;

    private final HomeContract.View mHomeView;

    public HomePresenter(CategoryRepository categoryRepository, HomeContract.View homeView) {
        mCategoryRepository = categoryRepository;
        mHomeView = homeView;
    }

    @Override
    public void getListCategory(String authToken) {
        mCategoryRepository.getCategories(authToken, new CategoryDataSource.Callback() {
            @Override
            public void onCategoryLoaded(List<Category> categoryList) {
                mHomeView.showListCategory(categoryList);
            }

            @Override
            public void onDataNotAvailable() {
                mHomeView.hideDialog();
            }
        });
    }
}
