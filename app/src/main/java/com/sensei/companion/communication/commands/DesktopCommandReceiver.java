package com.sensei.companion.communication.commands;

import android.os.Handler;
import android.util.Log;

import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.activities.TouchBarActivity;

class DesktopCommandReceiver extends CommandReceiver<DesktopCommandReceiver.DesktopCommand>{

    enum DesktopCommand {
        VOLUME_UP, VOLUME_DOWN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command, String extraInfo) {
        doCommand (mHandler, Enum.valueOf (DesktopCommand.class, command), extraInfo);
    }

    @Override
    public void doCommand (Handler mHandler, DesktopCommand command, String extraInfo) {
        TouchBarActivity mActivity = (TouchBarActivity) MessageHandler.curActivity.get();
        //TODO: add command executions
        switch (command) {
            case VOLUME_UP:
                //
                break;
            case VOLUME_DOWN:
                //
                break;
            default:
                Log.d (AppLauncher.DEBUG_TAG, "[DesktopCommandReceiver] Unexpected desktop command");
                break;
        }
    }
}
