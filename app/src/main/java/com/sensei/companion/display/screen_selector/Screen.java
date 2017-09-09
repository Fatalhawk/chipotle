package com.sensei.companion.display.screen_selector;

import android.graphics.Bitmap;

import com.sensei.companion.communication.commands.CommandsData;

class Screen {
    private String name;
    private Bitmap screenImage;
    private int programId;
    private CommandsData.Program program;

    Screen(String name, int programId, String programType, Bitmap screenImage){
        this.name = name;
        this.programId = programId;
        this.screenImage = screenImage;
        try {
            this.program = CommandsData.Program.valueOf(programType);
        } catch (IllegalArgumentException e) {
            this.program = CommandsData.Program.UNSUPPORTED;
            //Log.d (AppLauncher.DEBUG_TAG, "[Screen] error getting Program enum from name " + name);
        }
    }

    public String getName(){
        return name;
    }

    int getProgramId(){
        return programId;
    }

    Bitmap getScreenImage(){
        return screenImage;
    }

    public CommandsData.Program getProgram (){
        return program;
    }
}
