package com.sensei.companion.display;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AppLauncher extends AppCompatActivity {

    public static final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startPcSelection ();
    }

    public void startPcSelection () {
        Intent i = new Intent (this, PcManager.class);
        startActivity (i);
    }
}
