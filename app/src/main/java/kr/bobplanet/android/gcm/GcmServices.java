package kr.bobplanet.android.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import java.io.InputStream;
import java.net.URL;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.AppConstants;
import kr.bobplanet.android.EntityVault;
import kr.bobplanet.android.MainApplication;
import kr.bobplanet.android.MenuViewActivity;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * Google Cloud Messaging(GCM) 처리를 위해 필요한 여러 서비스를 모아놓은 상위클래스.
 * (서비스에서 구현할 내용이 별로 많지도 않아 굳이 개별 클래스로 만들 필요가 없음)
 *
 * @author heonkyu.jin
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
     * - 현재는 notification bar에 메시지 내용을 뿌려주는 정도만 구현되어 있음
     */
    public static class MessageListener extends GcmListenerService implements ImageLoader.ImageListener {
        private static final String TAG = MessageListener.class.getSimpleName();

        private Menu menu = null;
        private Bundle data = null;

        @Override
        public void onMessageReceived(String from, Bundle data) {
            Log.i(TAG, "GCM message = " + data.toString());

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

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                registerNotification(menu, data, bitmap);
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.w(TAG, "Image fetch error", error.getCause());
            registerNotification(menu, data, null);
        }

        private String getDefaultString(Bundle data, String key, String defaultValue) {
            String value = data.getString(key);
            return (value == null || value.length() == 0) ?
                    defaultValue : value;
        }

        private void registerNotification(Menu menu, Bundle data, @Nullable Bitmap bitmap) {
            Log.i(TAG, "Notification: menu = " + menu);
            Log.i(TAG, "bitmap = " + bitmap);

            Intent intent = new Intent(this, MenuViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(MENU_ARGUMENT, menu.toString());

            String title = getDefaultString(data, "title", menu.getDate() + ": " + menu.getItem().getId());
            String message = getDefaultString(data, "message", "Get it while you can");
            String detail = data.getString("detail");

            PendingIntent pending = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

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
            //EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER));
            Intent intent = new Intent(this, Registration.class);
            startService(intent);
        }
    }
}