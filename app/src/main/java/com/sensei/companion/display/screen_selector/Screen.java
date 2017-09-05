package com.sensei.companion.display.screen_selector;

import android.util.Log;
import android.widget.Button;
import android.graphics.Bitmap;

import com.sensei.companion.communication.commands.CommandsData;
import com.sensei.companion.display.activities.AppLauncher;

public class Screen {
    private String name;
    private Bitmap screenImage;
    private int programId;
    private CommandsData.Program program;
    static final String DESKTOP_NAME_PREFIX = "Screen ";

    Screen(String name, int programId, Bitmap screenImage){
        this.name = name;
        this.programId = programId;
        this.screenImage = screenImage;
        try {
            this.program = CommandsData.Program.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.program = CommandsData.Program.UNSUPPORTED;
            //Log.d (AppLauncher.DEBUG_TAG, "[Screen] error getting Program enum from name " + name);
        }
    }

    public String getName(){
        return name;
    }

    public int getProgramId(){
        return programId;
    }

    public Bitmap getScreenImage(){
        return screenImage;
    }

    public CommandsData.Program getProgram (){
        return program;
    }
}
