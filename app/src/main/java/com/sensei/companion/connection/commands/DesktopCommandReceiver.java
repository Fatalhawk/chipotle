package com.sensei.companion.connection.commands;

import android.os.Handler;
import android.util.Log;

import com.sensei.companion.connection.ConnectManager;
import com.sensei.companion.display.TouchBarActivity;

class DesktopCommandReceiver extends CommandReceiver<DesktopCommandReceiver.DesktopCommand>{

    private final String DEBUG_TAG = "appMonitor";

    enum DesktopCommand {
        BRIGHTNESS_UP, BRIGHTNESS_DOWN, VOLUME_UP, VOLUME_DOWN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command) {
        doCommand (mHandler, Enum.valueOf (DesktopCommand.class, command));
    }

    @Override
    public void doCommand (Handler mHandler, DesktopCommand command) {
        TouchBarActivity mActivity = ConnectManager.MessageHandler.mTouchBarActivity.get();
        //TODO: add command executions
        switch (command) {
            case BRIGHTNESS_UP:
                //
                break;
            case BRIGHTNESS_DOWN:
                //
                break;
            case VOLUME_UP:
                //
                break;
            case VOLUME_DOWN:
                //
                break;
            default:
                Log.d (DEBUG_TAG, "Unexpected desktop command");
                break;
        }
    }
}
