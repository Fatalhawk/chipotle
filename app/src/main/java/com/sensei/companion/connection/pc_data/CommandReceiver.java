package com.sensei.companion.connection.pc_data;

public abstract class CommandReceiver <T extends Enum<T>> {
    public abstract void interpretCommand (String command);
    public abstract void doCommand (T command);
}
