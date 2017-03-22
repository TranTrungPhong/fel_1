package com.framgia.fel1.base;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.framgia.fel1.R;

/**
 * Created by vuduychuong1994 on 3/22/17.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected ProgressDialog mDialog;

    public abstract void initViews();

    public abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViews();
    }

    public void showUserMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showUserMessage(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    public void showDialog() {
        if (mDialog == null) initDialog();
        if (!mDialog.isShowing()) mDialog.show();
    }

    public void dismissDialog() {
        if (mDialog == null) return;
        if (mDialog.isShowing()) mDialog.dismiss();
    }

    private void initDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(getString(R.string.dialog_loading_message));
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
    }
}
