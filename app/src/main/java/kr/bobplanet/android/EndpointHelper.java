package kr.bobplanet.android;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import kr.bobplanet.backend.bobplanetApi.BobplanetApi;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class EndpointHelper {
    public synchronized static BobplanetApi getAPI() {
        // Only do this once
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
        BobplanetApi.Builder builder = new BobplanetApi.Builder(
                AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl("https://kr-bobplanet.appspot.com/_ah/api/");

        return builder.build();
    }
}
