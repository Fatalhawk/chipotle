package com.sensei.companion.gestures;


import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String DEBUG_TAG = "appMonitor";

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }
    public boolean onSingleTapConfirmed(MotionEvent event){
        Log.i(DEBUG_TAG, "single tap " + event.toString());
        return false;
    }

    public boolean onDoubleTap(MotionEvent event){
        Log.i(DEBUG_TAG, "double tap " + event.toString());
        return false;
    }

    public void onLongPress(MotionEvent e){
        Log.i(DEBUG_TAG, "long press " + e.toString());
    }
}
