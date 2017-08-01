package com.sensei.companion.display;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class PcManager extends AppCompatActivity {

    private ConnectManager mConnectManager;
    private final String DEBUG_TAG = "appMonitor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i (DEBUG_TAG, "hi");
        setContentView(R.layout.activity_pc_manager);

        connect();
    }

    private void connect () {
        ConnectManager mConnectManager;
        mConnectManager = new ConnectManager();
        mConnectManager.initConnection (this, (TextView) findViewById(R.id.searchStatus), (Button) findViewById(R.id.connectButton), this);
    }
}
