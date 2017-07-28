package com.sensei.companion.connection.commands;

import android.os.Handler;

import com.sensei.companion.connection.ConnectManager;
import com.sensei.companion.display.TouchBarActivity;

class WindowsCommandReceiver extends CommandReceiver<WindowsCommandReceiver.WindowsCommand>{

    enum WindowsCommand {
        CLOSE, OPEN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command) {
        doCommand (mHandler, Enum.valueOf(WindowsCommand.class, command));
    }

    @Override
    public void doCommand (Handler mHandler, WindowsCommand command) {
        TouchBarActivity mActivity = ConnectManager.MessageHandler.mTouchBarActivity.get();
        //TODO: add command executions
    }
}
