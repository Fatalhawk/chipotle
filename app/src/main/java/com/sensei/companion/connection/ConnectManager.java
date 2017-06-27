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

import com.sensei.companion.display.PcManager;
import com.sensei.companion.display.TouchBarActivity;

import java.lang.ref.WeakReference;

public class ConnectManager {

    private final String DEBUG_TAG = "appMonitor";
    private static final int EXAMPLE_MESSAGE = 0;
    public static final int INIT_TOUCHBAR = 1;
    private TextView textView;
    private Button button;
    private PcManager pcManager;

    private static ConnectService mService;
    private boolean isBound = false;
    private Context context;

    public void initConnection (Context context, TextView textView, Button button, PcManager pcManager) {
        this.pcManager = pcManager;
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

    public static class MessageHandler extends Handler {
        // TODO: this code will provide access to the Activity with the UI that needs to be updated based on the incoming messages
        private WeakReference<PcManager> mPcManagerActivity = null;
        private static WeakReference<TouchBarActivity> mTouchBarActivity = null;

        MessageHandler (PcManager activity) {
            mPcManagerActivity = new WeakReference<PcManager> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INIT_TOUCHBAR) {
                PcManager activity = mPcManagerActivity.get();
                Intent i = new Intent (activity, TouchBarActivity.class);
                activity.startActivity (i);
                mPcManagerActivity = null;
            }
        }

        public static void setActivityReferenceToTouchBar (TouchBarActivity touchBarActivity) {
            mTouchBarActivity = new WeakReference <TouchBarActivity>(touchBarActivity);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ConnectService.MyLocalBinder binder = (ConnectService.MyLocalBinder) service;
            mService = binder.getService ();
            isBound = true;
            Log.i (DEBUG_TAG, "Connection service bound");
            mService.init (new MessageHandler(pcManager), textView, button);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
