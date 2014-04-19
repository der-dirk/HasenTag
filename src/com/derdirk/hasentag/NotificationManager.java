package com.derdirk.hasentag;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationManager
{
  private static int NOTIFICATION_ID = 12;
 
  private Context mApplicationContext;
  
  public NotificationManager(Context applicationContext)
  {
    mApplicationContext = applicationContext;
  }

  public Notification buildNotification()
  {
    // Prepare intent which is triggered if the
    // notification is selected
    Intent intent = new Intent(mApplicationContext, MainActivity.class);
    PendingIntent pIntent = PendingIntent.getActivity(mApplicationContext, 0, intent, 0);

    // Build notification
    // Actions are just fake
    Notification noti = new Notification.Builder(mApplicationContext)
    .setContentTitle(mApplicationContext.getString(R.string.notification_title))
    .setContentText(mApplicationContext.getString(R.string.notification_message))
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
  
  public void setNotification(Notification notification)
  {
    android.app.NotificationManager notificationManager = (android.app.NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(NOTIFICATION_ID, notification);
  }
  
  public void clearNotification()
  {
    android.app.NotificationManager notificationManager = (android.app.NotificationManager) mApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NOTIFICATION_ID);
  }
}
