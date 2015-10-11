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
import kr.bobplanet.android.AppConstants;
import kr.bobplanet.android.EntityVault;
import kr.bobplanet.android.MainApplication;
import kr.bobplanet.android.MenuViewBaseActivity;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * Google Cloud Messaging(GCM) 처리를 위해 필요한 여러 서비스를 모아놓은 상위클래스.
 * (서비스에서 구현할 내용이 별로 많지 않아 굳이 개별 클래스로 만들 필요가 없음)
 *
 * TODO 사용자토큰의 서버전송 (향후 개인별 메시지 발송기능 지원을 위해)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
public class GcmServices implements AppConstants {
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
                String token = instanceID.getToken(GCM_SENDER_ID,
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(TAG, "GCM Registration Token: " + token);

                GcmPubSub pubSub = GcmPubSub.getInstance(this);
                for (String topic : TOPICS) {
                    pubSub.subscribe(token, "/topics/" + topic, null);
                }

                EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER_SUCCESS));
            } catch (Exception e) {
                Log.d(TAG, "Failed to complete token refresh", e);

                EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER_FAILURE));
            }
        }
    }

    /**
     * GCM 메시지 수신을 담당하는 서비스.
     * (아마도 디바이스에서 실행되는 구글의 BroadcastReceiver가 호출해주는 것으로 추정)
     * <p/>
	 * - 서버에서 받은 푸쉬메시지 내용에 기반해서 MenuViewActivity를 실행하는 notification을 생성.
	 * - 서버는 메뉴ID만 보내주고, 위 activity에 전달할 정보는 EntityVault와 ImageLoader를 이용해서 값을 채운다.
	 * - Volley를 이용해 async로 이미지를 띄울 수 있도록 ImageListener 구현.
     */
    public static class MessageListener extends GcmListenerService implements ImageLoader.ImageListener {
        private static final String TAG = MessageListener.class.getSimpleName();

        private Menu menu = null;
        private Bundle data = null;

        /**
         * GCM메시지가 도착했을 때 호출됨. 실제 수신부.
         *
         * @param from 메시지 발신자. 별로 신경안씀.
         * @param data 메시지 본문.
         */
        @Override
        public void onMessageReceived(String from, Bundle data) {
            Log.i(TAG, "GCM message received. Message = " + data.toString());

            long menuId = Long.valueOf(data.getString("menuId"));
            this.data = data;

            EntityVault entityVault = MainApplication.getInstance().getEntityVault();
            final ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

            entityVault.loadMenu(menuId, new EntityVault.OnEntityLoadListener<Menu>() {
                @Override
                public void onEntityLoad(Menu result) {
                    menu = result;
                    imageLoader.get(result.getItem().getIconURL(), MessageListener.this);
                }
            });
        }

        /**
         * 이미지가 로딩되었을 때 Volley에서 호출해주는 callback.
		 * 정확하게는 모르겠으나 두 번 호출되는 듯함. (첫번째 호출때는 image가 null이다.)
         *
         * @param response
         * @param isImmediate
         */
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                registerNotification(menu, data, bitmap);
            }
        }

        /**
         * Volley에서 이미지를 로딩하지 못했을 때 호출하는 callback.
		 * 지금까지는 한번도 호출된 적이 없음.
         *
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(TAG, "Image fetch error", error.getCause());
            registerNotification(menu, data, null);
        }

        /**
         * 실제 Notification을 생성/등록한다.
         * 
         * @param menu
         * @param data
         * @param bitmap
         */
        private void registerNotification(Menu menu, Bundle data, @Nullable Bitmap bitmap) {
            Intent intent = new Intent(this, MenuViewBaseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(KEY_MENU, menu.toString());

            String title = getDefaultString(data, "title", menu.getDate() + ": " + menu.getItem().getId());
            String message = getDefaultString(data, "message", "Get it while you can");
            String detail = data.getString("detail");

            PendingIntent pending = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			// 이미지가 있으면 BigPicture 스타일, 없으면 BigText 스타일.
            NotificationCompat.Style style = bitmap != null ?
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap).setBigContentTitle(title)
                            .setSummaryText(message) :
                    new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(title).setSummaryText(detail);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pending);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }

	    /**
	     * 서버에서 전달받은 메시지 본문을 Bundle에서 꺼내는 유틸리티 함수.
	     *
	     * @param data
	     * @param key
	     * @param defaultValue
	     * @return
	     */
	    private String getDefaultString(Bundle data, String key, String defaultValue) {
	        String value = data.getString(key);
	        return (value == null || value.length() == 0) ?
	                defaultValue : value;
	    }
    }

    /**
     * 인스턴스ID 재설정 이벤트를 수신하는 서비스.
     * (디바이스의 공장초기화나 앱 언인스톨, 앱데이터 삭제 등의 케이스에 재설정된다고 함)
     * <p/>
     * - 인스턴스ID가 달라지면 GCM 재등록이 필요하므로 Registration 서비스 호출
     */
    public static class InstanceIDListener extends InstanceIDListenerService {
        @Override
        public void onTokenRefresh() {
            Intent intent = new Intent(this, Registration.class);
            startService(intent);
        }
    }
}