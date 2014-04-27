package com.derdirk.hasentag;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlertManager
{
  public static void setAlert(Context context, long alertTimeMs)
  {
    Intent intent = new Intent(context, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, intent, 0);     
    
    AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);    
    alarm.set(AlarmManager.RTC, alertTimeMs, pendingServiceIntent);
  }
  
  public static void cancelAlert(Context context)
  {    
    // TODO: Don't instantiate twice?
    Intent intent = new Intent(context, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(context, 0, intent, 0);
      
    AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    alarm.cancel(pendingServiceIntent);
  }
}
