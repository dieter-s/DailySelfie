package de.dieter.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by dsmorra on 17.11.2014.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        Notification.Builder mNotificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_action_camera)
                .setContentTitle(context.getText(R.string.notification_title))
                .setContentText(context.getText(R.string.notification_text))
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1,mNotificationBuilder.getNotification());

    }
}
