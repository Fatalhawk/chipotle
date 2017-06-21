package com.sensei.companion.display;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sensei.companion.R;
import com.sensei.companion.connection.ConnectManager;

public class AppLauncher extends AppCompatActivity {

    private ConnectManager mConnectManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_launcher);

        //mConnectManager = new ConnectManager();
        //mConnectManager.initConnection (this);
    }
}
