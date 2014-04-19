package com.derdirk.hasentag;

import java.util.Calendar;

import android.os.SystemClock;
import android.util.Log;

public class AlarmTimeCalculator
{

  public static long getAlarmTime(int field, int value, StringBuilder alertTimeString)
  {
    long elapsedTimeNowMs = SystemClock.elapsedRealtime();

    Calendar cal = Calendar.getInstance();
    cal.setFirstDayOfWeek(Calendar.MONDAY);
    
    // Get current time 
    long nowMs = cal.getTimeInMillis();
    Log.d("AlarmTimeCalculator", "Now: " + cal.getTime().toString());
    
    // Calculate alter time
    cal.add(field, value);
    Log.d("AlarmTimeCalculator", "Then: " + cal.getTime().toString());

    // Set to beginning of the period
    if (field == Calendar.WEEK_OF_YEAR)
    {
      cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
    }
    else if (field == Calendar.DAY_OF_YEAR)
    {
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
    }    
    long thenAdjustedMs = cal.getTimeInMillis();
    Log.d("AlarmTimeCalculator", "Then adjusted: " + cal.getTime().toString());
    
    // Calculate interval
    long alarmIntervalMs = thenAdjustedMs - nowMs;
    
    // Return Alarm time and alert time string
    alertTimeString.append(cal.getTime().toString());
    return elapsedTimeNowMs + alarmIntervalMs;
  }
}
