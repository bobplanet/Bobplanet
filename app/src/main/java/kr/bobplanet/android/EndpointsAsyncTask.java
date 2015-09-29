package kr.bobplanet.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.Daily;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
public class EndpointsAsyncTask extends AsyncTask<Void, Void, List<Daily>> {
    private static final String TAG = "EndpointsAsyncTask";
    private static BobplanetApi api = null;
    private Context context;
    private Exception exception;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Daily> doInBackground(Void ... params) {

        try {
            GoogleAuthUtil.getToken(context, "dummy@server.com",
                    "audience:server:client_id:" + AndroidConstants.ANDROID_CLIENT_ID);

            return api.list().execute().getItems();
        } catch (Exception e) {
            Log.d(TAG, "error", e);
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Daily> result) {
        if (result != null) {

        } else {
            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}