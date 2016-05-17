package com.framgia.fel1.util;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;

/**
 * Created by vuduychuong1994 on 5/17/16.
 */
public class TaskFragment extends Fragment {

    private TaskCallbacks mCallbacks;
    private DummyTask mTask;

    /**
     * Hold a reference to the parent Activity so we can report the task's current progress and
     * results. The Android framework will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    /**
     * Set the callback to null so we don't accidentally leak the Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void startInBackground(String[] param){
        mTask = new DummyTask();
        mTask.execute(param);
    }

    /**
     * A dummy task that performs some (dumb) background work and proxies progress updates and
     * results back to the Activity.
     * <p>
     * Note that we need to check if the callbacks are null in each method in case they are invoked
     * after the Activity's and Fragment's onDestroy() method have been called.
     */
    private class DummyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            if ( mCallbacks != null ) {
                mCallbacks.onPreExecute();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return mCallbacks.onBackGround(params);
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

    /**
     * Callback interface through which the fragment will report the task's progress and results
     * back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();

        String onBackGround(String[] param);

        void onProgressUpdate(String response);

        void onCancelled();

        void onPostExecute(String response);
    }
}
