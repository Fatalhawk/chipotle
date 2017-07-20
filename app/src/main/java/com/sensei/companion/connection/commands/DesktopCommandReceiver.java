package com.sensei.companion.connection.commands;

import android.os.Handler;

class DesktopCommandReceiver extends CommandReceiver<DesktopCommandReceiver.DesktopCommand>{

    enum DesktopCommand {
        BRIGHTNESS_UP, BRIGHTNESS_DOWN, VOLUME_UP, VOLUME_DOWN
    }

    @Override
    public void interpretCommand(Handler mHandler, String command) {
        doCommand (mHandler, Enum.valueOf (DesktopCommand.class, command));
    }

    @Override
    public void doCommand (Handler mHandler, DesktopCommand command) {

    }
}
