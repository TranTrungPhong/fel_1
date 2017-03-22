package com.framgia.fel1.presentation.home;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import java.util.List;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public class HomeContract {
    interface View {

        void showListCategory(List<Category> categoryList);

        void hideDialog();

        void showSignOutDialog();
    }

    interface Presenter {

        void getListCategory(String authToken);

        void logOut(String authToken);

        void startLoginActivity();

        User getUser();

        void putPreference(String key, int value);
    }
}
