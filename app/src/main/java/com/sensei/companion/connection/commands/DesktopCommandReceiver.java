package com.sensei.companion.connection.commands;

public class DesktopCommandReceiver extends CommandReceiver<DesktopCommandReceiver.DesktopCommand>{

    enum DesktopCommand {
        BRIGHTNESS_UP, BRIGHTNESS_DOWN, VOLUME_UP, VOLUME_DOWN
    }

    @Override
    public void interpretCommand(String command) {
        doCommand (Enum.valueOf (DesktopCommand.class, command));
    }

    @Override
    public void doCommand (DesktopCommand command) {

    }
}
