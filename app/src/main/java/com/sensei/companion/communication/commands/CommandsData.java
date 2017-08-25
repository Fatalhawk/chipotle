package com.sensei.companion.communication.commands;

import android.util.Log;

import com.sensei.companion.communication.connection.MessageHandler;
import com.sensei.companion.communication.messages.CommandMessage;
import com.sensei.companion.display.activities.AppLauncher;
import com.sensei.companion.proto.ProtoMessage;

import java.util.Hashtable;

public class CommandsData {

    private static Hashtable<Program, Class<? extends CommandReceiver>> programEnums = new Hashtable<>();

    static {
        programEnums.put (Program.SYSTEM, SystemCommandReceiver.class);
        programEnums.put (Program.WINDOWS, DesktopCommandReceiver.class); //TODO: maybe change
        programEnums.put (Program.UNSUPPORTED, DesktopCommandReceiver.class); //TODO: maybe change
    }

    public enum Program {
        SYSTEM, WINDOWS, UNSUPPORTED, MICROSOFT_WORD, MICROSOFT_EXCEL, MICROSOFT_POWERPOINT, MICROSOFT_OUTLOOK,
        SPOTIFY, PHOTOSHOP, ADOBE_ACROBT, SKYPE, CONTROL_PANEL, FILE_EXPLORER, NOTEPAD,
        MICROSOFT_PAINT, MICROSOFT_PAINT3D, MICROSOFT_PHOTOS, CORTANA, CHROME, FIREFOX,
        MICROSOFT_EDGE
    }

    /////////////////////////////////// Utility Methods /////////////////////////////////////////

    /*
    Takes the received command message and generically redirects it to the appropriate CommandReceiver subclass
     */
    public static void handleCommand (MessageHandler mHandler, CommandMessage commandMessage) {
        String command = commandMessage.getCommand();
        String extra_info = commandMessage.getExtraInfo();
        ProtoMessage.CommMessage.Command.CommandEnvironment environment = commandMessage.getEnvironment();

        Program commandEnvironment;
        switch (environment) {
            case PROGRAM:
                commandEnvironment = mHandler.getCurrentProgram();
                break;
            case SYSTEM:
                commandEnvironment = Program.SYSTEM;
                break;
            case WINDOWS:
                commandEnvironment = Program.WINDOWS;
                break;
            default:
                Log.d (AppLauncher.DEBUG_TAG, "[CommandsData] unexpected environment in received command");
                return;
        }

        Class <? extends CommandReceiver> receiver = programEnums.get(commandEnvironment).asSubclass(CommandReceiver.class);
        try {
            receiver.newInstance().interpretCommand(mHandler, command, extra_info);
        }catch (InstantiationException|IllegalAccessException e) {
            Log.e(AppLauncher.DEBUG_TAG, "[CommandsData] Error creating instance of receiver class", e);
        }
    }
}