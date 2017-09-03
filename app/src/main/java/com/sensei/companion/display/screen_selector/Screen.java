package com.sensei.companion.display.screen_selector;

import android.widget.Button;
import android.graphics.Bitmap;

public class Screen {
    private String name;
    private Bitmap screenImage;
    public static final String DESKTOP_NAME_PREFIX = "Screen ";

    Screen (String name){
        this.name = name;
    }

    Screen(String name, Bitmap screenImage){
        this.name = name;
        this.screenImage = screenImage;
    }

    public String getName(){
        return this.name;
    }

    public Bitmap getScreenImage(){
        return screenImage;
    }
}
