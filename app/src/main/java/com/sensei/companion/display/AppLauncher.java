package com.sensei.companion.display;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;


import android.util.Log;

public class AppLauncher extends AppCompatActivity {
    private final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_launcher);

        //print network stuff

        Log.i (DEBUG_TAG, command.toString());
        Log.i (DEBUG_TAG, "" + (Command.valueOf(command.toString()) == Command.OPEN));



        //startProgram ();
    }

    public void startProgram () {
        Intent i = new Intent(this, PcManager.class);
        startActivity(i);
    }
}
