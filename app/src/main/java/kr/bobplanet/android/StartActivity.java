package kr.bobplanet.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.gcm.GcmEvent;
import kr.bobplanet.android.gcm.GcmServices;

/**
 *
 */
public class StartActivity extends ActivitySkeleton {
    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
        if (checkPlayServices()) {
            startService(new Intent(this, GcmServices.Registration.class));
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(HAS_LAUNCHED_BEFORE, false)) {
            onFirstRun();
            prefs.edit().putBoolean(HAS_LAUNCHED_BEFORE, true).commit();
        }

        startActivity(new Intent(this, DailyViewActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    void onFirstRun() {
        new InitTask().execute();
    }

    public void onEvent(GcmEvent event) {
        switch (event.getType()) {
            case GcmEvent.REGISTER_SUCCESS:
                Log.d(TAG, "gcm register succeeded");
                break;
            case GcmEvent.REGISTER_FAILURE:
                Log.d(TAG, "gcm register failed");
                break;
        }
    }

    private class InitTask extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(StartActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Loading in progress");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            publishProgress(10);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }
    }
}
