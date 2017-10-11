package com.juan.mezclar;

import android.os.AsyncTask;

/**
 * Created by Juan on 11/10/2017.
 */

//Esta clase no la uso. Es solo un esqueleto para insertarlo como private en una activity

public class FtpAsyncTask extends AsyncTask<String, Integer, String> {
    public FtpAsyncTask() {
        super();
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String o) {
        super.onCancelled(o);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
