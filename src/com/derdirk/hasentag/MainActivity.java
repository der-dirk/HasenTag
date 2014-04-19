package com.derdirk.hasentag;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
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
  protected int mSmallCleaningIntervalUnit  = Calendar.SECOND;
  protected int mSmallCleaningIntervalValue = 5;
  protected int mBigCleaningIntervalUnit    = Calendar.WEEK_OF_YEAR;
  protected int mBigCleaningIntervalValue   = 4;
  
  protected String mNextReminderString = null;
  
  protected NumberPicker          mNumberPicker          = null;
  protected Spinner               mUnitSpinner           = null;
  protected TextView              mNextReminderTextView  = null;
  protected UnitToResourceMapping mUnitToResourceMapping = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    mNumberPicker          = (NumberPicker) findViewById(R.id.number_picker);
    mUnitSpinner           = (Spinner)  findViewById(R.id.small_unit_spinner);
    mNextReminderTextView  = (TextView) findViewById(R.id.next_reminder_text_view);
    mUnitToResourceMapping = new UnitToResourceMapping(this);
    
    mNumberPicker.setOnValueChangedListener(this);
    mUnitSpinner.setOnItemSelectedListener(this);
    
    mNumberPicker.setMinValue(1);
    mNumberPicker.setMaxValue(1000);
    
    // Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.units_array, android.R.layout.simple_spinner_item);
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
    mSmallCleaningIntervalUnit  = settings.getInt(   "SmallUnit",    Calendar.DAY_OF_YEAR);
    mSmallCleaningIntervalValue = settings.getInt(   "SmallValue",   2);
    mBigCleaningIntervalUnit    = settings.getInt(   "BigUnit",      Calendar.WEEK_OF_YEAR);
    mBigCleaningIntervalValue   = settings.getInt(   "BigValue",     4);
    mNextReminderString         = settings.getString("NextReminder", getString(R.string.next_reminder_default_text));
   
    mNumberPicker.setValue(mSmallCleaningIntervalValue);
   
    mNextReminderTextView.setText(mNextReminderString);
    
    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) mUnitSpinner.getAdapter();
    String selectedItem = mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit);
    int selectedItemNr = adapter.getPosition(selectedItem);
    mUnitSpinner.setSelection(selectedItemNr);
    
    
  }
  
  @Override
  protected void onPause()
  {
    super.onPause();
    
    SharedPreferences settings = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putInt(   "SmallUnit",    mSmallCleaningIntervalUnit);
    editor.putInt(   "SmallValue",   mSmallCleaningIntervalValue);
    editor.putInt(   "BigUnit",      mBigCleaningIntervalUnit);
    editor.putInt(   "BigValue",     mBigCleaningIntervalValue);     
    editor.putString("NextReminder", mNextReminderString);
    editor.commit();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
  
  public void onDoneButtonPressed(View view)
  {
    cancelAlert();
    NotificationManager nm = new NotificationManager(this);
    nm.clearNotification();
    setAlert();
  }
  
  public void onStopButtonPressed(View view)
  {
    cancelAlert();
    NotificationManager nm = new NotificationManager(this);
    nm.clearNotification();
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

  protected void setAlert()
  {
    Intent intent = new Intent(this, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);
      
    StringBuilder alertTimeStringBuilder = new StringBuilder();
    
    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    long alarmTimeInElapsedRealtime = AlarmTimeCalculator.getAlarmTime(mSmallCleaningIntervalUnit, mSmallCleaningIntervalValue, alertTimeStringBuilder);
    alarm.set(AlarmManager.ELAPSED_REALTIME, alarmTimeInElapsedRealtime, pendingServiceIntent);
    
    updateNextReminderString(alertTimeStringBuilder.toString());
    
    CharSequence text = getString(R.string.toast_message_pre)
        + " " + String.valueOf(mSmallCleaningIntervalValue)
        + " " + mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit)
        + " " + getString(R.string.toast_message_post);    
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
  }
  
  protected void cancelAlert()
  {
    // TODO: Don't instantiate twice...
    Intent intent = new Intent(this, HasenTagService.class);   
    PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);
      
    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    alarm.cancel(pendingServiceIntent);
    
    updateNextReminderString(getString(R.string.next_no_reminder_text));
  }
  
  protected void updateNextReminderString(String nextReminderString)
  {
    mNextReminderString = nextReminderString;
    mNextReminderTextView.setText(mNextReminderString);
  }
}
