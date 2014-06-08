package com.derdirk.hasentag;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.derdirk.hasentag.MainActivity;

public class BootCompletedReceiver extends BroadcastReceiver
{

  @Override
  public void onReceive(Context context, Intent intent)
  {
    SharedPreferences settings = context.getSharedPreferences("HasenTag", Context.MODE_PRIVATE);
    long alertTimeMs = settings.getLong(MainActivity.CurrentAlertTimeSettingsTag, 0);    
    
    Log.d("BootCompletedReceiver", "alertTimeMs: " + String.valueOf(alertTimeMs));
    
    Calendar calNow = Calendar.getInstance();    
    Calendar calAlertTime = Calendar.getInstance();
    calAlertTime.setTimeInMillis(alertTimeMs);
    
    Log.d("BootCompletedReceiver", "Now: " + calNow.getTime().toString());
    Log.d("BootCompletedReceiver", "Alert Time: " + calAlertTime.getTime().toString());
    
    if (calNow.before(calAlertTime))
    {
      AlertManager.setAlert(context, alertTimeMs);
      Log.d("BootCompletedReceiver", "Set alert");
    }
    else
    {
      NotificationManager.setNotification(context);
      Log.d("BootCompletedReceiver", "Set notification");
    }
  }

}
