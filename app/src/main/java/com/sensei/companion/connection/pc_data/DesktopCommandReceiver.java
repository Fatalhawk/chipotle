package com.sensei.companion.connection.pc_data;

public class DesktopCommandReceiver extends CommandReceiver<DesktopCommandReceiver.DesktopCommand>{

    public enum DesktopCommand {
        BRIGHTNESS_UP, BRIGHTNESS_DOWN, VOLUME_UP, VOLUME_DOWN
    }

    @Override
    public void interpretCommand(String command) {

    }

    @Override
    public void doCommand (DesktopCommand command) {

    }
}
