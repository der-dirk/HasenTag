package com.derdirk.hasentag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;

public class ValueChooserDialogFragment extends DialogFragment implements OnShowListener
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
      // Instantiate the NoticeDialogListener so we can send events to the host
      mClient = (ValueChooserClient) activity;
    }
    catch (ClassCastException e)
    {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() + " must implement ValueChooserDialogListener and ValueChooserClient");
    }
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
    LayoutInflater      inflater = getActivity().getLayoutInflater();

    builder.setView(inflater.inflate(R.layout.value_chooser, null))
           .setTitle(R.string.valuechooser_title)
           .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialogInterface, int id)
             {
               AlertDialog dialog = (AlertDialog)dialogInterface;
               EditText valueEdit = (EditText) dialog.findViewById(R.id.value_edit);
               mClient.onValueSelected(ValueChooserDialogFragment.this, Integer.parseInt(valueEdit.getText().toString()));
             }
           });
    
    // Create the AlertDialog object and return it
    
    Dialog dialog = builder.create();
    dialog.setOnShowListener(this);
    return dialog; 
  }

  @Override
  public void onShow(DialogInterface dialogInterface)
  {    
    int initialValue = mClient.provideInitialValue(this);
    
    Dialog dialog = (Dialog)dialogInterface;
    EditText valueEdit = (EditText) dialog.findViewById(R.id.value_edit);
    valueEdit.setText(Integer.toString(initialValue));
  }
}
