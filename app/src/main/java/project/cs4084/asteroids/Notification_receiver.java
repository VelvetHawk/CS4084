package project.cs4084.asteroids;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

// needs to be extended with BroadcastReceiver class and methods implemented
public class Notification_receiver extends BroadcastReceiver {

    // Called when class gets triggered
    @Override
    public void onReceive(Context context, Intent intent) {

        // Need to make instance of Notificiation manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // When user clicks, redirect to repeating activity
        Intent repeating_intent = new Intent(context, LoginActivity.class);

        // Ensure activity being called will replace the same old activity in the case of old activity already open
        // or running in the background
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.arrow_up_float)
                .setContentTitle("Asteroids")
                .setContentText("Get your daily dose of entertainment!")
                .setAutoCancel(true);

        // give unique number
        notificationManager.notify(100, builder.build());
    }
}
