package kr.bobplanet;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import kr.bobplanet.backend.dailyApi.DailyApi;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
class EndpointsAsyncTask extends AsyncTask<Void, Void, String> {
    private static DailyApi dailyApi = null;
    private Context context;

    EndpointsAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void ... params) {
        if(dailyApi == null) {  // Only do this once
   /*         DailyApi.Builder builder = new DailyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });*/
            // end options for devappserver

            DailyApi.Builder builder = new DailyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://kr-bobplanet.appspot.com/_ah/api/");

            dailyApi = builder.build();
        }

        try {
            return dailyApi.get(2184L).execute().getName();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}