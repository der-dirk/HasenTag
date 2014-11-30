package com.derdirk.hasentag;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationManager
{
  private static int NOTIFICATION_ID = 12;
  
  public static void setNotification(Context context)
  {
    android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_ID, buildNotification(context));
  }
  
  public static void clearNotification(Context context)
  {
    android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NOTIFICATION_ID);
  }
  
  protected static Notification buildNotification(Context context)
  {
    // Prepare intent which is triggered if the
    // notification is selected
    Intent intent = new Intent(context, MainActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

    // Build notification
    // Actions are just fake
    Notification noti = new NotificationCompat.Builder(context)
    .setContentTitle(context.getString(R.string.notification_title))
    .setContentText(context.getString(R.string.notification_message_text))
    .setSmallIcon(R.drawable.icon_rabbit)
    .setContentIntent(pIntent)
//    .addAction(R.drawable.ic_launcher, "Call", pIntent)
//    .addAction(R.drawable.ic_launcher, "More", pIntent)
//    .addAction(R.drawable.ic_launcher, "And more", pIntent)
    .build();
    
    // hide the notification after its selected
    noti.flags |= Notification.FLAG_NO_CLEAR;
    
    return noti;
  }  
}
