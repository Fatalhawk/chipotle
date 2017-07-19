package com.sensei.companion.connection.commands;

import android.util.Log;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class CommandsData {

    private final String DEBUG_TAG = "appMonitor";
    private Hashtable<Program, Class<? extends CommandReceiver>> programEnums = new Hashtable<>();

    public CommandsData () {
        programEnums.put (Program.UNSUPPORTED, DesktopCommandReceiver.class);
        programEnums.put (Program.WINDOWS, WindowsCommandReceiver.class);
    }

    private enum Program {
        WINDOWS, UNSUPPORTED, MICROSOFT_WORD, MICROSOFT_EXCEL, MICROSOFT_POWERPOINT, MICROSOFT_OUTLOOK,
        SPOTIFY, PHOTOSHOP, ADOBE_ACROBT, SKYPE, CONTROL_PANEL, FILE_EXPLORER, NOTEPAD,
        MICROSOFT_PAINT, MICROSOFT_PAINT3D, MICROSOFT_PHOTOS, CORTANA, CHROME, FIREFOX,
        MICROSOFT_EDGE
    }

    /////////////////////////////////// Utility Methods /////////////////////////////////////////

    /*
    For messages in the form "[environment] [command]"
     */
    public void handleCommand (String message) {
        StringTokenizer st = new StringTokenizer (message);
        try {
            Program environment = Enum.valueOf(Program.class, st.nextToken());
            Class <? extends CommandReceiver> receiver = programEnums.get(environment).asSubclass(CommandReceiver.class);
            try {
                receiver.newInstance().interpretCommand(st.nextToken());
            } catch (Exception e) {
                Log.e (DEBUG_TAG, "Error creating instance", e);
            }
        } catch (NoSuchElementException e) {
            Log.e (DEBUG_TAG, "Error with message: " + message, e);
        }
    }
}