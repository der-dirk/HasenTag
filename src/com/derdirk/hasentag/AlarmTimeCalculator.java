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

    Calendar calAlertTime = Calendar.getInstance();
    calAlertTime.setTimeInMillis(referenceTimeMs);
     
    if (calAlertTime.equals(calNow))
      calAlertTime.add(intervalUnit, intervalValue);
    
    while(calAlertTime.before(calNow))
    {
      calAlertTime.add(intervalUnit, intervalValue);
    }

    Log.d("AlarmTimeCalculator", "getAlarmTime: Then: " + calAlertTime.getTime().toString());
   
    // Set to beginning of the period
    calAlertTime = adjustTimeToInterval(calAlertTime, intervalUnit);
     
    long thenAdjustedMs = calAlertTime.getTimeInMillis();
    Log.d("AlarmTimeCalculator", "getAlarmTime: Then adjusted: " + calAlertTime.getTime().toString());
    
    // Return Alarm time
    return thenAdjustedMs;
  }
  
  public static long getReferenceTime(long timeMs, int intervalUnit)
  {
    Calendar calReferenceTime = Calendar.getInstance();
    calReferenceTime.setTimeInMillis(timeMs);    
    Log.d("AlarmTimeCalculator", "getReferenceTime: Time: " + calReferenceTime.getTime().toString());
    
    calReferenceTime = adjustTimeToInterval(calReferenceTime, intervalUnit);    
    Log.d("AlarmTimeCalculator", "getReferenceTime: Reference time: " + calReferenceTime.getTime().toString());
    
    return calReferenceTime.getTimeInMillis();
  }
  
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
