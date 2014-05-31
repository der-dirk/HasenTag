package com.derdirk.hasentag;

import java.util.Calendar;
import java.util.Date;

public class DateFormat
{
  public static String format(Date date, int intervalUnit)
  {
    if (intervalUnit == Calendar.WEEK_OF_YEAR || intervalUnit == Calendar.DAY_OF_YEAR)
      return java.text.DateFormat.getDateInstance().format(date);
    else
      return java.text.DateFormat.getDateTimeInstance().format(date);
  }
}
