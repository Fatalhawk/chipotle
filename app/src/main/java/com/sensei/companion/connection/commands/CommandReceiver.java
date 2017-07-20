package com.sensei.companion.connection.commands;

abstract class CommandReceiver <T extends Enum<T>> {
    public abstract void interpretCommand (String command);
    public abstract void doCommand (T command);
}
