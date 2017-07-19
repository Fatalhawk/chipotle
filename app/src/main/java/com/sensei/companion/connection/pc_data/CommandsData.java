package com.sensei.companion.connection.pc_data;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class CommandsData {

    private Hashtable<Program, Class<? extends Enum>> programEnums = new Hashtable<>();

    public CommandsData () {
        programEnums.put (Program.UNSUPPORTED, WindowsCommand.class);
        programEnums.put (Program.MICROSOFT_WORD, WordCommand.class);
    }

    public enum Program {
        UNSUPPORTED, MICROSOFT_WORD, MICROSOFT_EXCEL, MICROSOFT_POWERPOINT, MICROSOFT_OUTLOOK,
        SPOTIFY, PHOTOSHOP, ADOBE_ACROBT, SKYPE, CONTROL_PANEL, FILE_EXPLORER, NOTEPAD,
        MICROSOFT_PAINT, MICROSOFT_PAINT3D, MICROSOFT_PHOTOS, CORTANA, CHROME, FIREFOX,
        MICROSOFT_EDGE
    }

    ////////////////////////////////// Enums: Program Commands ///////////////////////////////////

    public enum WindowsCommand {
        CLOSE, OPEN, VOLUME, BRIGHTNESS, BRIGHTNESS_UP, BRIGHTNESS_DOWN, VOLUME_UP, VOLUME_DOWN,
        MUTE
    }

    public enum WordCommand {

    }

    /////////////////////////////////// Utility Methods /////////////////////////////////////////

    /*
    For messages in the form "[environment] [command]"
     */
    public void interpretMessage (String message) {
        StringTokenizer st = new StringTokenizer (message);
        String environment =
    }

    public interface AcknowledgeCommand <T extends Enum<T>> {
        public abstract void doCommand (T command);
    }


}
