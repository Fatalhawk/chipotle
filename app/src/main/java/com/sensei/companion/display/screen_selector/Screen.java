package com.sensei.companion.display.screen_selector;
import android.widget.Button;
import android.graphics.Bitmap;
/**
 * Created by rojigangengatharan on 2017-08-05.
 */

public class Screen {
    private String name;
    //image buttons for now later should be bitmaps of images instead
    private Button button;

    private Bitmap bitmap;

    protected static final String DESKTOP_NAME_PREFIX = "Formal";


    public Screen(String namep, Button buttonp){
        this.name = namep;
        this.button = buttonp;



    }
    public Screen(String namep, Button buttonp, Bitmap bitmapp){
        this.name = namep;
        this.button = buttonp;
        this.bitmap = bitmapp;
    }

    public String getName(){
        return this.name;
    }

    public Button getButton(){
        return this.button;
    }




}
