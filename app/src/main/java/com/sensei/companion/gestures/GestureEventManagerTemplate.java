package com.sensei.companion.gestures;

import android.app.IntentService;


import android.view.MotionEvent;
import android.view.View;

public class GestureEventManagerTemplate extends GestureListener {

    public GestureEventManagerTemplate (View v) {
        super (v);
    }

    public boolean onTouchGesture (View v, MotionEvent event) {
        //TODO: detect touch gestures using event data
        return true; //true if the listener has consumed the event, false otherwise.
    }
}
