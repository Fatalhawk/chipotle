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

public class AppLauncher extends AppCompatActivity {

    private final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_app_launcher);

        //print network stuff

        startProgram ();
    }

    public void startProgram () {
        Intent i = new Intent (this, PcManager.class);
        startActivity (i);
    }

    public void printNetworkStuff () {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list (nets)) {
                displayInterfaceInformation(netint);
            }
        }
        catch (SocketException e) {
        }
    }

    void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        Log.i(DEBUG_TAG, "Display name: " + netint.getDisplayName() + "\n");
        Log.i(DEBUG_TAG, "Name: " + netint.getName() + "\n");
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            Log.i(DEBUG_TAG, "InetAddress: " + inetAddress + "\n");
            for (InterfaceAddress x: netint.getInterfaceAddresses()) {
                int networkPrefixLength = x.getNetworkPrefixLength();
                Log.i(DEBUG_TAG, "" + networkPrefixLength + "\n");
            }
        }
        Log.i(DEBUG_TAG, "\n \n");
    }
}
