package com.derdirk.hasentag;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnItemSelectedListener, OnEditorActionListener
{
  
  protected int mSmallCleaningIntervalUnit  = Calendar.SECOND;
  protected int mSmallCleaningIntervalValue = 5;
  protected int mBigCleaningIntervalUnit    = Calendar.WEEK_OF_YEAR;
  protected int mBigCleaningIntervalValue   = 4;
  
  protected EditText              mNumberEdit            = null;
  protected Spinner               mUnitSpinner           = null;
  protected UnitToResourceMapping mUnitToResourceMapping = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
    mNumberEdit            = (EditText) findViewById(R.id.number_edit);
    mUnitSpinner           = (Spinner)  findViewById(R.id.small_unit_spinner);
    mUnitToResourceMapping = new UnitToResourceMapping(this);
    
    mNumberEdit.setOnEditorActionListener(this);
    mUnitSpinner.setOnItemSelectedListener(this);
    
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
    mSmallCleaningIntervalUnit  = settings.getInt("SmallUnit",  Calendar.DAY_OF_YEAR);
    mSmallCleaningIntervalValue = settings.getInt("SmallValue", 2);
    mBigCleaningIntervalUnit    = settings.getInt("BigUnit",    Calendar.WEEK_OF_YEAR);
    mBigCleaningIntervalValue   = settings.getInt("BigValue",   4);
   
    mNumberEdit.setText(String.valueOf(mSmallCleaningIntervalValue));    
    
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
    editor.putInt("SmallUnit",  mSmallCleaningIntervalUnit);
    editor.putInt("SmallValue", mSmallCleaningIntervalValue);
    editor.putInt("BigUnit",    mBigCleaningIntervalUnit);
    editor.putInt("BigValue",   mBigCleaningIntervalUnit);      
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

  // EditText callback
  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
  {
    boolean handled = false;
    if (actionId == EditorInfo.IME_ACTION_DONE)
    {
      mSmallCleaningIntervalValue = Integer.valueOf(v.getText().toString()); // Is always a number
      
      InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
      handled = true;
    }
    return handled;
  }
  
  // Spinner callbacks 
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
  {
    mSmallCleaningIntervalUnit = mUnitToResourceMapping.getUnit((String)mUnitSpinner.getItemAtPosition(pos));
  }
  
  public void onNothingSelected(AdapterView<?> parent)
  {}

	protected void setAlert()
	{
	  Intent intent = new Intent(this, HasenTagService.class);   
	  PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);
	    
	  AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	  long alarmTimeInElapsedRealtime = AlarmTimeCalculator.getAlarmTime(mSmallCleaningIntervalUnit, mSmallCleaningIntervalValue);
	  alarm.set(AlarmManager.ELAPSED_REALTIME, alarmTimeInElapsedRealtime, pendingServiceIntent);
	  
	  SparseArray<String> calendarUnit = new SparseArray<String>();
	  calendarUnit.put(Calendar.SECOND,       "Seconds");
	  calendarUnit.put(Calendar.DAY_OF_YEAR , "Days");
	  calendarUnit.put(Calendar.WEEK_OF_YEAR, "Weeks");
	  
	  CharSequence text = "Reminder will show in " + String.valueOf(mSmallCleaningIntervalValue) + " " + calendarUnit.get(mSmallCleaningIntervalUnit);
	  Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	
	 protected void cancelAlert()
	  {
	   // TODO: Don't instantiate twice...
	    Intent intent = new Intent(this, HasenTagService.class);   
	    PendingIntent pendingServiceIntent = PendingIntent.getService(this, 0, intent, 0);
	      
	    AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    alarm.cancel(pendingServiceIntent);
	  }
}
