package com.sensei.companion.communication.connection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.sensei.companion.communication.messages.CMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.activities.PcManager;

public class ConnectManager {
    private PcManager pcManager;
    private static ConnectService mService;
    private boolean isBound = false;

    public void initConnection (Context context, PcManager pcManager) {
        this.pcManager = pcManager;
        Intent intent = new Intent (context, ConnectService.class);
        context.bindService (intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public static void sendMessageToPC (CMessage message) {
        if (mService != null)
            mService.sendMessageToPC(message);
        else {
            Log.d (AppLauncher.DEBUG_TAG, "[ConnectManager] Error sending message to pc");
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectService.MyLocalBinder binder = (ConnectService.MyLocalBinder) service;
            mService = binder.getService ();
            isBound = true;
            Log.i (AppLauncher.DEBUG_TAG, "[ConnectManager] Connection service bound");
            mService.init (new MessageHandler(pcManager));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
