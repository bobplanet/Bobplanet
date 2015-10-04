package kr.bobplanet.android.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.AppConstants;
import kr.bobplanet.android.DailyViewActivity;
import kr.bobplanet.android.R;

public class GcmServices implements AppConstants {
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

    public static class MessageListener extends GcmListenerService {
        private static final String TAG = MessageListener.class.getSimpleName();

        @Override
        public void onMessageReceived(String from, Bundle data) {
            String message = data.getString("message");
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Message: " + message);

            sendNotification(message);
        }

        private void sendNotification(String message) {
            Intent intent = new Intent(this, DailyViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pending = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pending_up = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pending_down = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
/*
                    .addAction(R.drawable.ic_thumb_up_black_24dp, R.string.action_thumb_up,
                            intent.putExtra(DAILY_VIEW_ACTION, ACTION_THUMB_UP))
*/
                    .setContentIntent(pending);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }

    }

    public static class InstanceIDListener extends InstanceIDListenerService {
        @Override
        public void onTokenRefresh() {
            //EventBus.getDefault().post(new GcmEvent(GcmEvent.REGISTER));
            Intent intent = new Intent(this, Registration.class);
            startService(intent);
        }
    }
}