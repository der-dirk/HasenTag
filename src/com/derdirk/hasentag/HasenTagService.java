package com.derdirk.hasentag;
import android.app.IntentService;
import android.content.Intent;


public class HasenTagService extends IntentService
{
  public HasenTagService()
  {
    super("HasenTagService");
  }

  @Override
  protected void onHandleIntent(Intent workIntent)
  {
     NotificationManager.setNotification(this);
  }
}
