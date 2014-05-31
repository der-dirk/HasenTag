package com.derdirk.hasentag;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class UnitChooserDialogFragment extends DialogFragment
{
  /* The activity that creates an instance of this dialog fragment must
   * implement this interface in order to receive event call-backs.
   * The method passes the DialogFragment in case the host needs to query it. */
  public interface UnitChooserDialogListener
  {
      public void onUnitSelected(DialogFragment dialog, int which);
  }
  
  // Use this instance of the interface to deliver action events
  UnitChooserDialogListener mListener;

  // Override the Fragment.onAttach() method to instantiate the UnitChooserDialogListener
  @Override
  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    
    // Verify that the host activity implements the callback interface
    try
    {
      // Instantiate the NoticeDialogListener so we can send events to the host
      mListener = (UnitChooserDialogListener) activity;
    }
    catch (ClassCastException e)
    {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString() + " must implement UnitChooserDialogListener");
    }
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState)
  {
    // Use the Builder class for convenient dialog construction
    AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.unitchooser_title)
           .setItems(R.array.units_array, new DialogInterface.OnClickListener()
           {
             public void onClick(DialogInterface dialog, int which)
             {
               mListener.onUnitSelected(UnitChooserDialogFragment.this, which);
             }
           });
    
    // Create the AlertDialog object and return it
    return builder.create();
  }
}
