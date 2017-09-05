package com.sensei.companion.communication.connection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.PopupWindow;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.communication.messages.ProgramInfoMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.activities.PcManager;
import com.sensei.companion.display.activities.TouchBarActivity;
import com.sensei.companion.display.screen_selector.ScreenSelectorFragment;
import com.sensei.companion.proto.ProtoMessage;

import java.lang.ref.WeakReference;

public class MessageHandler extends Handler {
    static final int INIT_TOUCHBAR = 0;
    static final int NEW_PROGRAM_INFO = 1;
    static final String PROGRAM_INFO_MESSAGE = "program_info_message";

    private static CommandsData.Program currentProgram;
    public static WeakReference<? extends Activity> curActivity;

    MessageHandler (PcManager activity) {
        curActivity = new WeakReference<Activity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == INIT_TOUCHBAR) {
            PcManager activity = (PcManager)curActivity.get();
            Intent i = new Intent (activity, TouchBarActivity.class);
            activity.startActivity (i);
            curActivity = null;
        }
        else if (msg.what == NEW_PROGRAM_INFO) { //For NEW_PROGRAM_INFO messages in the form "[Program]"
            Bundle messageBundle = msg.getData();
            try {
                ProtoMessage.CommMessage message = ProtoMessage.CommMessage.parseFrom(messageBundle.getByteArray(PROGRAM_INFO_MESSAGE));
                ProgramInfoMessage programInfoMessage = new ProgramInfoMessage(message);
                ScreenSelectorFragment.setCurrentScreenNew(programInfoMessage);
            } catch (InvalidProtocolBufferException e) {
                Log.e (AppLauncher.DEBUG_TAG, "[ConnectManager] Error parsing new program info message bytes");
            }
        }
    }

    public static CommandsData.Program getCurrentProgram() {
        return currentProgram;
    }

    public static void setCurrentProgram(CommandsData.Program program) {
        currentProgram = program;
    }

    PopupWindow getPopUpWindow () {
        return ((PcManager)curActivity.get()).getConnectPopupWindow();
    }

    public static void setActivityReference (Activity activity) {
        curActivity = new WeakReference<>(activity);
    }
}
