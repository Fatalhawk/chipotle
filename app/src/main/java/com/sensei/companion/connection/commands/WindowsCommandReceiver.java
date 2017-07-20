package com.sensei.companion.connection.commands;

class WindowsCommandReceiver extends CommandReceiver<WindowsCommandReceiver.WindowsCommand>{

    enum WindowsCommand {
        CLOSE, OPEN
    }

    @Override
    public void interpretCommand(String command) {
        doCommand (Enum.valueOf(WindowsCommand.class, command));
    }

    @Override
    public void doCommand (WindowsCommand command) {

    }
}
