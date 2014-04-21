package com.derdirk.hasentag;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class AlarmTimeCalculator
{

  public static long getAlarmTime(long referenceTimeMs, int intervalUnit, int intervalValue)
  {
    Log.d("AlarmTimeCalculator", "Reference time: " + new Date(referenceTimeMs).toString());
    
    // Get current time and setup calendar
    
    Calendar calNow = Calendar.getInstance();    
    Log.d("AlarmTimeCalculator", "Now: " + calNow.getTime().toString());
    
    // Calculate alert time

    Calendar calAlertTime = Calendar.getInstance();
    calAlertTime.setTimeInMillis(referenceTimeMs);
     
    if (calAlertTime.equals(calNow))
      calAlertTime.add(intervalUnit, intervalValue);
    
    while(calAlertTime.before(calNow))
    {
      calAlertTime.add(intervalUnit, intervalValue);
    }

    Log.d("AlarmTimeCalculator", "Then: " + calAlertTime.getTime().toString());
   
    // Set to beginning of the period
    
    if (intervalUnit == Calendar.WEEK_OF_YEAR)
    {
      calAlertTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
      calAlertTime.set(Calendar.HOUR_OF_DAY, 0);
      calAlertTime.set(Calendar.MINUTE, 0);
      calAlertTime.set(Calendar.SECOND, 0);
    }
    else if (intervalUnit == Calendar.DAY_OF_YEAR)
    {
      calAlertTime.set(Calendar.HOUR_OF_DAY, 0);
      calAlertTime.set(Calendar.MINUTE, 0);
      calAlertTime.set(Calendar.SECOND, 0);
    }    
    long thenAdjustedMs = calAlertTime.getTimeInMillis();
    Log.d("AlarmTimeCalculator", "Then adjusted: " + calAlertTime.getTime().toString());
    
    // Return Alarm time
    return thenAdjustedMs;
  }
}
