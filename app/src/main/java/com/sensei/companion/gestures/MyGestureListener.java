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
        return true;
    }

    public boolean onDoubleTap(MotionEvent event){
        Log.i(DEBUG_TAG, "double tap " + event.toString());
        return true;
    }

    public void onLongPress(MotionEvent e){
        Log.i(DEBUG_TAG, "long press " + e.toString());
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        Log.i(DEBUG_TAG, "scroll " + e1.toString() + "    " + e2.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        return true;
    }
}
