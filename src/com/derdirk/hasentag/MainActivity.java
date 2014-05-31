package com.derdirk.hasentag;

import java.util.Calendar;
import java.util.Date;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.derdirk.hasentag.UnitChooserDialogFragment.UnitChooserDialogListener;
import com.derdirk.hasentag.ValueChooserDialogFragment.ValueChooserClient;

public class MainActivity extends    FragmentActivity
                          implements /*OnItemSelectedListener, */
                                     /*OnValueChangeListener,*/ 
                                     UnitChooserDialogListener, 
                                     ValueChooserClient,
                                     OnClickListener
{
  protected int    mSmallCleaningIntervalUnit  = Calendar.SECOND;
  protected int    mSmallCleaningIntervalValue = 5;
  protected int    mBigCleaningIntervalUnit    = Calendar.WEEK_OF_YEAR;
  protected int    mBigCleaningIntervalValue   = 4;
  protected long   mReferenceTimeMs            = 0;
  protected long   mAlertTimeMs                = 0;
  
//  protected NumberPicker              mNumberPicker          = null;
//  protected Spinner                   mUnitSpinner           = null;
  protected TextView                  mValueLabelTextView    = null;
  protected TextView                  mUnitLabelTextView     = null;
  protected TextView                  mNextReminderTextView  = null;
  protected UnitToResourceMapping     mUnitToResourceMapping = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
//    mNumberPicker          = (NumberPicker) findViewById(R.id.number_picker);
    mValueLabelTextView    = (TextView)     findViewById(R.id.value_label_text_view);
//    mUnitSpinner           = (Spinner)      findViewById(R.id.small_unit_spinner);
    mUnitLabelTextView     = (TextView)     findViewById(R.id.unit_label_text_view);
    mNextReminderTextView  = (TextView)     findViewById(R.id.next_reminder_text_view);
    mUnitToResourceMapping = new UnitToResourceMapping(this);
    
//    mNumberPicker.setOnValueChangedListener(this);
    mValueLabelTextView.setOnClickListener(this);
//    mUnitSpinner.setOnItemSelectedListener(this);
    mUnitLabelTextView.setOnClickListener(this);
    
//    // Create an ArrayAdapter using the string array and a default spinner layout
//    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.units_array, android.R.layout.simple_spinner_item);
//    // Specify the layout to use when the list of choices appears
//    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    // Apply the adapter to the spinner
//    mUnitSpinner.setAdapter(adapter);
  }

  @Override
  protected void onResume()
  {
    super.onResume();

    // Restore preferences
    SharedPreferences settings = getSharedPreferences("HasenTag", MODE_PRIVATE);
    mSmallCleaningIntervalUnit  = settings.getInt( "SmallUnit",     Calendar.DAY_OF_YEAR);
    mSmallCleaningIntervalValue = settings.getInt( "SmallValue",    2);
    mBigCleaningIntervalUnit    = settings.getInt( "BigUnit",       Calendar.WEEK_OF_YEAR);
    mBigCleaningIntervalValue   = settings.getInt( "BigValue",      4);
    mReferenceTimeMs            = settings.getLong("ReferenceTime", 0);
    mAlertTimeMs                = settings.getLong("AlertTime",     0);
    
    // Init interval picker
//    mNumberPicker.setValue(mSmallCleaningIntervalValue);
   
//    // Init unit picker
//    @SuppressWarnings("unchecked")
//    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) mUnitSpinner.getAdapter();
//    String selectedItem = mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit);
//    int selectedItemNr = adapter.getPosition(selectedItem);
//    mUnitSpinner.setSelection(selectedItemNr);
    mUnitLabelTextView.setText(mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit));
    
    // Update reminder text
    updateNextAlertText();
  }
  
  @Override
  protected void onPause()
  {
    super.onPause();
    
    // Save preferences
    SharedPreferences settings = getSharedPreferences("HasenTag", MODE_PRIVATE);
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
    AlertManager.cancelAlert(this);
    NotificationManager.clearNotification(this);
    
    if (Calendar.getInstance().getTimeInMillis() < mAlertTimeMs)
      Log.d("MainActivity", "Reset reference time due to early 'done'");
    
    // Reset reference time if the done button was pressed before an alert was issued
    // or if the reference time was not set yet
    if (mReferenceTimeMs == 0 || Calendar.getInstance().getTimeInMillis() < mAlertTimeMs)
      mReferenceTimeMs = AlarmTimeCalculator.getReferenceTime(Calendar.getInstance().getTimeInMillis(), mSmallCleaningIntervalUnit);
      
    mAlertTimeMs = AlarmTimeCalculator.getAlarmTime(mReferenceTimeMs, mSmallCleaningIntervalUnit, mSmallCleaningIntervalValue);
    
    AlertManager.setAlert(this, mAlertTimeMs);
    
    updateNextAlertText();
  }
  
  public void onStopButtonPressed(View view)
  {
    mReferenceTimeMs = 0;
    mAlertTimeMs     = 0;
    
    AlertManager.cancelAlert(this);
    NotificationManager.clearNotification(this);
    
    updateNextAlertText();
  }

//  // Number picker callback
//  @Override
//  public void onValueChange(NumberPicker picker, int oldVal, int newVal)
//  {
//    mSmallCleaningIntervalValue = newVal;
//  }
  
//  // Spinner callbacks 
//  @Override
//  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
//  {
//    mSmallCleaningIntervalUnit = mUnitToResourceMapping.getUnit((String)mUnitSpinner.getItemAtPosition(pos));
//  }
  
//  @Override
//  public void onNothingSelected(AdapterView<?> parent)
//  {}
  
  protected void updateNextAlertText()
  {
    if (mAlertTimeMs != 0)
      mNextReminderTextView.setText(new Date(mAlertTimeMs).toString());
    else
      mNextReminderTextView.setText(getString(R.string.next_no_reminder_text));
  }

  protected void showValueChooser()
  {
    // Create and show the dialog.
    DialogFragment newFragment = new ValueChooserDialogFragment();
    newFragment.show(getSupportFragmentManager(), "valuechooser");
  }  
  
  protected void showUnitChooser()
  {
    // Create and show the dialog.
    DialogFragment newFragment = new UnitChooserDialogFragment();
    newFragment.show(getSupportFragmentManager(), "unitchooser");
  }
  
  // Value chooser callback
  @Override
  public int provideInitialValue(DialogFragment dialog)
  {
    return mSmallCleaningIntervalValue;
  }
  
  // Value chooser callback
  @Override
  public void onValueSelected(DialogFragment dialog, int value)
  {
    mSmallCleaningIntervalValue = value;
    mValueLabelTextView.setText(Integer.toString(mSmallCleaningIntervalValue));
    
  }
  
  // Unit chooser callback
  @Override
  public void onUnitSelected(DialogFragment dialog, int which)
  {
    switch (which) // TODO: Make this more generic
    {
      case 0: mSmallCleaningIntervalUnit = Calendar.SECOND; break;
      case 1: mSmallCleaningIntervalUnit = Calendar.DAY_OF_YEAR; break;
      case 2: mSmallCleaningIntervalUnit = Calendar.WEEK_OF_YEAR; break;
    }
    mUnitLabelTextView.setText(mUnitToResourceMapping.getResource(mSmallCleaningIntervalUnit));
  }

  @Override
  public void onClick(View v)
  {
    if (v.getId() == R.id.unit_label_text_view)
      showUnitChooser();
    else if (v.getId() == R.id.value_label_text_view)
      showValueChooser();
  }  
}
