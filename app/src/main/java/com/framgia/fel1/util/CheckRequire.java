package com.framgia.fel1.util;
import android.content.Context;
import android.util.Patterns;
import android.widget.TextView;
import com.framgia.fel1.R;
import com.framgia.fel1.constant.Const;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vuduychuong1994 on 4/25/16.
 */
public class CheckRequire {

    public static boolean checkEmail(Context context, TextView mEditEmail) {
        if (Patterns.EMAIL_ADDRESS.matcher(mEditEmail.getText().toString()).matches()) {
            return true;
        } else {
            mEditEmail.setError(context.getResources().getString(R.string.error_email));
            return false;
        }
    }

    public static boolean checkPassword(Context context, TextView mEditPassword,
                                        TextView mEditPasswordConfirmation) {
        if (mEditPassword.getText().toString().length() < Const.PASSWORD_MIN_LENGTH) {
            mEditPassword.setError(
                    context.getResources().getString(R.string.error_length_password));
            return false;
        } else {
            Pattern passwordPattern =
                    Pattern.compile(mEditPassword.getText().toString(), Pattern.CASE_INSENSITIVE);
            Matcher matcher =
                    passwordPattern.matcher(mEditPasswordConfirmation.getText().toString());
            if (!matcher.matches()) {
                mEditPasswordConfirmation.setError(
                        context.getResources().getString(R.string.error_password_confimation));
                return false;
            } else {
                return true;
            }
        }
    }
}
