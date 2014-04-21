package com.derdirk.hasentag;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnItemSelectedListener, OnValueChangeListener
{
  protected int    mSmallCleaningIntervalUnit  = Calendar.SECOND;
  protected int    mSmallCleaningIntervalValue = 5;
  protected int    mBigCleaningIntervalUnit    = Calendar.WEEK_OF_YEAR;
  protected int    mBigCleaningIntervalValue   = 4;
  protected long   mReferenceTimeMs            = 0;
  protected long   mAlertTimeMs                = 0;
  
  protected NumberPicker          mNumberPicker          = null;
  protected Spinner               mUnitSpinner           = null;
  protected TextView              mNextReminderTextView  = null;
  protected UnitToResourceMapping mUnitToResourceMapping = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    mNumberPicker          = (NumberPicker) findViewById(R.id.number_picker);
    mUnitSpinner           = (Spinner)      findViewById(R.id.small_unit_spinner);
    mNextReminderTextView  = (TextView)     findViewById(R.id.next_reminder_text_view);
    mUnitToResourceMapping = new UnitToResourceMapping(this);
    
    mNumberPicker.setOnValueChangedListener(this);
    mUnitSpinner.setOnItemSelectedListener(this);
    
    mNumberPicker.setMinValue(1);
    mNumberPicker.setMaxValue(1000);
    
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.units_array, android.R.layout.simple_spinner_item);
    // Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Apply the adapter to the spinner
    mUnitSpinner.setAdapter(adapter);
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    // Restore preferences
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    mSmallCleaningIntervalUnit  = settings.getInt( "SmallUnit",     Calendar.DAY_OF_YEAR);
    mSmallCleaningIntervalValue = settings.getInt( "SmallValue",    2);
    mBigCleaningIntervalUnit    = settings.getInt( "BigUnit",       Calendar.WEEK_OF_YEAR);
    mBigCleaningIntervalValue   = settings.getInt( "BigValue",      4);
    mReferenceTimeMs            = settings.getLong("ReferenceTime", 0);
    mAlertTimeMs                = settings.getLong("AlertTime",     0);
    
    // Init interval picker
    mNumberPicker.setValue(mSmallCleaningIntervalValue);
   
    // Init unit picker
    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) mUnitSpinner.getAdapter();
    String selectedItem = mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit);
    int selectedItemNr = adapter.getPosition(selectedItem);
    mUnitSpinner.setSelection(selectedItemNr);
    
    // Update reminder text
    updateNextAlertText();
  }
  
  @Override
  protected void onPause()
  {
    super.onPause();
    
    // Save preferences
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt( "SmallUnit",     mSmallCleaningIntervalUnit);
    editor.putInt( "SmallValue",    mSmallCleaningIntervalValue);
    editor.putInt( "BigUnit",       mBigCleaningIntervalUnit);
    editor.putInt( "BigValue",      mBigCleaningIntervalValue);
    editor.putLong("ReferenceTime", mReferenceTimeMs);
    editor.putLong("AlertTime",     mAlertTimeMs);
    editor.commit();
  }
  
  public void onDoneButtonPressed(View view)
  {
    cancelAlert();
    NotificationManager nm = new NotificationManager(this);
    nm.clearNotification();
    
    if (Calendar.getInstance().getTimeInMillis() < mAlertTimeMs)
      Log.d("MainActivity", "Reset reference time due to early 'done'");
    
    // Reset reference time if the done button was pressed before an alert was issued
    // or if the reference time was not set yet
    if (mReferenceTimeMs == 0 || Calendar.getInstance().getTimeInMillis() < mAlertTimeMs)
      mReferenceTimeMs = Calendar.getInstance().getTimeInMillis();
      
    mAlertTimeMs = AlarmTimeCalculator.getAlarmTime(mReferenceTimeMs, mSmallCleaningIntervalUnit, mSmallCleaningIntervalValue);
    
    setAlert(mAlertTimeMs);
    
    updateNextAlertText();
  }
  
  public void onStopButtonPressed(View view)
  {
    mReferenceTimeMs = 0;
    mAlertTimeMs     = 0;
    
    cancelAlert();
    NotificationManager nm = new NotificationManager(this);
    nm.clearNotification();
    
    updateNextAlertText();
  }

  // Number picker callback
  @Override
  public void onValueChange(NumberPicker picker, int oldVal, int newVal)
  {
    mSmallCleaningIntervalValue = newVal;
  }
  
  // Spinner callbacks 
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
  {
    mSmallCleaningIntervalUnit = mUnitToResourceMapping.getUnit((String)mUnitSpinner.getItemAtPosition(pos));
  }
  
  @Override
  public void onNothingSelected(AdapterView<?> parent)
  {}

  protected void setAlert(long alertTimeMs)
  {
    Intent intent = new Intent(this, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);     
    
    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);    
    alarm.set(AlarmManager.RTC, alertTimeMs, pendingServiceIntent);
  }
  
  protected void cancelAlert()
  {    
    // TODO: Don't instantiate twice...
    Intent intent = new Intent(this, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);
      
    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    alarm.cancel(pendingServiceIntent);
  }
  
  protected void updateNextAlertText()
  {
    if (mAlertTimeMs != 0)
      mNextReminderTextView.setText(new Date(mAlertTimeMs).toString());
    else
      mNextReminderTextView.setText(getString(R.string.next_no_reminder_text));
  }

}
