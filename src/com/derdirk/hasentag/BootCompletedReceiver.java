package com.derdirk.hasentag;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    SharedPreferences settings = context.getSharedPreferences("HasenTag", Context.MODE_PRIVATE);
    long alertTimeMs = settings.getLong("AlertTime", 0);    
    
    Log.d("BootCompletedReceiver", "alertTimeMs: " + String.valueOf(alertTimeMs));
    
    Calendar calNow = Calendar.getInstance();    
    Calendar calAlertTime = Calendar.getInstance();
    calAlertTime.setTimeInMillis(alertTimeMs);
    
    Log.d("BootCompletedReceiver", "Now: " + calNow.getTime().toString());
    Log.d("BootCompletedReceiver", "Alert Time: " + calAlertTime.getTime().toString());
    
    if (calNow.after(calAlertTime))
    {
      NotificationManager nm = new NotificationManager(context);
      nm.setNotification(nm.buildNotification());
      
      Log.d("BootCompletedReceiver", "Set notification");
    }
  }

}
