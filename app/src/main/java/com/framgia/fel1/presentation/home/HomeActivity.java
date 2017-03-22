package com.framgia.fel1.presentation.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.fel1.R;
import com.framgia.fel1.activity.CategoryActivity;
import com.framgia.fel1.activity.LoginActivity;
import com.framgia.fel1.activity.UpdateProfileActivity;
import com.framgia.fel1.activity.UserActionActivity;
import com.framgia.fel1.activity.WordListActivity;
import com.framgia.fel1.adapter.CategoryAdapter;
import com.framgia.fel1.base.BaseActivity;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.constant.NetwordConst;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.data.source.category.CategoryRepository;
import com.framgia.fel1.data.source.category.remote.CategoryRemoteDataSource;
import com.framgia.fel1.model.Category;
import com.framgia.fel1.model.User;
import com.framgia.fel1.util.BitmapUtil;
import com.framgia.fel1.util.HttpRequest;
import com.framgia.fel1.util.InternetUtils;
import com.framgia.fel1.util.ShowImage;
import com.framgia.fel1.util.TaskFragment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by PhongTran on 04/15/2016.
 */
public class HomeActivity extends BaseActivity
        implements View.OnClickListener, CategoryAdapter.OnListCategoryClickItem, NavigationView.OnNavigationItemSelectedListener,
        HomeContract.View {

    private static final String ISCATEGORY = "ISCATEGORY";
    private static final String CONTENT_BITMAP = "bitmap";
    private CircleImageView mImageViewAvatar;
    private TextView mTextViewName;
    private TextView mTextViewEmail;
    private RecyclerView mRecyclerViewCategory;
    private CategoryAdapter mCategoryAdapter;
    private ArrayList<Category> mListCategory;
    private String mAuthToken;
    private User mUser;
    private Toast mToast;
    private static boolean sIsCategoryLoad;
    private boolean mIsLoadImage;
    private Bitmap mBitmapAvatar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private HomeContract.Presenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = new HomePresenter(
                CategoryRepository.getInstance(CategoryRemoteDataSource.getInstance()), this, this);
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.drawer_close);
        setNavigation();
        mRecyclerViewCategory = (RecyclerView) findViewById(R.id.listview_lesson_learned);
        mRecyclerViewCategory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mListCategory = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(this, mListCategory);
        mRecyclerViewCategory.setAdapter(mCategoryAdapter);
    }

    private void setNavigation() {
        mNavigationView = (NavigationView) findViewById(R.id.home_navigation);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                        R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        View view = mNavigationView.getHeaderView(0);
        mImageViewAvatar = (CircleImageView) view.findViewById(R.id.image_show_user_avatar);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mImageViewAvatar.setLayerPaint(paint);
        }
        mTextViewName = (TextView) view.findViewById(R.id.text_show_user_name);
        mTextViewEmail = (TextView) view.findViewById(R.id.text_show_user_email);
    }

    private void initData() {
        mUser = mPresenter.getUser();
        if (mUser == null) finish();

        if (!mIsLoadImage && InternetUtils.isInternetConnected(HomeActivity.this, false)) {
            mIsLoadImage = true;
            if (!TextUtils.isEmpty(mUser.getAvatar())) {
                new ShowImage(mImageViewAvatar).execute(mUser.getAvatar());
            }
        }
        mTextViewName.setText(mUser.getName());
        mTextViewEmail.setText(mUser.getEmail());
        mAuthToken = mUser.getAuthToken();
        if (!sIsCategoryLoad) {
            showDialog();
            mPresenter.getListCategory(mAuthToken);
        }
        mImageViewAvatar.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(mUser.getAvatar(), Const.DEFAULT_REQ,
                Const.DEFAULT_REQ);
        if (bitmap != null) mImageViewAvatar.setImageBitmap(bitmap);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_show_user_avatar) {
            startActivity(new Intent(HomeActivity.this, UpdateProfileActivity.class));
            sIsCategoryLoad = false;
        }
    }

    @Override
    public void showSignOutDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.infor).setMessage(R.string.confirrn_signout)
             .setPositiveButton(R.string.ok,
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(
                                 DialogInterface dialog, int which) {
                             if (InternetUtils.isInternetConnected(HomeActivity.this)) {
                                 mPresenter.logOut(mAuthToken);
                             } else {
                                 mPresenter.startLoginActivity();
                                 sIsCategoryLoad = false;
                             }
                         }
                     })
             .setNegativeButton(R.string.cancel, null)
             .show();
    }

    @Override
    public void onListCategoryClickItem(int position, Category category) {
        Intent intentLessonLearned = new Intent(HomeActivity.this, CategoryActivity.class);
        intentLessonLearned.putExtra(Const.AUTH_TOKEN, mAuthToken);
        intentLessonLearned.putExtra(Const.ID, mListCategory.get(position).getId());
        intentLessonLearned.putExtra(Const.NAME, mListCategory.get(position).getName());
        intentLessonLearned.putExtra(Const.USER, mUser);
        startActivity(intentLessonLearned);
        mPresenter.putPreference(Const.CATEGORY_ID, mListCategory.get(position).getId());
    }

    @Override
    public void onBackPressed() {
        if (mToast == null) {
            mToast = Toast.makeText(HomeActivity.this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT);
        }
        if (mToast.getView().isShown()) {
            super.onBackPressed();
            sIsCategoryLoad = false;
        } else {
            mToast.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ISCATEGORY, sIsCategoryLoad);
        outState.putSerializable(Const.LIST, mListCategory);
        if (mIsLoadImage)
            mBitmapAvatar = ((BitmapDrawable) mImageViewAvatar.getDrawable()).getBitmap();
        if (mBitmapAvatar != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mBitmapAvatar.compress(Bitmap.CompressFormat.PNG, Const.DEFAULT_REQ, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            outState.putByteArray(CONTENT_BITMAP, byteArray);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mListCategory.clear();
        mListCategory.addAll((ArrayList<Category>) savedInstanceState.getSerializable(Const.LIST));
        byte[] byteArray = savedInstanceState.getByteArray(CONTENT_BITMAP);
        if (byteArray != null)
            mBitmapAvatar = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (mBitmapAvatar != null) mImageViewAvatar.setImageBitmap(mBitmapAvatar);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(mNavigationView);
        sIsCategoryLoad = false;
        switch (item.getItemId()) {
            case R.id.nav_word_list:
                Intent intentWordList = new Intent(HomeActivity.this, WordListActivity.class);
                startActivity(intentWordList);
                break;
            case R.id.nav_activities:
                Intent intentActivities = new Intent(HomeActivity.this, UserActionActivity.class);
                startActivity(intentActivities);
                break;
            case R.id.nav_signout:
                showSignOutDialog();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void showListCategory(List<Category> categoryList) {
        dismissDialog();
        mListCategory.clear();
        mListCategory.addAll(categoryList);
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideDialog() {
        dismissDialog();
    }
}
