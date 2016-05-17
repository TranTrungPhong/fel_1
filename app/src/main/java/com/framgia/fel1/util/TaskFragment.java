package com.framgia.fel1.util;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by vuduychuong1994 on 5/17/16.
 */
public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";
    private TaskCallbacks mCallbacks;
    private DummyTask mTask;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(mCallbacks == null ) {
            Activity activity;
            if ( context instanceof Activity ) {
                activity = (Activity) context;
                mCallbacks = (TaskCallbacks) activity;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void startInBackground(String[] param) {
        mTask = new DummyTask();
        mTask.execute(param);
    }

    private class DummyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if ( mCallbacks != null ) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, params.toString());
            String response = null;
            if ( mCallbacks != null ) {
                response = mCallbacks.onBackGround(params);
            }
            return response;
        }

        @Override
        protected void onProgressUpdate(String... params) {
            if ( mCallbacks != null ) {
                mCallbacks.onProgressUpdate(params[0]);
            }
        }

        @Override
        protected void onCancelled() {
            if ( mCallbacks != null ) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if ( mCallbacks != null ) {
                mCallbacks.onPostExecute(response);
            }
        }
    }

    public interface TaskCallbacks {
        void onPreExecute();

        String onBackGround(String[] param);

        void onProgressUpdate(String response);

        void onCancelled();

        void onPostExecute(String response);
    }
}
