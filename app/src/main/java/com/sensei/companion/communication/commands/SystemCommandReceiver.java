package com.sensei.companion.communication.commands;

import android.os.Handler;
import android.util.Log;

import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.display.activities.TouchBarActivity;
import com.sensei.companion.display.screen_selector.ScreenSelectorFragment;

public class SystemCommandReceiver extends CommandReceiver<SystemCommandReceiver.SystemCommand>{

    public enum SystemCommand {
        CLOSE, OPEN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command, String extraInfo) {
        doCommand (mHandler, Enum.valueOf(SystemCommand.class, command), extraInfo);
    }

    @Override
    public void doCommand (Handler mHandler, final SystemCommand command, final String extraInfo) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (command) {
                    case CLOSE:
                        //the extra info in this case will be the int32 program id
                        ScreenSelectorFragment.removeExistingScreen(Integer.parseInt(extraInfo));
                        break;
                    case OPEN:
                        //the extra info in this case will be the int32 program id
                        ScreenSelectorFragment.setCurrentScreenExisting(Integer.parseInt(extraInfo));
                        break;
                    default:
                        Log.d (AppLauncher.DEBUG_TAG, "[DesktopCommandReceiver] Unexpected desktop command");
                        break;
                }
            }
        });
    }
}
