package kr.bobplanet.android;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.security.GeneralSecurityException;

/**
 * Google API 결과값의 integrity를 확인하는 클래스.
 * 현재는 사용하지 않음 (계속 사용할 일 없을 경우 제거 예정)
 *
 * @author heonkyu.jin
 * @version 15. 9. 29
 */
public class ApiAuthenticator {
    private static final String TAG = ApiAuthenticator.class.getSimpleName();

    private final String clientID;
    private final String audience;
    private final GoogleIdTokenVerifier verifier;

    public ApiAuthenticator(String clientID, String audience) {
        this.clientID = clientID;
        this.audience = audience;

        verifier = new GoogleIdTokenVerifier.Builder(
                AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory()).build();
    }

    public GoogleIdToken.Payload check(String tokenString) {
        try {
            GoogleIdToken token = GoogleIdToken.parse(verifier.getJsonFactory(), tokenString);
            if (verifier.verify(token)) {
                GoogleIdToken.Payload payload = token.getPayload();

                if (!payload.getAudience().equals(audience)) {
                    throw new GeneralSecurityException("Audience mismatch");
                } else if (!clientID.equals(payload.getAuthorizedParty())) {
                    throw new GeneralSecurityException("Client ID mismatch");
                }

                return payload;
            } else {
                throw new GeneralSecurityException("Token verification failure");
            }
        } catch (Exception e) {
            Log.d(TAG, "check error", e);
            return null;
        }
    }
}
