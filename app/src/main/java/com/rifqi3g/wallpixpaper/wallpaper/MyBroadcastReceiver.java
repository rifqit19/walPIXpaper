package com.rifqi3g.wallpixpaper.wallpaper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rifqi3g.wallpixpaper.wallpaper.Saved.SaveActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {

    SaveActivity saveActivity = new SaveActivity();

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "test alarm", Toast.LENGTH_LONG).show();
    }
}
