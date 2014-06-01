package com.derdirk.hasentag;

import java.util.Calendar;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class UnitToResourceMapping
{
  protected Context             mApplicationContext          = null;       
  protected SparseIntArray      mUnitToResourceIdMapping     = null;
  protected SparseArray<String> mResourceIdToResourceMapping = null;
  
  public UnitToResourceMapping(Context applicationContext)
  {
    mApplicationContext = applicationContext;
    
    mUnitToResourceIdMapping = new SparseIntArray();
    mUnitToResourceIdMapping.put(Calendar.SECOND,       R.string.interval_seconds);
    mUnitToResourceIdMapping.put(Calendar.DAY_OF_YEAR,  R.string.interval_days);
    mUnitToResourceIdMapping.put(Calendar.WEEK_OF_YEAR, R.string.interval_weeks);
    
    mResourceIdToResourceMapping = new SparseArray<String>();
    mResourceIdToResourceMapping.put(R.string.interval_seconds, applicationContext.getString(R.string.interval_seconds));
    mResourceIdToResourceMapping.put(R.string.interval_days,    applicationContext.getString(R.string.interval_days));
    mResourceIdToResourceMapping.put(R.string.interval_weeks,   applicationContext.getString(R.string.interval_weeks));
  }

  public String getResource(int calendarUnit)
  {
    return mResourceIdToResourceMapping.get(getResourceId(calendarUnit));
  }
  
  public int getResourceId(int calendarUnit)
  {
    return mUnitToResourceIdMapping.get(calendarUnit);
  }
  
  public int getUnit(int resourceId)
  {
    return mUnitToResourceIdMapping.keyAt(mUnitToResourceIdMapping.indexOfValue(resourceId));
  }
  
  public int getUnit(String resourceString)
  {
    return getUnit(mResourceIdToResourceMapping.keyAt(mResourceIdToResourceMapping.indexOfValue(resourceString)));
  }
  
}
