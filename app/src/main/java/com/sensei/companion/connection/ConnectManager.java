package com.sensei.companion.connection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

    private static final String DEBUG_TAG = "appMonitor";
    private TextView textView;
    private Button button;
    private PcManager pcManager;
    private static ConnectService mService;
    private boolean isBound = false;

    //PC message subject lines except for INIT_TOUCHBAR which is for Handler's Message.what
    final static int INIT_TOUCHBAR = 0;
    final static int COMPANION_COMMAND = 1;
    final static int NEW_PROGRAM_INFO = 2;
    //final static int COMPANION_DATA_<SUBJECT>

    //Bundle keys for Handler messages
    final static String PROGRAM_INFO = "program_info";
    final static String IMAGE_BYTES = "image_bytes";

    public void initConnection (Context context, TextView textView, Button button, PcManager pcManager) {
        this.pcManager = pcManager;
        this.textView = textView;
        this.button = button;
        Intent intent = new Intent (context, ConnectService.class);
        context.bindService (intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public static void sendMessageToPC (int messageSubject, String messageContent) {
        if (mService != null)
            mService.sendMessageToPC(messageSubject, messageContent);
    }

    public static class MessageHandler extends Handler {
        // TODO: this code will provide access to the Activity with the UI that needs to be updated based on the incoming messages
        private WeakReference<PcManager> mPcManagerActivity = null;
        private static WeakReference<TouchBarActivity> mTouchBarActivity = null;

        MessageHandler (PcManager activity) {
            mPcManagerActivity = new WeakReference<> (activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == INIT_TOUCHBAR) {
                PcManager activity = mPcManagerActivity.get();
                Intent i = new Intent (activity, TouchBarActivity.class);
                activity.startActivity (i);
                mPcManagerActivity = null;
            }
            else if (msg.what == NEW_PROGRAM_INFO) { //For NEW_PROGRAM_INFO messages in the form "[Program]"
                Bundle messageBundle = msg.getData();
                byte [] programInfoBytes = messageBundle.getByteArray (PROGRAM_INFO);
                byte [] imageBytes = messageBundle.getByteArray (IMAGE_BYTES);
                if (programInfoBytes != null && imageBytes != null) {
                    String programInfo = new String(programInfoBytes);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    //TODO: SEND THE PROGRAM INFO AND IMAGE BITMAP TO THE TOUCH_BAR_ACTIVITY
                }
                else {
                    Log.d (DEBUG_TAG, "NEW_PROGRAM_INFO NULL ERROR IN HANDLER MESSAGE");
                }
            }
        }

        public static void setActivityReferenceToTouchBar (TouchBarActivity touchBarActivity) {
            mTouchBarActivity = new WeakReference <>(touchBarActivity);
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
