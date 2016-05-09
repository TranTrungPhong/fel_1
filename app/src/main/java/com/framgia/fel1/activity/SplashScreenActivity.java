package com.framgia.fel1.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.Const;
import com.framgia.fel1.data.MySqliteHelper;
import com.framgia.fel1.model.User;


/**
 * Created by PhongTran on 04/06/2016.
 */
public class SplashScreenActivity extends Activity {
    private ProgressBar mProgressBarSplash;
    private int mStatusProgrees;
    private Handler mHandlerProgrees = new Handler();
    private static final int MAX_PROGRESSBAR = 100;
    private static final int PROGRESSBAR = 0;
    private MySqliteHelper mMySqliteHelper;
    private Intent mIntent;
    private SharedPreferences mSharedPreferences;
    private boolean mRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        initView();
        mMySqliteHelper = new MySqliteHelper(this);
        mSharedPreferences = getSharedPreferences(Const.MY_PREFERENCE, Context.MODE_PRIVATE);
        mRemember = mSharedPreferences.getBoolean(Const.REMEMBER, false);
        loadProgreesBar();
    }

    private void loadProgreesBar() {
        mProgressBarSplash.setProgress(PROGRESSBAR);
        mProgressBarSplash.setMax(MAX_PROGRESSBAR);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mStatusProgrees < MAX_PROGRESSBAR) {
                    mStatusProgrees = loadStatus();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandlerProgrees.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBarSplash.setProgress(mStatusProgrees);
                        }
                    });
                }
                if (mStatusProgrees >= MAX_PROGRESSBAR) {
                    if(mRemember){
                        mIntent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    }else {
                        mIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    }
                        startActivity(mIntent);
                        finish();
                }
            }
        }).start();
    }

    private int loadStatus() {
        if (mStatusProgrees < MAX_PROGRESSBAR) {
            mStatusProgrees++;
        }
        return mStatusProgrees;
    }

    private void initView() {
        mProgressBarSplash = (ProgressBar) findViewById(R.id.progressbar_splashscreen);
    }
}
