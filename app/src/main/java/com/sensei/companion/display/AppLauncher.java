package com.sensei.companion.display;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sensei.companion.R;


import android.util.Log;

public class AppLauncher extends AppCompatActivity {
    private static final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_launcher);

        //print network stuff

        startProgram ();
    }

    public void startProgram () {
        Intent i = new Intent(this, PcManager.class);
        startActivity(i);
    }
}
