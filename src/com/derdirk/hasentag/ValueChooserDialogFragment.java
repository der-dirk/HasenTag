package com.derdirk.hasentag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ValueChooserDialogFragment extends DialogFragment implements OnShowListener, OnKeyListener
{
  /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event call-backs.
   * The method passes the DialogFragment in case the host needs to query it. */
  public interface ValueChooserClient
  {
    public void onValueSelected(DialogFragment dialog, int value);
    public int  provideInitialValue(DialogFragment dialog);
  }
  
  // Use this instance of the interface to deliver action events
  ValueChooserClient mClient;
  
  protected int      mInitialValue =    0;
  
  // Override the Fragment.onAttach() method to instantiate the UnitChooserDialogListener
  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    
    // Verify that the host activity implements the callback interface
    try
    {
      mClient = (ValueChooserClient) activity;
    }
    catch (ClassCastException e)
    {
      throw new ClassCastException(activity.toString() + " must implement ValueChooserClient");
    }
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
    LayoutInflater      inflater = getActivity().getLayoutInflater();

    builder.setView(inflater.inflate(R.layout.value_chooser, null))
           .setTitle(R.string.valuechooser_title)
           .setPositiveButton(R.string.valuechooser_ok_text, new DialogInterface.OnClickListener()
             {
               public void onClick(DialogInterface dialogInterface, int id)
               {
                 commitValue((AlertDialog)dialogInterface);
               }
             })
           .setNegativeButton(R.string.valuechooser_cancel_text, new DialogInterface.OnClickListener()
             {
               public void onClick(DialogInterface dialogInterface, int id)
               {}
             });
    
    Dialog dialog = builder.create();
    dialog.setOnShowListener(this);
    return dialog; 
  }

  @Override
  public void onShow(DialogInterface dialogInterface)
  {
    // Get the current value of the interval
    mInitialValue = mClient.provideInitialValue(this);
    
    Dialog dialog = (Dialog)dialogInterface;
    
    // Setup the value edit
    EditText valueEdit = (EditText) dialog.findViewById(R.id.value_edit);
    valueEdit.setText(Integer.toString(mInitialValue));
    valueEdit.selectAll();
    valueEdit.setOnKeyListener(this);
    
    showSoftKeyboard(dialog);
  }
  
  // Listener callback for the EditText
  @Override
  public boolean onKey(View view, int keyCode, KeyEvent event)
  {
    if ((event.getAction() == KeyEvent.ACTION_DOWN ) && (keyCode == KeyEvent.KEYCODE_ENTER))
    {
      Dialog dialog = getDialog();         
      commitValue(dialog);
      dialog.cancel();
      return true;
    }
    
    return false;
  }

  protected void commitValue(Dialog dialog)
  {
    EditText valueEdit = (EditText) dialog.findViewById(R.id.value_edit);
    
    // Try to parse the edit text as a number and commit if possible
    try
    {
      int newValue = Integer.parseInt(valueEdit.getText().toString());
      mClient.onValueSelected(ValueChooserDialogFragment.this, newValue);
    }
    catch (NumberFormatException e)
    {}
  }
  
  protected void showSoftKeyboard(Dialog dialog)
  {
    InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
  }
}
