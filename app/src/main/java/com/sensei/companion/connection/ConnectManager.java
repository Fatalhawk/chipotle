package com.sensei.companion.connection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class ConnectManager {

    private final String DEBUG_TAG = "appMonitor";
    private static final int EXAMPLE_MESSAGE = 1;
    private TextView textView;
    private Button button;

    private static ConnectService mService;
    private boolean isBound = false;
    private Context context;

    public void initConnection (Context context, TextView textView, Button button) {
        this.textView = textView;
        this.button = button;
        this.context = context;
        Intent intent = new Intent (context, ConnectService.class);
        context.bindService (intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public static void sendMessageToPC (String message) {
        if (mService != null)
            mService.sendMessageToPC(message);
    }

    private static class MessageHandler extends Handler {
        /* TODO: this code will provide access to the Activity with the UI that needs to be updated based on the incoming messages
        private final WeakReference<MyActivity> mActivity;

        MessageHandler (MyActivity activity) {
            mActivity = new WeakReference<MyActivity> (activity);
        } */

        @Override
        public void handleMessage(Message msg) {
            //MyActivity activity = mActivity.get ();

            if (msg.what == EXAMPLE_MESSAGE) {
                //update UI through activity;
            }
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectService.MyLocalBinder binder = (ConnectService.MyLocalBinder) service;
            mService = binder.getService ();
            isBound = true;
            Log.i (DEBUG_TAG, "Connection service bound");
            mService.init (new MessageHandler(), textView, button);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
