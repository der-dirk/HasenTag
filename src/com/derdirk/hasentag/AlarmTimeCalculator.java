package com.derdirk.hasentag;

import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class AlarmTimeCalculator
{

  public static long getAlarmTime(long referenceTimeMs, int intervalUnit, int intervalValue)
  {
    Log.d("AlarmTimeCalculator", "getAlarmTime: Reference time: " + new Date(referenceTimeMs).toString());
    
    // Get current time and setup calendar
    
    Calendar calNow = Calendar.getInstance();    
    Log.d("AlarmTimeCalculator", "getAlarmTime: Now: " + calNow.getTime().toString());
    
    // Calculate alert time

    Calendar calAlarmTime = Calendar.getInstance();
    calAlarmTime.setTimeInMillis(referenceTimeMs);
     
    calAlarmTime = adjustTimeToInterval(calAlarmTime, intervalUnit);
    
    while(!calAlarmTime.after(calNow))
    {
      calAlarmTime.add(intervalUnit, intervalValue);
      calAlarmTime = adjustTimeToInterval(calAlarmTime, intervalUnit);
    }

    long alarmTimeAdjustedMs = calAlarmTime.getTimeInMillis();
    Log.d("AlarmTimeCalculator", "getAlarmTime: Then adjusted: " + calAlarmTime.getTime().toString());
    
    // Return Alarm time
    return alarmTimeAdjustedMs;
  }
  
  // For day or week intervals: Set the time to the beginning of the interval
  private static Calendar adjustTimeToInterval(Calendar calTime, int intervalUnit)
  {    
    if (intervalUnit == Calendar.WEEK_OF_YEAR)
    {
      calTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
      calTime.set(Calendar.HOUR_OF_DAY, 0);
      calTime.set(Calendar.MINUTE, 0);
      calTime.set(Calendar.SECOND, 0);
    }
    else if (intervalUnit == Calendar.DAY_OF_YEAR)
    {
      calTime.set(Calendar.HOUR_OF_DAY, 0);
      calTime.set(Calendar.MINUTE, 0);
      calTime.set(Calendar.SECOND, 0);
    }
    
    return calTime;
  }
}
