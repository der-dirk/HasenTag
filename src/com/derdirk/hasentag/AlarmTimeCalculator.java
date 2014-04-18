package com.derdirk.hasentag;

import java.util.Calendar;

import android.os.SystemClock;

public class AlarmTimeCalculator
{

  public static long getAlarmTime(int field, int value)
  {
    long elapsedTimeNowMs = SystemClock.elapsedRealtime();
    
    Calendar cal = Calendar.getInstance();  
    long nowMs = cal.getTimeInMillis();
    cal.add(field, value);
    long thenMs = cal.getTimeInMillis();
    long alarmIntervallMs = thenMs - nowMs;
    
    return elapsedTimeNowMs + alarmIntervallMs;
  }
}
