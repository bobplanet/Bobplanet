package kr.bobplanet.android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.NotifyManager;
import kr.bobplanet.android.R;

/**
 * Google Cloud Messaging(GCM) 처리를 위해 필요한 여러 서비스를 모아놓은 상위클래스.
 * (서비스에서 구현할 내용이 별로 많지 않아 굳이 개별 클래스로 만들 필요가 없음)
 * <p>
 * TODO 사용자토큰의 서버전송 (향후 개인별 메시지 발송기능 지원을 위해)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
public class GcmServices implements Constants {
    /**
     * GCM 등록을 담당하는 서비스.
     * 등록완료(성공이든 실패든)시에는 GcmEvent 이벤트를 fire함.
     */
    public static class Registration extends IntentService {
        private static final String TAG = Registration.class.getSimpleName();
        private static final String[] TOPICS = {"global"};

        public Registration() {
            super(TAG);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            try {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_sender_id),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                GcmPubSub pubSub = GcmPubSub.getInstance(this);
                for (String topic : TOPICS) {
                    pubSub.subscribe(token, "/topics/" + topic, null);
                }

                EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER_SUCCESS, token));
            } catch (Exception e) {
                Log.d(TAG, "Failed to complete token refresh", e);

                EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER_FAILURE));
            }
        }
    }

    /**
     * GCM 메시지 수신을 담당하는 서비스.
     * (아마도 디바이스에서 실행되는 구글의 BroadcastReceiver가 호출해주는 것으로 추정)
     * <p>
     * - 서버에서 받은 푸쉬메시지 내용에 기반해서 MenuViewActivity를 실행하는 notification을 생성.
     * - 서버는 메뉴ID만 보내주고, 위 activity에 전달할 정보는 EntityVault와 ImageLoader를 이용해서 값을 채운다.
     * - Volley를 이용해 async로 이미지를 띄울 수 있도록 ImageListener 구현.
     */
    public static class MessageListener extends GcmListenerService {
        private static final String TAG = MessageListener.class.getSimpleName();

        /**
         * GCM메시지가 도착했을 때 호출됨. 실제 수신부.
         *
         * @param from 메시지 발신자. 별로 신경안씀.
         * @param data 메시지 본문.
         */
        @Override
        public void onMessageReceived(String from, Bundle data) {
            Log.i(TAG, "GCM message received. Message = " + data.toString());

            NotifyManager notifyManager = new NotifyManager(this);
            notifyManager.requestNextMenuNotification(data);
        }
    }

    /**
     * 인스턴스ID 재설정 이벤트를 수신하는 서비스.
     * (디바이스의 공장초기화나 앱 언인스톨, 앱데이터 삭제 등의 케이스에 재설정된다고 함)
     * <p>
     * - 인스턴스ID가 달라지면 GCM 재등록이 필요하므로 Registration 서비스 호출
     */
    public static class InstanceIDListener extends InstanceIDListenerService {
        private static final String TAG = InstanceIDListener.class.getSimpleName();

        @Override
        public void onTokenRefresh() {
            Log.i(TAG, "onTokenRefresh()");
            Intent intent = new Intent(this, Registration.class);
            startService(intent);
        }
    }
}