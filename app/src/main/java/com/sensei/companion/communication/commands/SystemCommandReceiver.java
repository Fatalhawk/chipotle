package com.sensei.companion.communication.commands;

import android.os.Handler;
import android.util.Log;

import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.activities.TouchBarActivity;

class SystemCommandReceiver extends CommandReceiver<SystemCommandReceiver.SystemCommand>{

    enum SystemCommand {
        CLOSE, OPEN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command, String extraInfo) {
        doCommand (mHandler, Enum.valueOf(SystemCommand.class, command), extraInfo);
    }

    @Override
    public void doCommand (Handler mHandler, SystemCommand command, String extraInfo) {
        TouchBarActivity mActivity = (TouchBarActivity) MessageHandler.curActivity.get();
        //TODO: add command executions
        switch (command) {
            case CLOSE:
                break;
            case OPEN:
                break;
            default:
                Log.d (AppLauncher.DEBUG_TAG, "[DesktopCommandReceiver] Unexpected desktop command");
                break;
        }
    }
}
