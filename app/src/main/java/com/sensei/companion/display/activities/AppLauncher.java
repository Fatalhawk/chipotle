package com.sensei.companion.display.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class AppLauncher extends AppCompatActivity {

    public static final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i (DEBUG_TAG, "App started");

        Intent i = new Intent (this, TouchBarActivity.class);
        startActivity(i);

        //startPcSelection ();
    }

    public void startPcSelection () {
        Intent i = new Intent (this, PcManager.class);
        startActivity (i);
    }
}
